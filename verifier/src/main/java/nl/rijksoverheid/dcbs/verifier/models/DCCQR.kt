package nl.rijksoverheid.dcbs.verifier.models

import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableType
import nl.rijksoverheid.dcbs.verifier.models.data.DCCTestResult
import nl.rijksoverheid.dcbs.verifier.models.data.DCCTestType
import nl.rijksoverheid.dcbs.verifier.utils.formatDate
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
        if (from == CountryColorCode.GREEN || from == CountryColorCode.YELLOW) {
            return emptyList()
        }
        if (from == CountryColorCode.RED) {
            return listOf(DCCFailableItem(DCCFailableType.RedNotAllowed))
        }
        val age = dcc?.dateOfBirth?.toDate()?.let { it.yearDifference() } ?: 99
        if (age <= 11 && from != CountryColorCode.ORANGE_SHIPS_FLIGHT) {
            return emptyList()
        }

        val failingItems = ArrayList<DCCFailableItem>()
        if (dcc?.tests == null || dcc.tests.isEmpty()) {
            failingItems.add(DCCFailableItem(DCCFailableType.MissingRequiredTest))
        }

        if (from == CountryColorCode.ORANGE) {

            dcc?.vaccines?.forEach { vaccine ->
                return if (vaccine.isFullyVaccinated()) {
                    emptyList()
                } else {
                    listOf(DCCFailableItem(DCCFailableType.NeedFullVaccination))
                }
            }

            dcc?.recoveries?.forEach { recovery ->
                return if (recovery.isValidRecovery()) {
                    emptyList()
                } else {
                    listOf(DCCFailableItem(DCCFailableType.RecoveryNotValid))
                }
            }
        }
        dcc?.tests?.forEach { test ->
            if (test.getTestResult() != DCCTestResult.NotDetected) {
                failingItems.add(DCCFailableItem(DCCFailableType.TestMustBeNegative))
            }
            test.getTestDateExpiredIssue(to)?.let {
                failingItems.add(it)
            }
        }

        if (from == CountryColorCode.ORANGE_SHIPS_FLIGHT) {
            failingItems.add(DCCFailableItem(DCCFailableType.RequireSecondTest, 24, DCCTestType.RapidImmune.getDisplayName()))
        }
        return failingItems
    }
}