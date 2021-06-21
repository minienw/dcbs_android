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
import com.google.gson.Gson
import nl.rijksoverheid.ctr.shared.ext.findNavControllerSafety
import nl.rijksoverheid.dcbs.verifier.BuildConfig
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.FragmentScanResultValidBinding
import nl.rijksoverheid.dcbs.verifier.models.DCCQR
import nl.rijksoverheid.dcbs.verifier.models.DCCRecovery
import nl.rijksoverheid.dcbs.verifier.models.DCCTest
import nl.rijksoverheid.dcbs.verifier.models.DCCVaccine
import nl.rijksoverheid.dcbs.verifier.ui.scanner.models.ScanResultValidData
import nl.rijksoverheid.dcbs.verifier.ui.scanner.utils.ScannerUtil
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class ScanResultValidFragment : Fragment(R.layout.fragment_scan_result_valid) {

    private var _binding: FragmentScanResultValidBinding? = null
    private val binding get() = _binding!!

    private val args: ScanResultValidFragmentArgs by navArgs()
    private val scannerUtil: ScannerUtil by inject()

    private val autoCloseHandler = Handler(Looper.getMainLooper())
    private val autoCloseRunnable = Runnable {
        findNavControllerSafety(R.id.nav_scan_result_valid)?.navigate(
            ScanResultValidFragmentDirections.actionNavMain()
        )
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
    }

    @ExperimentalStdlibApi
    private fun presentPersonalDetails() {
        val verifiedQr = args.validData.verifiedQr
        val dccQR = Gson().fromJson(verifiedQr.data, DCCQR::class.java)
        binding.name.text = dccQR.getName()
        binding.destination.text = getString(R.string.destination_x, dccQR.issuer)
        binding.dateOfBirth.text = getString(R.string.item_date_of_birth_x, dccQR.getBirthDate())
        initVaccinations(dccQR.dcc?.vaccines)
        initTest(dccQR.dcc?.tests)
        initRecovery(dccQR.dcc?.recoveries)
    }

    private fun initVaccinations(items: List<DCCVaccine>?) {
        items?.let { vaccines ->
            binding.vaccineLayout.visibility = View.VISIBLE
            binding.dose1BoxName.text = vaccines[0].getVaccineProduct()?.getDisplayName() ?: ""
            binding.dose1BoxDate.text = vaccines[0].dateOfVaccination ?: ""
            binding.dose1TableDiseaseVaccineValue.text = vaccines[0].getVaccine()?.getDisplayName() ?: ""
            binding.dose1TableMemberStateValue.text = vaccines[0].countryOfVaccination
            binding.dose1TableCertificateIdentifierValue.text = vaccines[0].certificateIdentifier
            if (vaccines.size == 2) {
                binding.dose2BoxLayout.visibility = View.VISIBLE
                binding.dose2TableLayout.visibility = View.VISIBLE
                binding.dose2BoxName.text = vaccines[1].getVaccineProduct()?.getDisplayName() ?: ""
                binding.dose2BoxDate.text = vaccines[1].dateOfVaccination ?: ""
                binding.dose2TableDiseaseVaccineValue.text = vaccines[1].getVaccine()?.getDisplayName() ?: ""
                binding.dose2TableMemberStateValue.text = vaccines[1].countryOfVaccination
                binding.dose2TableCertificateIdentifierValue.text = vaccines[1].certificateIdentifier
            } else {
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
            binding.testBoxDate.text = tests[0].dateOfSampleCollection ?: ""
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
            binding.recoveryTableFirstDateValue.text = recoveries[0].dateOfFirstPositiveTest
            binding.recoveryTableValidFromValue.text = recoveries[0].certificateValidFrom
            binding.recoveryTableValidToValue.text = recoveries[0].certificateValidTo
            binding.testTableIssuerValue.text = recoveries[0].certificateIssuer
            binding.testTableIdentifierValue.text = recoveries[0].certificateIdentifier

        } ?: run {
            binding.recoveryLayout.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        val autoCloseDuration =
            if (BuildConfig.FLAVOR == "tst") TimeUnit.SECONDS.toMillis(10) else TimeUnit.MINUTES.toMillis(
                3
            )
        autoCloseHandler.postDelayed(autoCloseRunnable, autoCloseDuration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        autoCloseHandler.removeCallbacks(autoCloseRunnable)
    }
}
