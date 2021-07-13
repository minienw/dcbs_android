package nl.rijksoverheid.dcbs.verifier.models

import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableType
import nl.rijksoverheid.dcbs.verifier.models.data.DCCTestResult
import nl.rijksoverheid.dcbs.verifier.utils.daysElapsed
import nl.rijksoverheid.dcbs.verifier.utils.formatDate
import nl.rijksoverheid.dcbs.verifier.utils.toDate
import nl.rijksoverheid.dcbs.verifier.utils.yearDifference
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
    private val expirationTime: Long?, // When does this QR expire in seconds
    val dcc: DCC?
) {

    val july17th = 1626472800.0
    val july24th = 1627077600.0

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

    fun processBusinessRules(from: CountryRisk, to: CountryRisk, countries: List<CountryRisk>): List<DCCFailableItem> {
        val failingItems = ArrayList<DCCFailableItem>()
        if (from.isIndecisive() || to.isIndecisive()) {
            return listOf(DCCFailableItem(DCCFailableType.UndecidableFrom))
        }
        failingItems.addAll(processGeneralRules(countries))

        if (to.getPassType() == CountryRiskPass.NLRules) {
            failingItems.addAll(processNLBusinessRules(from, to))
        }

        return failingItems
    }

    private fun processNLBusinessRules(from: CountryRisk, to: CountryRisk): List<DCCFailableItem> {
        val fromColorCode = from.getColourCode()
        if (fromColorCode == CountryColorCode.GREEN || fromColorCode == CountryColorCode.YELLOW) {
            return emptyList()
        }
        if (fromColorCode == CountryColorCode.RED) {
            return listOf(DCCFailableItem(DCCFailableType.RedNotAllowed))
        }
        val age = dcc?.getDateOfBirth()?.yearDifference() ?: 99
        if (age <= 11 && fromColorCode != CountryColorCode.ORANGE_SHIPS_FLIGHT) {
            return emptyList()
        }

        val failingItems = ArrayList<DCCFailableItem>()
        if (dcc?.tests == null || dcc.tests.isEmpty()) {
            failingItems.add(DCCFailableItem(DCCFailableType.MissingRequiredTest))
        }

        if (fromColorCode == CountryColorCode.ORANGE) {

            dcc?.vaccines?.forEach { vaccine ->
                val items = ArrayList<DCCFailableItem>()
                if ((vaccine.dateOfVaccination?.toDate()?.daysElapsed() ?: 0) < 15
                    && Date().time >= july17th
                    && (vaccine.dateOfVaccination?.toDate()?.time ?: 0) >= july17th
                ) {
                    items.add(DCCFailableItem(DCCFailableType.InvalidVaccine14Days))
                }
                return if (vaccine.isFullyVaccinated()) {
                    items
                } else {
                    items.add(DCCFailableItem(DCCFailableType.NeedFullVaccination))
                    items
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

        if (fromColorCode == CountryColorCode.ORANGE_SHIPS_FLIGHT) {
            failingItems.add(DCCFailableItem(DCCFailableType.RequireSecondTest, 24))
        }
        return failingItems
    }

    private fun processGeneralRules(countries: List<CountryRisk>): List<DCCFailableItem> {
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
            if (vaccine.getTargetedDisease() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidTargetDisease))
            }
            if (!vaccine.isCountryValid(countries)) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidCountryCode))
            }
            if (vaccine.dateOfVaccination?.toDate() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidVaccineDate))
            }

//            if (LocalDate.now() >= LocalDate.of(2021, 7, 10)) {
//                vaccine.dateOfVaccination.toDate()?.let { vaccinationDate ->
//                    val localDateRequiredMinimumVaccinationDate: LocalDateTime =
//                        LocalDateTime.now().minusDays(14)
//                    val requiredMinimumVaccinationDate = Date.from(
//                        localDateRequiredMinimumVaccinationDate.atZone(ZoneId.systemDefault())
//                            .toInstant()
//                    )
//                    if (vaccinationDate > requiredMinimumVaccinationDate) {
//
//                    }
//                }
//            }
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
            if (!test.isCountryValid(countries)) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidCountryCode))
            }
            if (test.dateOfSampleCollection?.toDate() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidTestDate))
            }
        }
        dcc?.recoveries?.forEach { recovery ->
            if (recovery.getTargetedDisease() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidTargetDisease))
            }
            if (!recovery.isCountryValid(countries)) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidCountryCode))
            }
            if (recovery.certificateValidTo?.toDate() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidRecoveryToDate))
            }
            if (recovery.certificateValidFrom?.toDate() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidRecoveryFromDate))
            }
            if (recovery.dateOfFirstPositiveTest?.toDate() == null) {
                failingItems.add(DCCFailableItem(DCCFailableType.InvalidRecoveryFirstTestDate))
            }
        }
        return failingItems
    }
}