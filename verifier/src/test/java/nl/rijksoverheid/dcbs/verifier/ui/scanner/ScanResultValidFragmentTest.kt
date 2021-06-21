package nl.rijksoverheid.dcbs.verifier.ui.scanner

import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.schibsted.spain.barista.assertion.BaristaAssertions.assertAny
import com.schibsted.spain.barista.assertion.BaristaBackgroundAssertions.assertHasBackground
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaScrollInteractions.safelyScrollTo
import com.schibsted.spain.barista.interaction.BaristaScrollInteractions.scrollTo
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import io.mockk.mockk
import io.mockk.verify
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.fakeVerifiedQr
import nl.rijksoverheid.dcbs.verifier.ui.scanner.models.ScanResultValidData
import nl.rijksoverheid.dcbs.verifier.ui.scanner.utils.ScannerUtil
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
class ScanResultValidFragmentTest : AutoCloseKoinTest() {

    private lateinit var navController: TestNavHostController
    private val scannerUtil: ScannerUtil = mockk(relaxed = true)

    @Test
    fun `Screen shows correct content when data is Valid`() {
        launchScanResultValidFragment(data = ScanResultValidData.Valid(verifiedQr = fakeVerifiedQr()))
        assertHasBackground(R.id.root, R.color.secondary_green)
        assertDisplayed(R.id.title, R.string.scan_result_valid_title)
        assertNotDisplayed(R.id.personal_details)
    }

    @Test
    fun `Screen shows correct content when data is Demo`() {
        launchScanResultValidFragment(data = ScanResultValidData.Demo(verifiedQr = fakeVerifiedQr()))
        assertHasBackground(R.id.root, R.color.grey_2)
        assertDisplayed(R.id.title, R.string.scan_result_demo_title)
        assertNotDisplayed(R.id.personal_details)
    }

    @Test
    fun `Screen shows correct content after click`() {
        launchScanResultValidFragment(
            data = ScanResultValidData.Valid(
                verifiedQr = fakeVerifiedQr(
                    firstNameInitial = "B",
                    lastNameInitial = "N",
                    birthDay = "2",
                    birthMonth = "7"
                )
            )
        )

        // Click screen
        clickOn(R.id.root)

        // Make sure the correct title is shown in toolbar
        assertDisplayed(R.id.toolbar)
        assertAny<Toolbar>(R.id.toolbar, "Toolbar shows correct title"){
            it.title == it.context.getString(R.string.scan_result_valid_title)
        }

        // Assert correct content is displayed on screen
        assertDisplayed(R.id.personal_details)
        scrollTo(R.id.first_name_initial)
        assertDisplayed(R.id.first_name_initial, "B")
        scrollTo(R.id.last_name_initial)
        assertDisplayed(R.id.last_name_initial, "N")
        scrollTo(R.id.birth_day)
        assertDisplayed(R.id.birth_day, "02")
        scrollTo(R.id.birth_month)
        assertDisplayed(R.id.birth_month, "JUL (07)")

        // Assert button click
        safelyScrollTo(R.id.button)
        clickOn(R.id.button)
        verify { scannerUtil.launchScanner(any()) }
    }

    @Test
    fun `Screen shows correct content after 800ms`() {
        launchScanResultValidFragment(
            data = ScanResultValidData.Valid(
                verifiedQr = fakeVerifiedQr(
                    firstNameInitial = "B",
                    lastNameInitial = "N",
                    birthDay = "2",
                    birthMonth = "7"
                )
            )
        )

        // Wait 800ms until personal details are shown
        BaristaSleepInteractions.sleep(800)

        // Make sure the correct title is shown in toolbar
        assertDisplayed(R.id.toolbar)
        assertAny<Toolbar>(R.id.toolbar, "Toolbar shows correct title"){
            it.title == it.context.getString(R.string.scan_result_valid_title)
        }

        // Assert correct content is displayed on screen
        assertDisplayed(R.id.personal_details)
        scrollTo(R.id.first_name_initial)
        assertDisplayed(R.id.first_name_initial, "B")
        scrollTo(R.id.last_name_initial)
        assertDisplayed(R.id.last_name_initial, "N")
        scrollTo(R.id.birth_day)
        assertDisplayed(R.id.birth_day, "02")
        scrollTo(R.id.birth_month)
        assertDisplayed(R.id.birth_month, "JUL (07)")

        // Assert button click
        safelyScrollTo(R.id.button)
        clickOn(R.id.button)
        verify { scannerUtil.launchScanner(any()) }
    }

    @Test
    fun `Screen shows correct content with demo QR after click`() {
        launchScanResultValidFragment(
            data = ScanResultValidData.Demo(
                verifiedQr = fakeVerifiedQr(
                    firstNameInitial = "B",
                    lastNameInitial = "N",
                    birthDay = "2",
                    birthMonth = "7"
                )
            )
        )

        // Click screen
        clickOn(R.id.root)

        // Make sure the correct title is shown in toolbar
        assertDisplayed(R.id.toolbar)
        assertAny<Toolbar>(R.id.toolbar, "Toolbar shows correct title"){
            it.title == it.context.getString(R.string.scan_result_demo_title)
        }

        // Assert correct content is displayed on screen
        assertDisplayed(R.id.personal_details)
        scrollTo(R.id.first_name_initial)
        assertDisplayed(R.id.first_name_initial, "B")
        scrollTo(R.id.last_name_initial)
        assertDisplayed(R.id.last_name_initial, "N")
        scrollTo(R.id.birth_day)
        assertDisplayed(R.id.birth_day, "02")
        scrollTo(R.id.birth_month)
        assertDisplayed(R.id.birth_month, "JUL (07)")

        // Assert button click
        safelyScrollTo(R.id.button)
        clickOn(R.id.button)
        verify { scannerUtil.launchScanner(any()) }
    }

    @Test
    fun `Screen shows correct content with demo QR after 800ms`() {
        launchScanResultValidFragment(
            data = ScanResultValidData.Demo(
                verifiedQr = fakeVerifiedQr(
                    firstNameInitial = "B",
                    lastNameInitial = "N",
                    birthDay = "2",
                    birthMonth = "7"
                )
            )
        )

        // Wait 800ms until personal details are shown
        BaristaSleepInteractions.sleep(800)

        // Make sure the correct title is shown in toolbar
        assertDisplayed(R.id.toolbar)
        assertAny<Toolbar>(R.id.toolbar, "Toolbar shows correct title"){
            it.title == it.context.getString(R.string.scan_result_demo_title)
        }

        // Assert correct content is displayed on screen
        assertDisplayed(R.id.personal_details)
        scrollTo(R.id.first_name_initial)
        assertDisplayed(R.id.first_name_initial, "B")
        scrollTo(R.id.last_name_initial)
        assertDisplayed(R.id.last_name_initial, "N")
        scrollTo(R.id.birth_day)
        assertDisplayed(R.id.birth_day, "02")
        scrollTo(R.id.birth_month)
        assertDisplayed(R.id.birth_month, "JUL (07)")

        // Assert button click
        safelyScrollTo(R.id.button)
        clickOn(R.id.button)
        verify { scannerUtil.launchScanner(any()) }
    }


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
