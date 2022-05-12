package nl.rijksoverheid.dcbs.verifier.ui.scanner

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.GsonBuilder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import nl.rijksoverheid.ctr.design.ext.enableCustomLinks
import nl.rijksoverheid.ctr.shared.ext.findNavControllerSafety
import nl.rijksoverheid.ctr.shared.utils.Accessibility.setAsAccessibilityButton
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.FragmentScanResultBinding
import nl.rijksoverheid.dcbs.verifier.models.*
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableType
import nl.rijksoverheid.dcbs.verifier.models.data.ValueSetType
import nl.rijksoverheid.dcbs.verifier.persistance.PersistenceManager
import nl.rijksoverheid.dcbs.verifier.ui.scanner.models.VerifiedQr
import nl.rijksoverheid.dcbs.verifier.ui.scanner.utils.ScannerUtil
import nl.rijksoverheid.dcbs.verifier.utils.AppConfigCachedUtil
import nl.rijksoverheid.dcbs.verifier.utils.formatDate
import nl.rijksoverheid.dcbs.verifier.utils.timeAgo
import nl.rijksoverheid.dcbs.verifier.utils.toDate
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
    private val appConfigUtil: AppConfigCachedUtil by inject()
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
            appConfigUtil.getValueSetsRaw()?.let { valueSetsRaw ->
                appConfigUtil.getCountries(true)?.let { countries ->
                    val from = countries.find { it.code == persistenceManager.getDepartureValue() }
                        ?: CountryRisk.getUnselected(context)
                    val to = countries.find { it.code == persistenceManager.getDestinationValue() }
                        ?: CountryRisk.getUnselected(context)
                    val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
                    val dccQR = gson.fromJson(verifiedQr.data, DCCQR::class.java)
                    val failedItems =
                        dccQR.processBusinessRules(
                            from,
                            to,
                            appConfigUtil.getAllBusinessRules(),
                            valueSetsRaw
                        )
                    val shouldShowGreenOverride = dccQR.shouldShowGreenOverride(from, to)

                    when {
                        failedItems.isEmpty() -> setScreenValid()
                        failedItems.any { it.type == DCCFailableType.UndecidableFrom } -> {
                            setScreenUndecided()
                        }
                        else -> {
                            if (!shouldShowGreenOverride) {
                                binding.recyclerViewBusinessError.visibility = View.VISIBLE
                                setBusinessErrorMessages(failedItems)
                            } else {
                                binding.recyclerViewBusinessError.visibility = View.GONE
                            }
                            setScreenInvalid(
                                R.drawable.ic_valid_qr_code,
                                shouldShowGreenOverride
                            )
                        }
                    }
                    binding.informationLayout.visibility = View.VISIBLE
                    binding.descriptionLayout.visibility = View.GONE
                    presentPersonalDetails(verifiedQr)
                }
            }

        } ?: run {
            setScreenInvalid(R.drawable.ic_invalid_qr_code, false)
            binding.descriptionLayout.visibility = View.VISIBLE
            binding.informationLayout.visibility = View.GONE
            binding.recyclerViewBusinessError.visibility = View.GONE
            binding.subtitle.enableCustomLinks {
                findNavController().navigate(
                    ScanResultFragmentDirections.actionInvalidScreenToScanInstructions(
                        true
                    )
                )
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

    private fun playAccessibilityMessage(message: String) {
        (activity?.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager)?.let { manager ->
            if (manager.isEnabled) {
                val e = AccessibilityEvent.obtain()
                e.eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT
                e.text.add(message)
                manager.sendAccessibilityEvent(e)
            }
        }
    }

    private fun setBusinessErrorMessages(failedItems: List<DCCFailableItem>) {

        context?.let { c ->
            GroupAdapter<GroupieViewHolder>()
                .run {
                    addAll(failedItems.map { BusinessErrorAdapterItem(it.getDisplayName(c)) })
                    binding.recyclerViewBusinessError.adapter = this
                }
        }
    }

    private fun setScreenValid() {
        val context = context ?: return
        binding.root.setBackgroundResource(R.color.secondary_green)
        binding.title.text = getString(R.string.valid_for_journey)
        binding.title.setTextColor(ContextCompat.getColor(context, R.color.white))
        binding.layoutCountryPicker.riskLabel.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.white
            )
        )
        binding.image.setImageResource(R.drawable.ic_valid_qr_code)
        binding.recyclerViewBusinessError.visibility = View.GONE
        playAccessibilityMessage(getString(R.string.scan_result_valid_title))
    }

    private fun setScreenUndecided() {
        val context = context ?: return
        binding.root.setBackgroundResource(R.color.undecided_gray)
        binding.title.text = getString(R.string.result_inconclusive_title)
        binding.title.setTextColor(ContextCompat.getColor(context, R.color.black))
        binding.layoutCountryPicker.riskLabel.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.black
            )
        )
        binding.image.setImageResource(R.drawable.ic_valid_qr_code)
        binding.recyclerViewBusinessError.visibility = View.GONE
        playAccessibilityMessage(getString(R.string.scan_result_valid_title))
    }

    private fun setScreenInvalid(@DrawableRes iconResId: Int, showGreen: Boolean) {
        val context = context ?: return
        binding.root.setBackgroundResource(if (showGreen) R.color.secondary_green else R.color.red)
        binding.title.text =
            if (showGreen) getString(R.string.valid_for_journey) else getString(R.string.invalid_for_journey)
        binding.title.setTextColor(ContextCompat.getColor(context, R.color.white))
        binding.layoutCountryPicker.riskLabel.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.white
            )
        )
        binding.image.setImageResource(iconResId)
        playAccessibilityMessage(getString(R.string.scan_result_invalid_title))
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
            if (vaccines.isNotEmpty()) {
                binding.vaccineLayout.visibility = View.VISIBLE
                val vaccinMedicalProduct = getValueSetDisplayName(
                    vaccines[0].vaccineMedicalProduct,
                    ValueSetType.VaccineProduct
                )
                binding.dose1BoxTitle.text = getString(
                    R.string.item_vaccin_x_dose_x_x,
                    vaccinMedicalProduct,
                    vaccines[0].doseNumber,
                    vaccines[0].totalSeriesOfDoses
                )
                binding.dose1Status.text = getVaccineStatusText(vaccines[0])
                getVaccineStatusColour(vaccines[0])?.let { textColor ->
                    binding.dose1Status.setTextColor(textColor)
                }
                binding.dose1BoxDate.text = vaccines[0].dateOfVaccination?.formatDate()
                binding.dose1BoxTimeAgo.text = vaccines[0].dateOfVaccination?.toDate()?.timeAgo(
                    daysLabel = getString(R.string.x_days),
                    dayLabel = getString(R.string.x_day),
                    oldLabel = getString(R.string.old)
                )
                binding.dose1TableDiseaseValue.text =
                    getValueSetDisplayName(vaccines[0].targetedDisease, ValueSetType.TargetedAgent)
                binding.dose1TableVaccineValue.text =
                    getValueSetDisplayName(vaccines[0].vaccine, ValueSetType.VaccineType)
                binding.dose1TableMemberStateValue.text = vaccines[0].countryOfVaccination
                binding.dose1TableManufacturerValue.text =
                    getValueSetDisplayName(
                        vaccines[0].marketingAuthorizationHolder,
                        ValueSetType.VaccineAuthHolder
                    )
                binding.dose1TableIssuerValue.text = vaccines[0].certificateIssuer
                binding.dose1TableCertificateIdentifierValue.text =
                    vaccines[0].certificateIdentifier
                if (vaccines.size == 2) {
                    binding.dose1TableTitle.visibility = View.VISIBLE
                    binding.dose2BoxLayout.visibility = View.VISIBLE
                    binding.dose2TableLayout.visibility = View.VISIBLE
                    binding.dose2BoxTitle.text = getString(
                        R.string.item_vaccin_x_dose_x_x,
                        vaccines[1].vaccine,
                        vaccines[1].doseNumber,
                        vaccines[1].totalSeriesOfDoses
                    )
                    binding.dose2BoxName.text = getValueSetDisplayName(
                        vaccines[1].vaccineMedicalProduct,
                        ValueSetType.VaccineProduct
                    )
                    binding.dose2BoxDate.text = vaccines[1].dateOfVaccination?.formatDate()
                    binding.dose2TableDiseaseValue.text = getValueSetDisplayName(
                        vaccines[1].targetedDisease,
                        ValueSetType.TargetedAgent
                    )
                    binding.dose2TableVaccineValue.text =
                        getValueSetDisplayName(vaccines[1].vaccine, ValueSetType.VaccineType)
                    binding.dose2TableMemberStateValue.text = vaccines[1].countryOfVaccination
                    binding.dose2TableManufacturerValue.text =
                        getValueSetDisplayName(
                            vaccines[1].marketingAuthorizationHolder,
                            ValueSetType.VaccineAuthHolder
                        )
                    binding.dose2TableIssuerValue.text = vaccines[1].certificateIssuer
                    binding.dose2TableCertificateIdentifierValue.text =
                        vaccines[1].certificateIdentifier
                } else {
                    binding.dose1TableTitle.visibility = View.GONE
                    binding.dose2BoxLayout.visibility = View.GONE
                    binding.dose2TableLayout.visibility = View.GONE
                }
            } else {
                binding.vaccineLayout.visibility = View.GONE
            }
        } ?: run {
            binding.vaccineLayout.visibility = View.GONE
        }
    }

    private fun getValueSetDisplayName(value: String, valueSetType: ValueSetType): String {
        val valueItem =
            appConfigUtil.getValueSetContainer(valueSetType)?.find { it.key == value }?.item
        return valueItem?.display ?: value
    }

    private fun getVaccineStatusColour(vaccine: DCCVaccine): Int? {
        return context?.let {
            ContextCompat.getColor(
                it, if (vaccine.isFullyVaccinated())
                    R.color.black
                else R.color.red
            )
        }
    }

    private fun getVaccineStatusText(vaccine: DCCVaccine): String {
        return if (vaccine.isFullyVaccinated()) getString(R.string.vaccin_complete) else getString(
            R.string.vaccin_incomplete
        )
    }

    private fun initTest(items: List<DCCTest>?) {
        val context = context ?: return
        items?.let { tests ->
            if (tests.isNotEmpty()) {
                binding.testLayout.visibility = View.VISIBLE
                binding.testBoxTitle.text =
                    tests[0].getTestResult(appConfigUtil.getEuropeanVerificationRules())
                        ?.getDisplayName(context) ?: tests[0].testResult
                binding.testBoxDate.text = tests[0].dateOfSampleCollection?.formatDate()
                binding.testBoxAge.text = tests[0].getTestAge(context) ?: ""
                binding.testTableTargetValue.text =
                    getValueSetDisplayName(tests[0].targetedDisease, ValueSetType.TargetedAgent)
                binding.testTableTypeValue.text =
                    getValueSetDisplayName(tests[0].typeOfTest, ValueSetType.TestType)
                binding.testTableNameValue.text = tests[0].NAATestName
                binding.testTableManufacturerValue.text = getValueSetDisplayName(
                    tests[0].RATTestNameAndManufac ?: "",
                    ValueSetType.TestManufacturer
                )
                binding.testTableCenterValue.text = tests[0].testingCentre
                binding.testTableCountryValue.text = tests[0].countryOfTest
                binding.testTableIssuerValue.text = tests[0].certificateIssuer
                binding.testTableIdentifierValue.text = tests[0].certificateIdentifier
            } else {
                binding.testLayout.visibility = View.GONE
            }
        } ?: run {
            binding.testLayout.visibility = View.GONE
        }
    }

    private fun initRecovery(items: List<DCCRecovery>?) {
        items?.let { recoveries ->
            if (recoveries.isNotEmpty()) {
                binding.recoveryLayout.visibility = View.VISIBLE
                binding.recoveryBoxName.text = getValueSetDisplayName(
                    recoveries[0].targetedDisease,
                    ValueSetType.TargetedAgent
                )
                binding.recoveryTableFirstDateValue.text =
                    recoveries[0].dateOfFirstPositiveTest?.formatDate()
                binding.recoveryTableValidFromValue.text =
                    recoveries[0].certificateValidFrom?.formatDate()
                binding.recoveryTableValidToValue.text =
                    recoveries[0].certificateValidTo?.formatDate()
                binding.recoveryTableCountryValue.text = recoveries[0].countryOfTest
                binding.recoveryTableIssuerValue.text = recoveries[0].certificateIssuer
                binding.recoveryTableIdentifierValue.text = recoveries[0].certificateIdentifier
            } else {
                binding.recoveryLayout.visibility = View.GONE
            }
        } ?: run {
            binding.recoveryLayout.visibility = View.GONE
        }
    }

    private fun initCountries() {

        appConfigUtil.getCountries(true)?.let { countries ->
            val departureCountryRisk =
                countries.find { it.code == persistenceManager.getDepartureValue() }
            val departureCountry = departureCountryRisk?.name() ?: getString(R.string.pick_country)

            val destinationCountryRisk =
                countries.find { it.code == persistenceManager.getDestinationValue() }
            val isNLDestination = destinationCountryRisk?.getPassType() == CountryRiskPass.NLRules
            val destinationCountry =
                destinationCountryRisk?.name() ?: getString(R.string.pick_country)
            binding.layoutCountryPicker.departureValue.text =
                if (isNLDestination) departureCountry else getString(R.string.country_not_used)
            binding.layoutCountryPicker.destinationValue.text = destinationCountry

            departureCountryRisk?.let {
                val riskColor =
                    countries.find { it.isColourCode == true && it.color == departureCountryRisk.color }
                        ?.name()
                val euLabel =
                    if (departureCountryRisk.isEU == true) getString(R.string.item_eu) else getString(
                        R.string.item_not_eu
                    )
                binding.layoutCountryPicker.riskLabel.text = "${riskColor ?: ""} | $euLabel"
            } ?: run {
                binding.layoutCountryPicker.riskLabel.text = ""
            }

            context?.let {
                if (isNLDestination) {
                    binding.layoutCountryPicker.departureCard.setBackgroundResource(R.drawable.bg_white_opacity70)
                    binding.layoutCountryPicker.departureValue.setTextColor(
                        ContextCompat.getColor(
                            it,
                            R.color.primary_blue
                        )
                    )
                    binding.layoutCountryPicker.departureCard.setOnClickListener {
                        findNavController().navigate(VerifierQrScannerFragmentDirections.actionDeparturePicker())
                    }
                } else {
                    binding.layoutCountryPicker.departureCard.setBackgroundResource(R.drawable.bg_inactive_gray_opacity70)
                    binding.layoutCountryPicker.departureValue.setTextColor(
                        ContextCompat.getColor(
                            it,
                            R.color.black
                        )
                    )
                }
            }
        }

        binding.layoutCountryPicker.departureCard.setAsAccessibilityButton(true)
        binding.layoutCountryPicker.departureCard.contentDescription =
            getString(R.string.accessibility_choose_departure_button)
        binding.layoutCountryPicker.destinationCard.setAsAccessibilityButton(true)
        binding.layoutCountryPicker.destinationCard.contentDescription =
            getString(R.string.accessibility_choose_destination_button)

        binding.layoutCountryPicker.destinationCard.setOnClickListener {
            findNavController().navigate(VerifierQrScannerFragmentDirections.actionDestinationPicker())
        }
    }

    private fun setPauseTimer() {
        countDownTime -= 1
        binding.pauseValue.text = countDownTime.toString()
        if (countDownTime.toString().toList().last() == '0') {
            playAccessibilityMessage(countDownTime.toString())
        }
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
