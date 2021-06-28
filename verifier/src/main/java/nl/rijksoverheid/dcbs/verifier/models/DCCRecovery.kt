package nl.rijksoverheid.dcbs.verifier.models

import com.google.gson.annotations.SerializedName
import nl.rijksoverheid.dcbs.verifier.models.data.TargetedDisease
import nl.rijksoverheid.dcbs.verifier.utils.resetToEndOfTheDay
import nl.rijksoverheid.dcbs.verifier.utils.resetToMidnight
import nl.rijksoverheid.dcbs.verifier.utils.toDate
import java.util.*

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
    val dateOfFirstPositiveTest: String,
    @SerializedName("co")
    val countryOfTest: String,
    @SerializedName("is")
    val certificateIssuer: String,
    @SerializedName("df")
    val certificateValidFrom: String,
    @SerializedName("du")
    val certificateValidTo: String,
    @SerializedName("ci")
    val certificateIdentifier: String
) {

    fun getTargetedDisease(): TargetedDisease? {
        return TargetedDisease.fromValue(targetedDisease)
    }

    fun isValidRecovery() : Boolean {
        val from = certificateValidFrom.toDate()
        val to = certificateValidTo.toDate()
        return if (from != null && to != null) {
            val now = Date()
            now.after(from.resetToMidnight()) && now.before(to.resetToEndOfTheDay())
        } else false

    }

    fun isCountryValid() : Boolean {
        return IsoCountries.countryForCode(countryOfTest) != null
    }
}