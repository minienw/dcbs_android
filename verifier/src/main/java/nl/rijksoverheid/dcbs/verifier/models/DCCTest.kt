package nl.rijksoverheid.dcbs.verifier.models

import com.google.gson.annotations.SerializedName
import nl.rijksoverheid.dcbs.verifier.models.data.*

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

class DCCTest(
    @SerializedName("tg")
    val targetedDisease: String?,
    @SerializedName("tt")
    val typeOfTest: String?,
    @SerializedName("nm")
    val NAATestName: String?,
    @SerializedName("ma")
    val RATTestNameAndManufac: String?,
    @SerializedName("sc")
    val dateOfSampleCollection: String?,
    @SerializedName("tr")
    val testResult: String?,
    @SerializedName("tc")
    val testingCentre: String?,
    @SerializedName("co")
    val countryOfTest: String?,
    @SerializedName("is")
    val certificateIssuer: String?,
    @SerializedName("ci")
    val certificateIdentifier: String?
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
}