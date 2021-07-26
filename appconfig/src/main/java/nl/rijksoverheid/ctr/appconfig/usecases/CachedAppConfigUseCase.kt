package nl.rijksoverheid.ctr.appconfig

import com.squareup.moshi.Moshi
import dgca.verifier.app.engine.data.Rule
import nl.rijksoverheid.ctr.appconfig.api.model.AppConfig
import nl.rijksoverheid.ctr.appconfig.persistence.AppConfigStorageManager
import nl.rijksoverheid.ctr.shared.ext.toObject
import okio.BufferedSource
import java.io.File

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

interface CachedAppConfigUseCase {
    fun getCachedAppConfigRaw(): String?
    fun getCachedAppConfig(): AppConfig?
    fun getCachedAppConfigMaxValidityHours(): Int
    fun getCachedAppConfigVaccinationEventValidity(): Int
    fun getCachedPublicKeys(): BufferedSource?
    fun getCachedBusinessRulesRaw(): String?
    fun getProviderName(providerIdentifier: String?): String
    fun getCachedValueSetsRaw(): String?
}

class CachedAppConfigUseCaseImpl constructor(
    private val appConfigStorageManager: AppConfigStorageManager,
    private val cacheDir: String,
    private val moshi: Moshi
) : CachedAppConfigUseCase {

    override fun getCachedAppConfigRaw(): String? {
        val configFile = File(cacheDir, "config.json")
        return appConfigStorageManager.getFileAsBufferedSource(configFile)?.readUtf8()
    }

    override fun getCachedAppConfig(): AppConfig? {
        val configFile = File(cacheDir, "config.json")
        return appConfigStorageManager.getFileAsBufferedSource(configFile)?.readUtf8()
            ?.toObject(moshi)
    }

    override fun getCachedAppConfigMaxValidityHours(): Int {
        return getCachedAppConfig()?.maxValidityHours
            ?: throw IllegalStateException("AppConfig should be cached")
    }

    override fun getCachedAppConfigVaccinationEventValidity(): Int {
        return getCachedAppConfig()?.vaccinationEventValidity
            ?: throw IllegalStateException("AppConfig should be cached")
    }

    override fun getCachedPublicKeys(): BufferedSource? {
        val publicKeysFile = File(cacheDir, "public_keys.json")
        return appConfigStorageManager.getFileAsBufferedSource(publicKeysFile)
    }

    override fun getCachedBusinessRulesRaw(): String? {
        val businessRulesFile = File(cacheDir, "business_rules.json")
        return appConfigStorageManager.getFileAsBufferedSource(businessRulesFile)?.readUtf8()
    }

    override fun getCachedValueSetsRaw(): String? {
        val valueSetsFile = File(cacheDir, "value_sets.json")
        return appConfigStorageManager.getFileAsBufferedSource(valueSetsFile)?.readUtf8()
    }


    override fun getProviderName(providerIdentifier: String?): String {
        return getCachedAppConfig()?.providerIdentifiers?.firstOrNull { provider -> provider.code == providerIdentifier }?.name
            ?: ""
    }
}
