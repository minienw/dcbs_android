/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *  Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *  SPDX-License-Identifier: EUPL-1.2
 *
 */

package nl.rijksoverheid.ctr.appconfig

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.rijksoverheid.ctr.appconfig.models.AppStatus
import nl.rijksoverheid.ctr.appconfig.models.ConfigResult
import nl.rijksoverheid.ctr.appconfig.persistence.AppConfigStorageManager
import nl.rijksoverheid.ctr.appconfig.usecases.AppConfigUseCase
import nl.rijksoverheid.ctr.appconfig.usecases.AppStatusUseCase
import nl.rijksoverheid.ctr.appconfig.usecases.LoadPublicKeysUseCase
import nl.rijksoverheid.ctr.appconfig.usecases.PersistConfigUseCase
import nl.rijksoverheid.ctr.shared.MobileCoreWrapper
import nl.rijksoverheid.ctr.shared.ext.ClmobileVerifyException
import java.util.*

abstract class AppConfigViewModel : ViewModel() {
    val appStatusLiveData = MutableLiveData<AppStatus>()

    abstract fun refresh(mobileCoreWrapper: MobileCoreWrapper)
    abstract fun checkLastConfigFetchExpired(time: Long): Boolean
    abstract fun lastConfigFetchTime(): Date
}

class AppConfigViewModelImpl(
    private val appConfigUseCase: AppConfigUseCase,
    private val appStatusUseCase: AppStatusUseCase,
    private val persistConfigUseCase: PersistConfigUseCase,
    private val loadPublicKeysUseCase: LoadPublicKeysUseCase,
    private val appConfigStorageManager: AppConfigStorageManager,
    private val cachedAppConfigUseCase: CachedAppConfigUseCase,
    private val cacheDirPath: String,
    private val isVerifierApp: Boolean,
    private val versionCode: Int
) : AppConfigViewModel() {

    override fun refresh(mobileCoreWrapper: MobileCoreWrapper) {
        viewModelScope.launch {
            val configResult = appConfigUseCase.get()
            val appStatus = appStatusUseCase.get(configResult, versionCode)
            if (configResult is ConfigResult.Success) {
                persistConfigUseCase.persist(
                    appConfigContents = configResult.appConfig,
                    publicKeyContents = configResult.publicKeys
                )
                cachedAppConfigUseCase.getCachedPublicKeys()?.let {
                    loadPublicKeysUseCase.load(it)
                }
            }

            if (isVerifierApp) {
                if (!appConfigStorageManager.areConfigFilesPresent()) {
                    return@launch appStatusLiveData.postValue(AppStatus.InternetRequired)
                }

                val initializationError = mobileCoreWrapper.initializeVerifier(cacheDirPath)
                if (initializationError != null) {
                    throw ClmobileVerifyException(initializationError)
                }
            }

            appStatusLiveData.postValue(appStatus)
        }
    }

    override fun checkLastConfigFetchExpired(time: Long): Boolean {
        return appConfigUseCase.checkLastConfigFetchExpired(time)
    }

    override fun lastConfigFetchTime(): Date {
        return appConfigUseCase.lastConfigFetchTime()
    }
}
