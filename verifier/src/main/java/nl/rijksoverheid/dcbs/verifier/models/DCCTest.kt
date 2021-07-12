package nl.rijksoverheid.dcbs.verifier.models

import android.content.Context
import com.google.gson.annotations.SerializedName
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.models.data.*
import nl.rijksoverheid.dcbs.verifier.utils.hourDifference
import nl.rijksoverheid.dcbs.verifier.utils.toDate
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

class DCCTest(
    @SerializedName("tg")
    val targetedDisease: String,
    @SerializedName("tt")
    val typeOfTest: String,
    @SerializedName("nm")
    val NAATestName: String?,
    @SerializedName("ma")
    val RATTestNameAndManufac: String?,
    @SerializedName("sc")
    val dateOfSampleCollection: String,
    @SerializedName("tr")
    val testResult: String,
    @SerializedName("tc")
    val testingCentre: String?,
    @SerializedName("co")
    val countryOfTest: String,
    @SerializedName("is")
    val certificateIssuer: String,
    @SerializedName("ci")
    val certificateIdentifier: String
) {

    fun getTargetedDisease(): TargetedDisease? {
        return TargetedDisease.fromValue(targetedDisease)
    }

    fun getTestType(): DCCTestType? {
        return DCCTestType.fromValue(typeOfTest)
    }

    fun getTestResult(): DCCTestResult? {
        return DCCTestResult.fromValue(testResult)
    }

    fun getTestManufacturer(): DCCTestManufacturer? {
        return DCCTestManufacturer.fromValue(RATTestNameAndManufac)
    }

    fun getTestDateExpiredIssue(to: CountryRisk): DCCFailableItem? {

        getTestType()?.let { testType ->
            testType.validFor(to)?.let { maxHours ->
                dateOfSampleCollection.toDate()?.let { date ->
                    if (date.hourDifference() > maxHours) {
                        return DCCFailableItem(DCCFailableType.TestDateExpired, date.hourDifference())
                    }
                }
            }
        }

        return null
    }

    fun getTestAge(context: Context): String? {

        dateOfSampleCollection.toDate()?.let { date ->
            val localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            val diff: Duration = Duration.between(
                localDateTime,
                LocalDateTime.now()
            )
            val diffInHours = diff.toHours()
            val diffInMinutes = diff.toMinutes() % 60
            return context.getString(R.string.test_ago_x, diffInHours, diffInMinutes)
        }

        return null
    }

    fun isCountryValid(countries: List<CountryRisk>) : Boolean {
        return countries.find {it.code == countryOfTest } != null
    }
}