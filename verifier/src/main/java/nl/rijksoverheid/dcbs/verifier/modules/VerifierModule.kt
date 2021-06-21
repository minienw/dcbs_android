/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

package nl.rijksoverheid.dcbs.verifier.modules

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import nl.rijksoverheid.ctr.introduction.ui.new_terms.models.NewTerms
import nl.rijksoverheid.dcbs.verifier.persistance.PersistenceManager
import nl.rijksoverheid.dcbs.verifier.persistance.SharedPreferencesPersistenceManager
import nl.rijksoverheid.dcbs.verifier.ui.scanner.ScannerViewModel
import nl.rijksoverheid.dcbs.verifier.ui.scanner.ScannerViewModelImpl
import nl.rijksoverheid.dcbs.verifier.ui.scanner.datamappers.VerifiedQrDataMapper
import nl.rijksoverheid.dcbs.verifier.ui.scanner.datamappers.VerifiedQrDataMapperImpl
import nl.rijksoverheid.dcbs.verifier.ui.scanner.usecases.TestResultValidUseCase
import nl.rijksoverheid.dcbs.verifier.ui.scanner.usecases.TestResultValidUseCaseImpl
import nl.rijksoverheid.dcbs.verifier.ui.scanner.usecases.VerifyQrUseCase
import nl.rijksoverheid.dcbs.verifier.ui.scanner.usecases.VerifyQrUseCaseImpl
import nl.rijksoverheid.dcbs.verifier.ui.scanner.utils.QrCodeUtil
import nl.rijksoverheid.dcbs.verifier.ui.scanner.utils.QrCodeUtilImpl
import nl.rijksoverheid.dcbs.verifier.ui.scanner.utils.ScannerUtil
import nl.rijksoverheid.dcbs.verifier.ui.scanner.utils.ScannerUtilImpl
import nl.rijksoverheid.dcbs.verifier.ui.scanqr.ScanQrViewModel
import nl.rijksoverheid.dcbs.verifier.ui.scanqr.ScanQrViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Configure app config dependencies
 *
 * @param path Path for the public keys api, for example "keys" to fetch the config from <baseurl>/keys/public_keys
 */
fun verifierModule(path: String) = module {

    factory<NewTerms?> {
        NewTerms(version = 1, true)
    }

    single<PersistenceManager> {
        SharedPreferencesPersistenceManager(
            get()
        )
    }

    // Use cases
    factory<VerifyQrUseCase> {
        VerifyQrUseCaseImpl(get())
    }
    factory<TestResultValidUseCase> {
        TestResultValidUseCaseImpl(get())
    }
    factory<VerifiedQrDataMapper> { VerifiedQrDataMapperImpl(get(), get()) }

    // Utils
    factory<QrCodeUtil> { QrCodeUtilImpl(get()) }
    factory<ScannerUtil> { ScannerUtilImpl() }

    // ViewModels
    viewModel<ScanQrViewModel> { ScanQrViewModelImpl(get()) }
    viewModel<ScannerViewModel> { ScannerViewModelImpl(get()) }

    single {
        get<Moshi.Builder>(Moshi.Builder::class)
            .add(KotlinJsonAdapterFactory()).build()
    }
}
