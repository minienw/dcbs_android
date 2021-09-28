package nl.rijksoverheid.dcbs.verifier.models

import com.google.gson.annotations.SerializedName

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

class DCCRecovery(
    @SerializedName("tg")
    val targetedDisease: String,
    @SerializedName("fr")
    val dateOfFirstPositiveTest: String?,
    @SerializedName("co")
    val countryOfTest: String,
    @SerializedName("is")
    val certificateIssuer: String,
    @SerializedName("df")
    val certificateValidFrom: String?,
    @SerializedName("du")
    val certificateValidTo: String?,
    @SerializedName("ci")
    val certificateIdentifier: String
)