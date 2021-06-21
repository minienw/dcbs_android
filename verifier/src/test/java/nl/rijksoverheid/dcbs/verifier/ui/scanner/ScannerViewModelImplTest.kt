package nl.rijksoverheid.dcbs.verifier.ui.scanner

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import nl.rijksoverheid.ctr.shared.livedata.Event
import nl.rijksoverheid.dcbs.verifier.fakeTestResultValidUseCase
import nl.rijksoverheid.dcbs.verifier.fakeVerifiedQr
import nl.rijksoverheid.dcbs.verifier.ui.scanner.models.VerifiedQrResultState
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
class ScannerViewModelImplTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val loadingMockedObserver: Observer<Event<Boolean>> = mockk(relaxed = true)
    private val validatedQrObserver: Observer<Event<VerifiedQrResultState>> = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @Test
    fun `Validating test result delegates to correct livedatas`() = runBlocking {
        val viewModel = ScannerViewModelImpl(testResultValidUseCase = fakeTestResultValidUseCase())

        viewModel.loadingLiveData.observeForever(loadingMockedObserver)
        viewModel.verifiedQrResultStateLiveData.observeForever(validatedQrObserver)

        viewModel.validate("")

        verifyOrder {
            loadingMockedObserver.onChanged(Event(true))
            loadingMockedObserver.onChanged(Event(false))
        }

        verify {
            validatedQrObserver.onChanged(
                Event(
                    VerifiedQrResultState.Valid(
                        verifiedQr = fakeVerifiedQr()
                    )
                )
            )
        }
    }
}
