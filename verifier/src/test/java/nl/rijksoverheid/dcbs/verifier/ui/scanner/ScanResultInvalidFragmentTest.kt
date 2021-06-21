package nl.rijksoverheid.dcbs.verifier.ui.scanner

import androidx.core.os.bundleOf
import androidx.core.text.parseAsHtml
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.action.ViewActions.openLinkWithUri
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import com.schibsted.spain.barista.assertion.BaristaBackgroundAssertions.assertHasBackground
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaScrollInteractions.scrollTo
import com.schibsted.spain.barista.internal.performActionOnView
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.mockk
import io.mockk.verify
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.fakeVerifiedQr
import nl.rijksoverheid.dcbs.verifier.ui.scanner.models.ScanResultInvalidData
import nl.rijksoverheid.dcbs.verifier.ui.scanner.utils.ScannerUtil
import org.junit.Assert.assertEquals
import org.junit.Test
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
class ScanResultInvalidFragmentTest : AutoCloseKoinTest() {

    private lateinit var navController: TestNavHostController
    private val scannerUtil: ScannerUtil = mockk(relaxed = true)

    @Test
    fun `Screen shows correct content`() {
        launchScanResultInvalidFragment(data = ScanResultInvalidData.Error("invalid QR code"))
        assertHasBackground(R.id.root, R.color.red)
        assertDisplayed(R.id.title, R.string.scan_result_invalid_title)
        scrollTo(R.id.subtitle)
        assertDisplayed(
            R.id.subtitle,
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.scan_result_invalid_subtitle)
                .parseAsHtml().toStr()
        )
    }

    @Test
    fun `Invalid result on description click opens explanation dialog`() {
        launchScanResultInvalidFragment(data = ScanResultInvalidData.Error("invalid QR code"))
        performActionOnView(withId(R.id.subtitle), openLinkWithUri(""))
        assertEquals(
            navController.currentDestination?.id,
            R.id.invalid_explanation_bottomsheet
        )
    }

    @Test
    fun `DCC QR issued in NL shows correct error message`() {
        launchScanResultInvalidFragment(data = ScanResultInvalidData.Invalid(verifiedQr = fakeVerifiedQr()))
        assertHasBackground(R.id.root, R.color.red)
        assertDisplayed(R.id.title, R.string.scan_result_european_nl_invalid_title)
        scrollTo(R.id.subtitle)
        assertDisplayed(
            R.id.subtitle,
            InstrumentationRegistry.getInstrumentation().context.getString(R.string.scan_result_european_nl_invalid_subtitle)
                .parseAsHtml().toStr()
        )
    }

    @Test
    fun `Clicking scan again button opens scanner`() {
        launchScanResultInvalidFragment()
        clickOn(R.id.button)
        verify { scannerUtil.launchScanner(any()) }
    }


    private fun launchScanResultInvalidFragment(
        data: ScanResultInvalidData = ScanResultInvalidData.Invalid(
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
            it.setCurrentDestination(R.id.nav_scan_result_invalid)
        }

        launchFragmentInContainer(
            themeResId = R.style.AppTheme,
            fragmentArgs = bundleOf("invalidData" to data)
        ) {
            ScanResultInvalidFragment().also {
                it.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        Navigation.setViewNavController(it.requireView(), navController)
                    }
                }
            }
        }
    }
}
