package nl.rijksoverheid.dcbs.verifier.ui.scanner

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import io.mockk.mockk
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.fakeVerifiedQr
import nl.rijksoverheid.dcbs.verifier.ui.scanner.models.ScanResultValidData
import nl.rijksoverheid.dcbs.verifier.ui.scanner.utils.ScannerUtil
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.robolectric.RobolectricTestRunner

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
@RunWith(RobolectricTestRunner::class)
class ScanResultValidFragmentTest : AutoCloseKoinTest() {

    private lateinit var navController: TestNavHostController
    private val scannerUtil: ScannerUtil = mockk(relaxed = true)


    private fun launchScanResultValidFragment(
        data: ScanResultValidData = ScanResultValidData.Valid(
            fakeVerifiedQr()
        )
    ) {
        loadKoinModules(
            module(override = true) {
                factory {
                    scannerUtil
                }
            }
        )

        navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        ).also {
            it.setGraph(R.navigation.verifier_nav_graph_scanner)
            it.setCurrentDestination(R.id.nav_scan_result_valid)
        }

        launchFragmentInContainer(
            themeResId = R.style.AppTheme,
            fragmentArgs = bundleOf("validData" to data)
        ) {
            ScanResultValidFragment().also {
                it.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        Navigation.setViewNavController(it.requireView(), navController)
                    }
                }
            }
        }
    }
}
