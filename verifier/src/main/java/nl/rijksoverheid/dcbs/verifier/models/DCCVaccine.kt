package nl.rijksoverheid.dcbs.verifier.models

import com.google.gson.annotations.SerializedName

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

class DCCVaccine(
    @SerializedName("tg")
    val targetedDisease: String,
    @SerializedName("vp")
    val vaccine: String,
    @SerializedName("mp")
    val vaccineMedicalProduct: String, // EU1/20/1507 (vaccine model?)
    @SerializedName("ma")
    val marketingAuthorizationHolder: String, // ORG-100031184 issuer?
    @SerializedName("dn")
    val doseNumber: Int,
    @SerializedName("sd")
    val totalSeriesOfDoses: Int,
    @SerializedName("dt")
    val dateOfVaccination: String?,
    @SerializedName("co")
    val countryOfVaccination: String,
    @SerializedName("is")
    val certificateIssuer: String,
    @SerializedName("ci")
    val certificateIdentifier: String
) {

    fun isFullyVaccinated(): Boolean {
        return doseNumber >= totalSeriesOfDoses
    }
}