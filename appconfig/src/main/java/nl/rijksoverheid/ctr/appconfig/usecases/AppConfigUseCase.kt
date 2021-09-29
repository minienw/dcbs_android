/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *  Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *  SPDX-License-Identifier: EUPL-1.2
 *
 */

package nl.rijksoverheid.ctr.appconfig.usecases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.rijksoverheid.ctr.appconfig.persistence.AppConfigPersistenceManager
import nl.rijksoverheid.ctr.appconfig.models.ConfigResult
import nl.rijksoverheid.ctr.appconfig.repositories.ConfigRepository
import retrofit2.HttpException
import java.io.IOException
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

interface AppConfigUseCase {
    suspend fun get(): ConfigResult
    fun checkLastConfigFetchExpired(time: Long) : Boolean
    fun lastConfigFetchTime(): Date
}

class AppConfigUseCaseImpl(
    private val clock: Clock,
    private val appConfigPersistenceManager: AppConfigPersistenceManager,
    private val configRepository: ConfigRepository
) : AppConfigUseCase {
    override suspend fun get(): ConfigResult = withContext(Dispatchers.IO) {
        try {
            val success = ConfigResult.Success(
                appConfig = configRepository.getConfig(),
                publicKeys = configRepository.getPublicKeys(),
                businessRules = configRepository.getBusinessRules(),
                customBusinessRules = configRepository.getCustomBusinessRules(),
                valueSets = configRepository.getValueSets(),
            )
            appConfigPersistenceManager.saveAppConfigLastFetchedSeconds(
                OffsetDateTime.now(clock).toEpochSecond()
            )
            success
        } catch (e: IOException) {
            ConfigResult.Error
        } catch (e: HttpException) {
            ConfigResult.Error
        }
    }

    override fun checkLastConfigFetchExpired(time: Long) : Boolean {
        return appConfigPersistenceManager.getAppConfigLastFetchedSeconds() + time < OffsetDateTime.now(clock).toEpochSecond()
    }

    override fun lastConfigFetchTime(): Date {
        return Date(appConfigPersistenceManager.getAppConfigLastFetchedSeconds() * 1000)
    }
}
