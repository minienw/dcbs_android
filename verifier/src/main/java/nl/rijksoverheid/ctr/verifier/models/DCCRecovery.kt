package nl.rijksoverheid.ctr.verifier.models

import com.google.gson.annotations.SerializedName
import nl.rijksoverheid.ctr.verifier.models.data.CountryCode
import nl.rijksoverheid.ctr.verifier.models.data.TargetedDisease

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

class DCCRecovery(
    @SerializedName("tg")
    val targetedDisease: String?,
    @SerializedName("fr")
    val dateOfFirstPositiveTest: String?,
    @SerializedName("co")
    val countryOfTest: String?,
    @SerializedName("is")
    val certificateIssuer: String?,
    @SerializedName("df")
    val certificateValidFrom: String?,
    @SerializedName("du")
    val certificateValidTo: String?,
    @SerializedName("ci")
    val certificateIdentifier: String?
) {

    fun getCountryOfTest(): CountryCode? {
        return CountryCode.fromValue(countryOfTest)
    }

    fun getTargetedDisease(): TargetedDisease? {
        return TargetedDisease.fromValue(targetedDisease)
    }
}