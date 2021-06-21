/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

package nl.rijksoverheid.ctr.verifier

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import nl.rijksoverheid.ctr.design.BaseMainFragment
import nl.rijksoverheid.ctr.design.menu.about.AboutThisAppData
import nl.rijksoverheid.ctr.design.menu.about.AboutThisAppFragment
import nl.rijksoverheid.ctr.verifier.databinding.FragmentMainBinding
import java.util.concurrent.TimeUnit

class VerifierMainFragment :
    BaseMainFragment(R.layout.fragment_main, setOf(R.id.nav_scan_qr)) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val autoConfigCheckHandler = Handler(Looper.getMainLooper())
    private val autoConfigCheckRunnable = Runnable {
        checkLastConfigFetchExpired()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.about) {
                navigateToAbout()
            }
            return@setOnMenuItemClickListener true
        }

        binding.layoutCertificateExpired.btnUpdate.setOnClickListener {
            (activity as? VerifierMainActivity)?.updateConfig()
            binding.layoutCertificateExpired.root.visibility = View.GONE
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_about_this_app, R.id.nav_scan_instructions -> binding.toolbar.menu.findItem(R.id.about)?.isVisible = false
                else -> binding.toolbar.menu.findItem(R.id.about)?.isVisible = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        autoConfigCheckHandler.postDelayed(autoConfigCheckRunnable, TimeUnit.SECONDS.toMillis(10))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        autoConfigCheckHandler.removeCallbacks(autoConfigCheckRunnable)
    }

    private fun navigateToAbout() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(
            R.id.action_about_this_app, AboutThisAppFragment.getBundle(
                data = AboutThisAppData(
                    versionName = BuildConfig.VERSION_NAME,
                    versionCode = BuildConfig.VERSION_CODE.toString(),
                    readMoreItems = listOf(
                        AboutThisAppData.ReadMoreItem(
                            text = getString(R.string.contact),
                            url = getString(R.string.url_faq),
                        ),
                        AboutThisAppData.ReadMoreItem(
                            text = getString(R.string.privacy_statement),
                            url = getString(R.string.url_terms_of_use),
                        ),
                        AboutThisAppData.ReadMoreItem(
                            text = getString(R.string.about_this_app_accessibility),
                            url = getString(R.string.url_accessibility),
                        )
                    )
                )
            )
        )
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
