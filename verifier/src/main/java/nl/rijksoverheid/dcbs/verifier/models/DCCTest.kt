package nl.rijksoverheid.dcbs.verifier.models

import android.content.Context
import com.google.gson.annotations.SerializedName
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.models.data.DCCTestResult
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
    val dateOfSampleCollection: String?,
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

    fun getTestResult(euRules: EURules?): DCCTestResult? {
        return when {
            isTestDetected(euRules) -> DCCTestResult.Detected
            isTestNotDetected(euRules) -> DCCTestResult.NotDetected
            else -> null
        }
    }

    private fun isTestDetected(euRules: EURules?) : Boolean {
        return testResult == (euRules?.testDetectedType ?: DCCTestResult.Detected.value)
    }

    private fun isTestNotDetected(euRules: EURules?) : Boolean {
        return testResult == (euRules?.testNotDetectedType ?: DCCTestResult.NotDetected.value)
    }

    fun getTestAge(context: Context): String? {

        dateOfSampleCollection?.toDate()?.let { date ->
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

    fun getTestAgeInHours(): Int? {

        dateOfSampleCollection?.toDate()?.let { date ->
            val localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            val diff: Duration = Duration.between(
                localDateTime,
                LocalDateTime.now()
            )
            var diffInHours = diff.toHours()
            val diffInMinutes = diff.toMinutes() % 60
            if (diffInMinutes > 0) {
                diffInHours += 1
            }
            return diffInHours.toInt()
        }

        return null
    }
}