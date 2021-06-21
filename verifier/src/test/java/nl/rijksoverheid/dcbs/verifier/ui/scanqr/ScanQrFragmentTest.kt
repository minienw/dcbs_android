package nl.rijksoverheid.dcbs.verifier.ui.scanqr

import android.content.SharedPreferences
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import io.mockk.mockk
import io.mockk.verify
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.fakeScanQrViewModel
import nl.rijksoverheid.dcbs.verifier.ui.scanner.utils.ScannerUtil
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
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
class ScanQrFragmentTest : AutoCloseKoinTest() {

    private val scannerUtil: ScannerUtil = mockk(relaxed = true)

    private val navController = TestNavHostController(
        ApplicationProvider.getApplicationContext()
    ).also {
        it.setGraph(R.navigation.verifier_nav_graph_main)
        it.setCurrentDestination(R.id.nav_scan_qr)
    }

    /**
     * Camera qr code scanner is bypassed in test
     */
    @Test
    fun `Clicking start scan opens scanner`() {
        launchScanQrFragment()
        clickOn(R.id.button)
        verify { scannerUtil.launchScanner(any()) }
    }

    private fun launchScanQrFragment(
        hasSeenScanInstructions: Boolean = true
    ) {
        loadKoinModules(
            module(override = true) {
                factory<SharedPreferences> {
                    PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getInstrumentation().context)
                }

                factory {
                    scannerUtil
                }

                viewModel {
                    fakeScanQrViewModel(
                        scanInstructionsSeen = hasSeenScanInstructions
                    )
                }
            }
        )

        launchFragmentInContainer(themeResId = R.style.AppTheme) {
            ScanQrFragment().also {
                it.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        Navigation.setViewNavController(it.requireView(), navController)
                    }
                }
            }
        }
    }

}
