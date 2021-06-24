package nl.rijksoverheid.dcbs.verifier.ui.scanner

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.GsonBuilder
import nl.rijksoverheid.ctr.shared.ext.findNavControllerSafety
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.FragmentScanResultValidBinding
import nl.rijksoverheid.dcbs.verifier.models.*
import nl.rijksoverheid.dcbs.verifier.persistance.PersistenceManager
import nl.rijksoverheid.dcbs.verifier.ui.scanner.models.ScanResultValidData
import nl.rijksoverheid.dcbs.verifier.ui.scanner.utils.ScannerUtil
import nl.rijksoverheid.dcbs.verifier.utils.formatDate
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class ScanResultValidFragment : Fragment(R.layout.fragment_scan_result_valid) {

    private var _binding: FragmentScanResultValidBinding? = null
    private val binding get() = _binding!!

    private val args: ScanResultValidFragmentArgs by navArgs()
    private val scannerUtil: ScannerUtil by inject()
    private val persistenceManager: PersistenceManager by inject()

    private var countDownTime = COUNTDOWN_TIME
    private val autoCloseHandler = Handler(Looper.getMainLooper())
    private val autoCloseRunnable = Runnable {
        setPauseTimer()
    }

    @ExperimentalStdlibApi
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentScanResultValidBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(ScanResultValidFragmentDirections.actionNavMain())
        }

        when (args.validData) {
            is ScanResultValidData.Demo -> {
                binding.title.text = getString(R.string.scan_result_demo_title)
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey_2
                    )
                )
            }
            is ScanResultValidData.Valid -> {
                binding.title.text = getString(R.string.scan_result_valid_title)
            }
        }

        binding.button.setOnClickListener {
            scannerUtil.launchScanner(requireActivity())
        }

        presentPersonalDetails()
        initCountries()
        setPauseTimer()
        binding.btnPause.setOnClickListener {
            if (binding.pauseLabel.text == getString(R.string.pause)) {
                binding.pauseLabel.text = getString(R.string.resume)
                autoCloseHandler.removeCallbacks(autoCloseRunnable)
            } else {
                binding.pauseLabel.text = getString(R.string.pause)
                setPauseTimer()
            }
        }
    }

    @ExperimentalStdlibApi
    private fun presentPersonalDetails() {
        val verifiedQr = args.validData.verifiedQr
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        val dccQR = gson.fromJson(verifiedQr.data, DCCQR::class.java)
        binding.name.text = dccQR.getName()
        binding.dateOfBirth.text = getString(R.string.item_date_of_birth_x, dccQR.getBirthDate().formatDate())
        initVaccinations(dccQR.dcc?.vaccines)
        initTest(dccQR.dcc?.tests)
        initRecovery(dccQR.dcc?.recoveries)
    }

    @SuppressLint("SetTextI18n")
    private fun initVaccinations(items: List<DCCVaccine>?) {

        binding.dose1TableTitle.text = getString(R.string.item_dose_x, 1)
        binding.dose2TableTitle.text = getString(R.string.item_dose_x, 2)
        items?.let { vaccines ->
            binding.vaccineLayout.visibility = View.VISIBLE
            binding.dose1BoxTitle.text = getString(R.string.item_dose_x_x, vaccines[0].doseNumber, vaccines[0].totalSeriesOfDoses)
            binding.dose1BoxName.text = vaccines[0].getVaccineProduct()?.getDisplayName() ?: ""
            binding.dose1BoxDate.text = vaccines[0].dateOfVaccination.formatDate()
            binding.dose1TableDiseaseVaccineValue.text =
                "${vaccines[0].getTargetedDisease()?.getDisplayName() ?: ""} | ${vaccines[0].getVaccine()?.getDisplayName() ?: ""}"
            binding.dose1TableMemberStateValue.text = vaccines[0].countryOfVaccination
            binding.dose1TableIssuerValue.text = vaccines[0].certificateIssuer
            binding.dose1TableCertificateIdentifierValue.text = vaccines[0].certificateIdentifier
            if (vaccines.size == 2) {
                binding.dose1TableTitle.visibility = View.VISIBLE
                binding.dose2BoxLayout.visibility = View.VISIBLE
                binding.dose2TableLayout.visibility = View.VISIBLE
                binding.dose2BoxTitle.text = getString(R.string.item_dose_x_x, vaccines[1].doseNumber, vaccines[1].totalSeriesOfDoses)
                binding.dose2BoxName.text = vaccines[1].getVaccineProduct()?.getDisplayName() ?: ""
                binding.dose2BoxDate.text = vaccines[1].dateOfVaccination.formatDate()
                binding.dose2TableDiseaseVaccineValue.text =
                    "${vaccines[1].getTargetedDisease()?.getDisplayName() ?: ""} | ${vaccines[1].getVaccine()?.getDisplayName() ?: ""}"
                binding.dose2TableMemberStateValue.text = vaccines[1].countryOfVaccination
                binding.dose2TableIssuerValue.text = vaccines[1].certificateIssuer
                binding.dose2TableCertificateIdentifierValue.text = vaccines[1].certificateIdentifier
            } else {
                binding.dose1TableTitle.visibility = View.GONE
                binding.dose2BoxLayout.visibility = View.GONE
                binding.dose2TableLayout.visibility = View.GONE
            }

        } ?: run {
            binding.vaccineLayout.visibility = View.GONE
        }
    }

    private fun initTest(items: List<DCCTest>?) {
        items?.let { tests ->
            binding.testLayout.visibility = View.VISIBLE
            binding.testBoxName.text = tests[0].getTestResult()?.getDisplayName() ?: ""
            binding.testBoxDate.text = tests[0].dateOfSampleCollection.formatDate()
            binding.testTableTargetValue.text = tests[0].getTargetedDisease()?.getDisplayName() ?: ""
            binding.testTableTypeValue.text = tests[0].getTestType()?.getDisplayName() ?: ""
            binding.testTableNameValue.text = tests[0].NAATestName
            binding.testTableManufacturerValue.text = tests[0].getTestManufacturer()?.getDisplayName() ?: ""
            binding.testTableCenterValue.text = tests[0].testingCentre
            binding.testTableCountryValue.text = tests[0].countryOfTest
            binding.testTableIssuerValue.text = tests[0].certificateIssuer
            binding.testTableIdentifierValue.text = tests[0].certificateIdentifier

        } ?: run {
            binding.testLayout.visibility = View.GONE
        }
    }

    private fun initRecovery(items: List<DCCRecovery>?) {
        items?.let { recoveries ->
            binding.recoveryLayout.visibility = View.VISIBLE
            binding.recoveryBoxName.text = recoveries[0].getTargetedDisease()?.getDisplayName() ?: ""
            binding.recoveryTableFirstDateValue.text = recoveries[0].dateOfFirstPositiveTest.formatDate()
            binding.recoveryTableValidFromValue.text = recoveries[0].certificateValidFrom.formatDate()
            binding.recoveryTableValidToValue.text = recoveries[0].certificateValidTo.formatDate()
            binding.recoveryTableCountryValue.text = recoveries[0].countryOfTest
            binding.recoveryTableIssuerValue.text = recoveries[0].certificateIssuer
            binding.recoveryTableIdentifierValue.text = recoveries[0].certificateIdentifier

        } ?: run {
            binding.recoveryLayout.visibility = View.GONE
        }
    }

    private fun setPauseTimer() {
        countDownTime -= 1
        binding.pauseValue.text = countDownTime.toString()
        if (countDownTime <= 0) {
            findNavControllerSafety(R.id.nav_scan_result_valid)?.navigate(
                ScanResultValidFragmentDirections.actionNavMain()
            )
        }
        autoCloseHandler.postDelayed(autoCloseRunnable, TimeUnit.SECONDS.toMillis(1))
    }

    private fun initCountries() {
        val departureCountry =
            Countries.getCountryNameResId(persistenceManager.getDepartureValue())?.let { getString(it) } ?: getString(R.string.pick_country)
        val destinationCountry =
            Countries.getCountryNameResId(persistenceManager.getDestinationValue())?.let { getString(it) } ?: getString(R.string.pick_country)
        binding.layoutCountryPicker.departureValue.text = departureCountry
        binding.layoutCountryPicker.destinationValue.text = destinationCountry

        binding.layoutCountryPicker.departureCard.setOnClickListener {
            findNavController().navigate(VerifierQrScannerFragmentDirections.actionCountryPicker(true))
        }

        binding.layoutCountryPicker.destinationCard.setOnClickListener {
            findNavController().navigate(VerifierQrScannerFragmentDirections.actionCountryPicker(false))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        autoCloseHandler.removeCallbacks(autoCloseRunnable)
    }

    companion object {
        const val COUNTDOWN_TIME = 60
    }
}
