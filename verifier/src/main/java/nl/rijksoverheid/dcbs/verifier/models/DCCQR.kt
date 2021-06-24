package nl.rijksoverheid.dcbs.verifier.models

import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import nl.rijksoverheid.dcbs.verifier.models.data.DCCTestResult
import nl.rijksoverheid.dcbs.verifier.utils.formatDate
import nl.rijksoverheid.dcbs.verifier.utils.hourDifference
import nl.rijksoverheid.dcbs.verifier.utils.toDate
import nl.rijksoverheid.dcbs.verifier.utils.yearDifference
import java.util.*

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

class DCCQR(
    val credentialVersion: Int?,
    val issuer: String?, // Country for example France
    val issuedAt: Long?, // When was this QR issued at in seconds
    val expirationTime: Long?, // When does this QR expire in seconds
    val dcc: DCC?
) {

    fun getName(): String {
        return dcc?.name?.retrieveLastName() + " " + (dcc?.name?.firstName ?: "").capitalize(Locale.getDefault())
    }

    fun getBirthDate(): String? {

        return (dcc?.dateOfBirth ?: "").formatDate()
    }

    fun isSpecimen(): Boolean {
        return false
    }

    fun isDomesticDcc(): Boolean {
        return false
    }

    fun isVerified(): Boolean {
        return (expirationTime ?: 0) > Date().time
    }

    fun processBusinessRules(from: CountryColorCode?, to: String): List<DCCFailableItem> {
        val toCode = to.toLowerCase()
        from?.let {
            if (toCode == "nl") {
                return processNLBusinessRules(from, toCode)
            }
        }

        return emptyList()
    }

    private fun processNLBusinessRules(from: CountryColorCode, to: String): List<DCCFailableItem> {
        val failingItems = ArrayList<DCCFailableItem>()
        val age = dcc?.dateOfBirth?.toDate()?.let { it.yearDifference() } ?: 99
        if (age > 13 &&
            (from == CountryColorCode.ORANGE || from == CountryColorCode.RED)
        ) {
            if (dcc?.tests == null || dcc.tests.isEmpty()) {
                failingItems.add(DCCFailableItem.MissingRequiredTest)
            } else {
                for (test in dcc.tests) {
                    if (test.getTestResult() != DCCTestResult.NotDetected) {
                        failingItems.add(DCCFailableItem.TestMustBeNegative)
                    }
                    test.getTestType()?.let { testType ->
                        testType.validFor(to)?.let { maxHours ->
                            test.dateOfSampleCollection.toDate()?.let { date ->
                                if (date.hourDifference() > maxHours) {
                                    if (maxHours == 48) {
                                        failingItems.add(DCCFailableItem.TestDateExpired48)
                                    } else {
                                        failingItems.add(DCCFailableItem.TestDateExpired72)
                                    }
                                }
                            } ?: {
                                failingItems.add(DCCFailableItem.TestDateExpired72)
                            }
                        }
                    } ?: run {
                        failingItems.add(DCCFailableItem.TestDateExpired72)
                    }
                }
            }
        }
        return failingItems
    }
}