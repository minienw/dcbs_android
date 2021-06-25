package nl.rijksoverheid.dcbs.verifier.ui.scanner

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.GsonBuilder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import nl.rijksoverheid.ctr.design.ext.enableCustomLinks
import nl.rijksoverheid.ctr.shared.ext.findNavControllerSafety
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.FragmentScanResultBinding
import nl.rijksoverheid.dcbs.verifier.models.*
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import nl.rijksoverheid.dcbs.verifier.persistance.PersistenceManager
import nl.rijksoverheid.dcbs.verifier.ui.scanner.models.VerifiedQr
import nl.rijksoverheid.dcbs.verifier.ui.scanner.utils.ScannerUtil
import nl.rijksoverheid.dcbs.verifier.utils.formatDate
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
class ScanResultFragment : Fragment(R.layout.fragment_scan_result) {

    private var _binding: FragmentScanResultBinding? = null
    private val binding get() = _binding!!

    private val scannerUtil: ScannerUtil by inject()
    private val persistenceManager: PersistenceManager by inject()

    private var countDownTime = COUNTDOWN_TIME
    private val autoCloseHandler = Handler(Looper.getMainLooper())
    private val autoCloseRunnable = Runnable {
        setPauseTimer()
    }

    private val args: ScanResultFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentScanResultBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(ScanResultFragmentDirections.actionNavMain())
        }

        args.data.verifiedQr?.let { verifiedQr ->


            val colorCode = CountryColorCode.fromValue(persistenceManager.getDepartureValue())
            val toCode = persistenceManager.getDestinationValue() ?: ""
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
            val dccQR = gson.fromJson(verifiedQr.data, DCCQR::class.java)
            val failedItems = dccQR.processBusinessRules(colorCode, toCode)

            if (failedItems.isEmpty()) {
                setScreenValid()
                binding.recyclerViewBusinessError.visibility = View.GONE
            } else {
                binding.recyclerViewBusinessError.visibility = View.VISIBLE
                setBusinessErrorMessages(failedItems)
                setScreenInvalid()
            }
            binding.informationLayout.visibility = View.VISIBLE
            binding.descriptionLayout.visibility = View.GONE
            presentPersonalDetails(verifiedQr)

        } ?: run {
            setScreenInvalid()
            binding.descriptionLayout.visibility = View.VISIBLE
            binding.informationLayout.visibility = View.GONE
            binding.recyclerViewBusinessError.visibility = View.GONE
            binding.subtitle.enableCustomLinks {
                findNavController().navigate(ScanResultFragmentDirections.actionInvalidScreenToScanInstructions(true))
            }
        }

        binding.button.setOnClickListener {
            scannerUtil.launchScanner(requireActivity())
        }

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

    private fun setBusinessErrorMessages(failedItems : List<DCCFailableItem>) {

        context?.let { c ->
            GroupAdapter<GroupieViewHolder>()
                .run {
                    addAll(failedItems.map { BusinessErrorAdapterItem(it.getDisplayName(c)) })
                    binding.recyclerViewBusinessError.adapter = this
                }
        }
    }

    private fun setScreenValid() {
        binding.root.setBackgroundResource(R.color.secondary_green)
        binding.title.text = getString(R.string.scan_result_valid_title)
        binding.image.setImageResource(R.drawable.ic_valid_qr_code)
    }

    private fun setScreenInvalid() {
        binding.root.setBackgroundResource(R.color.red)
        binding.title.text = getString(R.string.scan_result_invalid_title)
        binding.image.setImageResource(R.drawable.ic_invalid_qr_code)
    }

    private fun presentPersonalDetails(verifiedQr: VerifiedQr) {

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        val dccQR = gson.fromJson(verifiedQr.data, DCCQR::class.java)
        binding.name.text = dccQR.getName()
        binding.dateOfBirth.text = getString(R.string.item_date_of_birth_x, dccQR.getBirthDate())
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


    private fun initCountries() {
        val departureCountry =
            CountryColorCode.fromValue(persistenceManager.getDepartureValue())?.getDisplayName()?.let { it } ?: getString(R.string.pick_country)
        val destinationCountry =
            Countries.getCountryNameResId(persistenceManager.getDestinationValue())?.let { getString(it) } ?: getString(R.string.pick_country)
        binding.layoutCountryPicker.departureValue.text = departureCountry
        binding.layoutCountryPicker.destinationValue.text = destinationCountry

        binding.layoutCountryPicker.departureCard.setOnClickListener {
            findNavController().navigate(VerifierQrScannerFragmentDirections.actionColorCodePicker())
        }

        binding.layoutCountryPicker.destinationCard.setOnClickListener {
            findNavController().navigate(VerifierQrScannerFragmentDirections.actionCountryPicker())
        }
    }

    private fun setPauseTimer() {
        countDownTime -= 1
        binding.pauseValue.text = countDownTime.toString()
        if (countDownTime <= 0) {
            findNavControllerSafety(R.id.nav_scan_result)?.navigate(
                ScanResultFragmentDirections.actionNavMain()
            )
        }
        autoCloseHandler.postDelayed(autoCloseRunnable, TimeUnit.SECONDS.toMillis(1))
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
