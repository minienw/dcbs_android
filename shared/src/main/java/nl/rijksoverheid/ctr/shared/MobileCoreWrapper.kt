/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

package nl.rijksoverheid.ctr.shared

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import mobilecore.Mobilecore
import mobilecore.Result
import nl.rijksoverheid.ctr.shared.ext.*
import nl.rijksoverheid.ctr.shared.models.DomesticCredential
import nl.rijksoverheid.ctr.shared.models.DomesticCredentialAttributes
import nl.rijksoverheid.ctr.shared.models.ReadDomesticCredential
import org.json.JSONObject
import java.lang.reflect.Type

interface MobileCoreWrapper {
    // returns error message, if initializing failed
    fun initializeVerifier(configFilesPath: String): String?
    fun verify(credential: ByteArray): Result
}

class MobileCoreWrapperImpl(private val moshi: Moshi) : MobileCoreWrapper {

    override fun initializeVerifier(configFilesPath: String): String? {
        return try {
            val initResult = Mobilecore.initializeVerifier(configFilesPath)
            initResult.error.takeIf { it.isNotEmpty() }
        } catch (exception: Exception) {
            exception.message ?: "unknown initializeVerifier library error"
        }
    }

    override fun verify(credential: ByteArray): Result = Mobilecore.verify(credential)
}
