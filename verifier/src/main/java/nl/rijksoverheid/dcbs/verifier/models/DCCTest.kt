package nl.rijksoverheid.dcbs.verifier.models

import com.google.gson.annotations.SerializedName
import nl.rijksoverheid.dcbs.verifier.models.data.*
import nl.rijksoverheid.dcbs.verifier.utils.hourDifference
import nl.rijksoverheid.dcbs.verifier.utils.toDate

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

    fun getTestDateExpiredIssue(to: String): DCCFailableItem? {

        getTestType()?.let { testType ->
            testType.validFor(to)?.let { maxHours ->
                dateOfSampleCollection.toDate()?.let { date ->
                    if (date.hourDifference() > maxHours) {
                        return if (maxHours == 48) {
                            DCCFailableItem(DCCFailableType.TestDateExpired, 48)
                        } else {
                            DCCFailableItem(DCCFailableType.TestDateExpired, 72)
                        }
                    }
                } ?: run {
                    return DCCFailableItem(DCCFailableType.TestDateExpired, 72)
                }
            }
        } ?: run {
            return DCCFailableItem(DCCFailableType.TestDateExpired, 72)
        }

        return null
    }
}