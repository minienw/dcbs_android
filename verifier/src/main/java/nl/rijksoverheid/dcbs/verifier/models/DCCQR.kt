package nl.rijksoverheid.dcbs.verifier.models

import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableType
import nl.rijksoverheid.dcbs.verifier.models.data.DCCTestResult
import nl.rijksoverheid.dcbs.verifier.utils.formatDate
import nl.rijksoverheid.dcbs.verifier.utils.toDate
import nl.rijksoverheid.dcbs.verifier.utils.yearDifference
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
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
        return dcc?.name?.retrieveLastName() + " " + (dcc?.name?.firstName
            ?: "").capitalize(Locale.getDefault())
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
        val failingItems = ArrayList<DCCFailableItem>()
        failingItems.addAll(processGeneralRules())

        val toCode = to.toLowerCase(Locale.getDefault())
        from?.let {
            if (toCode == "nl") {
                failingItems.addAll(processNLBusinessRules(from, toCode))
            }
        }

        return failingItems
    }

    private fun processNLBusinessRules(from: CountryColorCode, to: String): List<DCCFailableItem> {
        if (from == CountryColorCode.GREEN || from == CountryColorCode.YELLOW) {
            return emptyList()
        }
        if (from == CountryColorCode.RED) {
            return listOf(DCCFailableItem(DCCFailableType.RedNotAllowed))
        }
        val age = dcc?.getDateOfBirth()?.let { it.yearDifference() } ?: 99
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
            failingItems.add(DCCFailableItem(DCCFailableType.RequireSecondTest, 24))
        }
        return failingItems
    }

    private fun processGeneralRules(): List<DCCFailableItem> {
        val failingItems = ArrayList<DCCFailableItem>()
        dcc?.getDateOfBirth()?.let {
            if (!dcc.isValidDateOfBirth()) {
                failingItems.add(DCCFailableItem(DCCFailableType.DateOfBirthOutOfRange))
            }
        } ?: run {
            failingItems.add(DCCFailableItem(DCCFailableType.InvalidDateOfBirth))
        }

        dcc?.vaccines?.forEach { vaccine ->
            if (vaccine.getMarketingHolder() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidVaccineHolder))
            }
            if (vaccine.getVaccine() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidVaccineType))
            }
            if (vaccine.getVaccineProduct() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidVaccineProduct))
            }
            if (!vaccine.isCountryValid()) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidCountryCode))
            }
            if (vaccine.dateOfVaccination.toDate() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidVaccineDate))
            } else if (LocalDate.now() >= LocalDate.of(2021, 7, 10)) {
                vaccine.dateOfVaccination.toDate()?.let { vaccinationDate ->
                    val localDateRequiredMinimumVaccinationDate: LocalDateTime =
                        LocalDateTime.now().minusDays(14)
                    val requiredMinimumVaccinationDate = Date.from(
                        localDateRequiredMinimumVaccinationDate.atZone(ZoneId.systemDefault())
                            .toInstant()
                    )
                    if (vaccinationDate > requiredMinimumVaccinationDate) {
                        failingItems.add(DCCFailableItem(DCCFailableType.InvalidVaccine14Days))
                    }
                }

            }
        }

        dcc?.tests?.forEach { test ->
            if (test.getTestResult() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidTestResult))
            }
            if (test.getTestType() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidTestType))
            }
            if (test.getTargetedDisease() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidTargetDisease))
            }
            if (!test.isCountryValid()) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidCountryCode))
            }
            if (test.dateOfSampleCollection.toDate() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidTestDate))
            }
        }
        dcc?.recoveries?.forEach { recovery ->
            if (recovery.getTargetedDisease() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidTargetDisease))
            }
            if (!recovery.isCountryValid()) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidCountryCode))
            }
            if (recovery.certificateValidTo.toDate() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidRecoveryToDate))
            }
            if (recovery.certificateValidFrom.toDate() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidRecoveryFromDate))
            }
            if (recovery.dateOfFirstPositiveTest.toDate() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidRecoveryFirstTestDate))
            }
        }
        return failingItems
    }
}