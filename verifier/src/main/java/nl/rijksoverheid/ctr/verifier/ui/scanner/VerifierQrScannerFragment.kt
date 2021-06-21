package nl.rijksoverheid.ctr.verifier.ui.scanner

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.barcode.Barcode
import nl.rijksoverheid.ctr.qrscanner.QrCodeScannerFragment
import nl.rijksoverheid.ctr.shared.livedata.EventObserver
import nl.rijksoverheid.ctr.verifier.BuildConfig
import nl.rijksoverheid.ctr.verifier.R
import nl.rijksoverheid.ctr.verifier.VerifierMainActivity
import nl.rijksoverheid.ctr.verifier.ui.scanner.models.ScanResultInvalidData
import nl.rijksoverheid.ctr.verifier.ui.scanner.models.ScanResultValidData
import nl.rijksoverheid.ctr.verifier.ui.scanner.models.VerifiedQrResultState
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
                is VerifiedQrResultState.Valid -> {
                    findNavController().navigate(
                        VerifierQrScannerFragmentDirections.actionScanResultValid(
                            validData = ScanResultValidData.Valid(
                                verifiedQr = it.verifiedQr
                            )
                        )
                    )
                }
                is VerifiedQrResultState.Demo -> {
                    findNavController().navigate(
                        VerifierQrScannerFragmentDirections.actionScanResultValid(
                            validData = ScanResultValidData.Demo(
                                verifiedQr = it.verifiedQr
                            )
                        )
                    )
                }
                is VerifiedQrResultState.Invalid -> {
                    findNavController().navigate(
                        VerifierQrScannerFragmentDirections.actionScanResultInvalid(
                            invalidData = ScanResultInvalidData.Invalid(
                                verifiedQr = it.verifiedQr
                            )
                        )
                    )
                }
                is VerifiedQrResultState.Error -> {
                    findNavController().navigate(
                        VerifierQrScannerFragmentDirections.actionScanResultInvalid(
                            invalidData = ScanResultInvalidData.Error(
                                error = it.error
                            )
                        )
                    )
                }
            }
        })

        binding.layoutCertificateExpired.btnUpdate.setOnClickListener {
            (activity as? VerifierMainActivity)?.updateConfig()
            binding.layoutCertificateExpired.root.visibility = View.GONE
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
