package nl.rijksoverheid.dcbs.verifier.persistance

import android.content.SharedPreferences

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
interface PersistenceManager {
    fun saveSecretKeyJson(json: String)
    fun getSecretKeyJson(): String?
    fun saveLocalTestResultJson(localTestResultJson: String)
    fun getLocalTestResultJson(): String?
    fun saveDestinationValue(value: String)
    fun getDestinationValue(): String?
    fun saveDepartureValue(value: String)
    fun getDepartureValue(): String?
}

class SharedPreferencesPersistenceManager(private val sharedPreferences: SharedPreferences) :
    PersistenceManager {

    companion object {
        const val SECRET_KEY_JSON = "SECRET_KEY_JSON"
        const val LOCAL_TEST_RESULT = "LOCAL_TEST_RESULT"
        const val DEPARTURE_VALUE = "DEPARTURE_VALUE"
        const val DESTINATION_VALUE = "DESTINATION_VALUE"
    }

    override fun saveSecretKeyJson(json: String) {
        sharedPreferences.edit().putString(SECRET_KEY_JSON, json).apply()
    }

    override fun getSecretKeyJson(): String? {
        return sharedPreferences.getString(SECRET_KEY_JSON, null)
    }

    override fun saveLocalTestResultJson(localTestResultJson: String) {
        sharedPreferences.edit().putString(LOCAL_TEST_RESULT, localTestResultJson).apply()
    }

    override fun getLocalTestResultJson(): String? {
        return sharedPreferences.getString(LOCAL_TEST_RESULT, null)
    }

    override fun saveDestinationValue(value: String) {
        sharedPreferences.edit().putString(DESTINATION_VALUE, value).apply()
    }

    override fun getDestinationValue(): String? {
        return sharedPreferences.getString(DESTINATION_VALUE, null)
    }

    override fun saveDepartureValue(value: String) {
        sharedPreferences.edit().putString(DEPARTURE_VALUE, value).apply()
    }

    override fun getDepartureValue(): String? {
        return sharedPreferences.getString(DEPARTURE_VALUE, null)
    }
}
