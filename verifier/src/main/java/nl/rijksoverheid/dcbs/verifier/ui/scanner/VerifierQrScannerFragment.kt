package nl.rijksoverheid.dcbs.verifier.ui.scanner

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.barcode.Barcode
import nl.rijksoverheid.ctr.qrscanner.QrCodeScannerFragment
import nl.rijksoverheid.ctr.shared.livedata.EventObserver
import nl.rijksoverheid.ctr.shared.utils.Accessibility.setAsAccessibilityButton
import nl.rijksoverheid.dcbs.verifier.BuildConfig
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.VerifierMainActivity
import nl.rijksoverheid.dcbs.verifier.models.CountryRiskPass
import nl.rijksoverheid.dcbs.verifier.persistance.PersistenceManager
import nl.rijksoverheid.dcbs.verifier.ui.scanner.models.ScanResultData
import nl.rijksoverheid.dcbs.verifier.ui.scanner.models.VerifiedQrResultState
import nl.rijksoverheid.dcbs.verifier.utils.AppConfigCachedUtil
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
class VerifierQrScannerFragment : QrCodeScannerFragment() {

    private val scannerViewModel: ScannerViewModel by viewModel()
    private val persistenceManager: PersistenceManager by inject()
    private val appConfigUtil: AppConfigCachedUtil by inject()

    private val autoConfigCheckHandler = Handler(Looper.getMainLooper())
    private val autoConfigCheckRunnable = Runnable {
        checkLastConfigFetchExpired()
    }

    override fun onQrScanned(content: String) {
        scannerViewModel.validate(
            qrContent = content
        )
    }

    override fun getCopy(): Copy {
        return Copy(
            title = getString(R.string.scanner_custom_title),
            message = getString(R.string.scanner_custom_message),
            rationaleDialog = Copy.RationaleDialog(
                title = getString(R.string.camera_rationale_dialog_title),
                description = getString(R.string.camera_rationale_dialog_description),
                okayButtonText = getString(R.string.ok)
            )
        )
    }

    override fun getBarcodeFormats(): List<Int> {
        val formats = mutableListOf<Int>()
        formats.add(Barcode.FORMAT_QR_CODE)
        if (BuildConfig.FLAVOR == "tst") {
            formats.add(Barcode.FORMAT_AZTEC)
        }
        return formats
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scannerViewModel.loadingLiveData.observe(viewLifecycleOwner, EventObserver {
            binding.progress.visibility = if (it) View.VISIBLE else View.GONE
        })

        scannerViewModel.verifiedQrResultStateLiveData.observe(viewLifecycleOwner, EventObserver {

            when (it) {
                is VerifiedQrResultState.Error -> {
                    (activity as? VerifierMainActivity)?.updateConfig()
                    findNavController().navigate(VerifierQrScannerFragmentDirections.actionScanResult(ScanResultData(null)))
                }
                is VerifiedQrResultState.Demo -> {
                    findNavController().navigate(VerifierQrScannerFragmentDirections.actionScanResult(ScanResultData(it.verifiedQr)))
                }
                is VerifiedQrResultState.Valid -> {
                    findNavController().navigate(VerifierQrScannerFragmentDirections.actionScanResult(ScanResultData(it.verifiedQr)))
                }
                is VerifiedQrResultState.Invalid -> {
                    findNavController().navigate(VerifierQrScannerFragmentDirections.actionScanResult(ScanResultData(it.verifiedQr)))
                }
            }
        })

        binding.layoutCertificateExpired.btnUpdate.setOnClickListener {
            (activity as? VerifierMainActivity)?.updateConfig()
            binding.layoutCertificateExpired.root.visibility = View.GONE
        }

        appConfigUtil.getCountries(true)?.let { countries ->
            val departureCountryRisk = countries.find { it.code == persistenceManager.getDepartureValue() }
            val departureCountry = departureCountryRisk?.name() ?: getString(R.string.pick_country)

            val destinationCountryRisk = countries.find { it.code == persistenceManager.getDestinationValue() }
            val isNLDestination = destinationCountryRisk?.getPassType() == CountryRiskPass.NLRules
            val destinationCountry = destinationCountryRisk?.name() ?: getString(R.string.pick_country)
            binding.layoutCountryPicker.departureValue.text = if (isNLDestination) departureCountry else getString(R.string.country_not_used)
            binding.layoutCountryPicker.destinationValue.text = destinationCountry
            val riskColor = countries.find { it.isColourCode == true && it.color == departureCountryRisk?.color }?.name()
            val euLabel = if (departureCountryRisk?.isEU == true) getString(R.string.item_eu) else getString(R.string.item_not_eu)
            binding.layoutCountryPicker.riskLabel.text = "${riskColor ?: ""} | $euLabel"

            context?.let {
                if (isNLDestination) {
                    binding.layoutCountryPicker.departureCard.setBackgroundResource(R.drawable.bg_white_opacity70)
                    binding.layoutCountryPicker.departureValue.setTextColor(ContextCompat.getColor(it, R.color.primary_blue))
                    binding.layoutCountryPicker.departureCard.setOnClickListener {
                        findNavController().navigate(VerifierQrScannerFragmentDirections.actionDeparturePicker())
                    }
                } else {
                    binding.layoutCountryPicker.departureCard.setBackgroundResource(R.drawable.bg_inactive_gray_opacity70)
                    binding.layoutCountryPicker.departureValue.setTextColor(ContextCompat.getColor(it, R.color.black))
                }
            }

            binding.layoutCountryPicker.departureCard.setAsAccessibilityButton(true)
            binding.layoutCountryPicker.departureCard.contentDescription = getString(R.string.accessibility_choose_departure_button)
            binding.layoutCountryPicker.destinationCard.setAsAccessibilityButton(true)
            binding.layoutCountryPicker.destinationCard.contentDescription = getString(R.string.accessibility_choose_destination_button)
        }

        binding.layoutCountryPicker.destinationCard.setOnClickListener {
            findNavController().navigate(VerifierQrScannerFragmentDirections.actionDestinationPicker())
        }

    }

    override fun onResume() {
        super.onResume()
        autoConfigCheckHandler.postDelayed(autoConfigCheckRunnable, TimeUnit.SECONDS.toMillis(10))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoConfigCheckHandler.removeCallbacks(autoConfigCheckRunnable)
    }

    private fun checkLastConfigFetchExpired() {
        val refreshCheckDuration = if (BuildConfig.FLAVOR == "acc") TimeUnit.MINUTES.toSeconds(2) else TimeUnit.MINUTES.toSeconds(60)
        val expiredLayoutDuration = if (BuildConfig.FLAVOR == "acc") TimeUnit.MINUTES.toSeconds(1) else TimeUnit.DAYS.toSeconds(1)

        val needsConfigRefresh = (activity as? VerifierMainActivity)?.checkLastConfigFetchExpired(refreshCheckDuration)
        if (needsConfigRefresh == true) {
            (activity as? VerifierMainActivity)?.updateConfig()
        }

        val shouldShowExpiredLayout = (activity as? VerifierMainActivity)?.checkLastConfigFetchExpired(expiredLayoutDuration)
        binding.layoutCertificateExpired.root.visibility = if (shouldShowExpiredLayout == true) View.VISIBLE else View.GONE
        autoConfigCheckHandler.postDelayed(autoConfigCheckRunnable, TimeUnit.SECONDS.toMillis(10))
    }
}
