package nl.rijksoverheid.dcbs.verifier

import nl.rijksoverheid.ctr.api.apiModule
import nl.rijksoverheid.ctr.appconfig.*
import nl.rijksoverheid.ctr.design.designModule
import nl.rijksoverheid.ctr.introduction.introductionModule
import nl.rijksoverheid.ctr.shared.SharedApplication
import nl.rijksoverheid.ctr.shared.sharedModule
import nl.rijksoverheid.dcbs.verifier.modules.*
import nl.rijksoverheid.dcbs.verifier.modules.verifierIntroductionModule
import nl.rijksoverheid.dcbs.verifier.modules.verifierMobileCoreModule
import nl.rijksoverheid.dcbs.verifier.modules.verifierModule
import nl.rijksoverheid.dcbs.verifier.modules.verifierPreferenceModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
open class VerifierApplication : SharedApplication() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@VerifierApplication)
            modules(
                apiModule(
                    BuildConfig.BASE_API_URL,
                    BuildConfig.SIGNATURE_CERTIFICATE_CN_MATCH,
                    BuildConfig.FEATURE_CORONA_CHECK_API_CHECKS,
                    BuildConfig.FEATURE_TEST_PROVIDER_API_CHECKS
                ),
                verifierModule("verifier"),
                verifierIntroductionModule,
                sharedModule,
                appConfigModule("verifier", BuildConfig.VERSION_CODE),
                introductionModule,
                *getAdditionalModules().toTypedArray(),
                designModule
            )
        }
    }

    override fun getAdditionalModules(): List<Module> {
        return listOf(verifierPreferenceModule, verifierMobileCoreModule)
    }
}
