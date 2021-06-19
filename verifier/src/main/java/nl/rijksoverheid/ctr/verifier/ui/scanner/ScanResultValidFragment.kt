package nl.rijksoverheid.ctr.verifier.ui.scanner

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
import nl.rijksoverheid.ctr.verifier.BuildConfig
import nl.rijksoverheid.ctr.verifier.R
import nl.rijksoverheid.ctr.verifier.databinding.FragmentScanResultValidBinding
import nl.rijksoverheid.ctr.verifier.models.DCCQR
import nl.rijksoverheid.ctr.verifier.ui.scanner.models.ScanResultValidData
import nl.rijksoverheid.ctr.verifier.ui.scanner.utils.ScannerUtil
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
        binding.destination.text = getString(R.string.destination_country, dccQR.getIssuer()?.getDisplayName())
        binding.dateOfBirth.text = getString(R.string.date_of_birth, dccQR.getBirthDate())
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
