package nl.rijksoverheid.dcbs.verifier.models

import com.google.gson.annotations.SerializedName

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

class DCC(
    @SerializedName("ver")
    val version: String, // DCC version
    @SerializedName("dob")
    val dateOfBirth: String, // Date of birth 1962-07-01
    @SerializedName("nam")
    val name: DCCNameObject,
    @SerializedName("v")
    val vaccines: List<DCCVaccine>?,
    @SerializedName("t")
    val tests: List<DCCTest>?,
    @SerializedName("r")
    val recoveries: List<DCCRecovery>?,
)