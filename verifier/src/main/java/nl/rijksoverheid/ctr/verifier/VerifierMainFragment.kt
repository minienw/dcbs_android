/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

package nl.rijksoverheid.ctr.verifier

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import nl.rijksoverheid.ctr.design.BaseMainFragment
import nl.rijksoverheid.ctr.design.menu.about.AboutThisAppData
import nl.rijksoverheid.ctr.design.menu.about.AboutThisAppFragment
import nl.rijksoverheid.ctr.verifier.databinding.FragmentMainBinding

class VerifierMainFragment :
    BaseMainFragment(R.layout.fragment_main, setOf(R.id.nav_scan_qr)) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

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

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_about_this_app -> binding.toolbar.menu.findItem(R.id.about)?.isVisible = false
                else -> binding.toolbar.menu.findItem(R.id.about)?.isVisible = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}
