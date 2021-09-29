package nl.rijksoverheid.ctr.appconfig.usecases

import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.rijksoverheid.ctr.appconfig.persistence.AppConfigPersistenceManager
import nl.rijksoverheid.ctr.appconfig.persistence.AppConfigStorageManager
import nl.rijksoverheid.ctr.appconfig.persistence.StorageResult
import java.io.File

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

interface PersistConfigUseCase {
    suspend fun persist(
        appConfigContents: String,
        publicKeyContents: String,
        businessRulesContent: String,
        customBusinessRulesContent: String,
        valueSetsContent: String
    ): StorageResult
}

class PersistConfigUseCaseImpl(
    private val appConfigPersistenceManager: AppConfigPersistenceManager,
    private val appConfigStorageManager: AppConfigStorageManager,
    private val isVerifierApp: Boolean,
    private val cacheDir: String,
) : PersistConfigUseCase {

    override suspend fun persist(appConfigContents: String,
                                 publicKeyContents: String,
                                 businessRulesContent: String,
                                 customBusinessRulesContent: String,
                                 valueSetsContent: String) =
        withContext(Dispatchers.IO) {

            val publicKeysFile = File(cacheDir, "public_keys.json")
            val publicKeysStorageResult = appConfigStorageManager.storageFile(publicKeysFile, publicKeyContents)
            if (publicKeysStorageResult is StorageResult.Error) {
                return@withContext publicKeysStorageResult
            }

            val configFile = File(cacheDir, "config.json")
            val configStorageResult = appConfigStorageManager.storageFile(configFile, appConfigContents)
            if (configStorageResult is StorageResult.Error) {
                return@withContext configStorageResult
            }

            val businessRulesFile = File(cacheDir, "business_rules.json")
            val businessRulesStorageResult = appConfigStorageManager.storageFile(businessRulesFile, businessRulesContent)
            if (businessRulesStorageResult is StorageResult.Error) {
                return@withContext businessRulesStorageResult
            }

            val customBusinessRulesFile = File(cacheDir, "custom_business_rules.json")
            val customBusinessRulesStorageResult = appConfigStorageManager.storageFile(customBusinessRulesFile, customBusinessRulesContent)
            if (customBusinessRulesStorageResult is StorageResult.Error) {
                return@withContext customBusinessRulesStorageResult
            }

            val valueSetsFile = File(cacheDir, "value_sets.json")
            val valueSetsStorageResult = appConfigStorageManager.storageFile(valueSetsFile, valueSetsContent)
            if (valueSetsStorageResult is StorageResult.Error) {
                return@withContext valueSetsStorageResult
            }

            return@withContext StorageResult.Success
        }
}
