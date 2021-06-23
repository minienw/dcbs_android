package nl.rijksoverheid.dcbs.verifier.ui.scanner

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import nl.rijksoverheid.ctr.design.ext.enableCustomLinks
import nl.rijksoverheid.ctr.shared.ext.findNavControllerSafety
import nl.rijksoverheid.dcbs.verifier.BuildConfig
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.FragmentScanResultInvalidBinding
import nl.rijksoverheid.dcbs.verifier.databinding.FragmentScanResultValidBinding
import nl.rijksoverheid.dcbs.verifier.ui.scanner.models.ScanResultInvalidData
import nl.rijksoverheid.dcbs.verifier.ui.scanner.utils.ScannerUtil
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
class ScanResultInvalidFragment : Fragment(R.layout.fragment_scan_result_invalid) {

    private var _binding: FragmentScanResultInvalidBinding? = null
    private val binding get() = _binding!!
    
    private val scannerUtil: ScannerUtil by inject()


    private var countDownTime = COUNTDOWN_TIME
    private val autoCloseHandler = Handler(Looper.getMainLooper())
    private val autoCloseRunnable = Runnable {
        setPauseTimer()
    }

    private val args: ScanResultInvalidFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentScanResultInvalidBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(ScanResultInvalidFragmentDirections.actionNavMain())
        }

        when (args.invalidData) {
            is ScanResultInvalidData.Invalid -> {
                binding.title.text = getString(R.string.scan_result_european_nl_invalid_title)
                binding.subtitle.text = getString(R.string.scan_result_european_nl_invalid_subtitle)
            }
            is ScanResultInvalidData.Error -> {
                binding.subtitle.enableCustomLinks {
                    findNavController().navigate(ScanResultInvalidFragmentDirections.actionShowInvalidExplanation())
                }
            }
        }

        binding.button.setOnClickListener {
            scannerUtil.launchScanner(requireActivity())
        }

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

    private fun setPauseTimer() {
        countDownTime -= 1
        binding.pauseValue.text = countDownTime.toString()
        if (countDownTime <= 0) {
            findNavControllerSafety(R.id.nav_scan_result_invalid)?.navigate(
                ScanResultInvalidFragmentDirections.actionNavMain()
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
