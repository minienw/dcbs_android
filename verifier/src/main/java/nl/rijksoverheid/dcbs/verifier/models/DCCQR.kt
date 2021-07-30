package nl.rijksoverheid.dcbs.verifier.models

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import dgca.verifier.app.engine.DefaultAffectedFieldsDataRetriever
import dgca.verifier.app.engine.DefaultCertLogicEngine
import dgca.verifier.app.engine.DefaultJsonLogicValidator
import dgca.verifier.app.engine.Result
import dgca.verifier.app.engine.data.*
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableType
import nl.rijksoverheid.dcbs.verifier.utils.formatDate
import nl.rijksoverheid.dcbs.verifier.utils.yearDifference
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*


/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

class DCCQR(
    private val issuer: String?, // Country for example France
    private val issuedAt: Long?, // When was this QR issued at in seconds
    private val expirationTime: Long?, // When does this QR expire in seconds
    val dcc: DCC?
) {

    fun getName(): String {
        return dcc?.name?.retrieveLastName() + " " + (dcc?.name?.firstName
            ?: "").capitalize(Locale.getDefault())
    }

    fun getBirthDate(): String? {

        return (dcc?.dateOfBirth ?: "").formatDate()
    }

    private fun DCC.getEngineCertificateType(): CertificateType {
        return when {
            this.recoveries?.isNotEmpty() == true -> CertificateType.RECOVERY
            this.vaccines?.isNotEmpty() == true -> CertificateType.VACCINATION
            this.tests?.isNotEmpty() == true -> CertificateType.TEST
            else -> CertificateType.TEST
        }
    }

    private fun String.getValueSetMap(): Map<String, List<String>> {
        val valueSetsMap = mutableMapOf<String, List<String>>()
        val jsonObject = JsonParser.parseString(this).asJsonObject
        val entrySet: MutableSet<MutableMap.MutableEntry<String, JsonElement>> =
            jsonObject.entrySet()
        entrySet.forEach { entrySetItem ->
            val nestedKeysList = mutableListOf<String>()
            val nestedEntrySet = entrySetItem.value.asJsonObject.entrySet()
            nestedEntrySet.forEach { nestedEntrySetItem ->
                nestedKeysList.add(nestedEntrySetItem.key)
            }
            valueSetsMap[entrySetItem.key] = nestedKeysList
        }
        return valueSetsMap
    }

    private fun List<Rule>.filterRules(
        validationClock: ZonedDateTime,
        issuanceCountryIsoCode: String,
        certificateType: CertificateType,
    ): List<Rule> {
        val acceptanceRules = this.toMutableList()
        if (issuanceCountryIsoCode.isNotBlank()) {
            forEach { rule ->
                val validDate = rule.validFrom < validationClock && rule.validTo > validationClock
                val validType =
                    rule.ruleCertificateType.name == certificateType.name || rule.ruleCertificateType == RuleCertificateType.GENERAL
                val validCountry =
                    rule.countryCode.toUpperCase(Locale.getDefault()) == issuanceCountryIsoCode
                if (!validDate || !validType || !validCountry) {
                    acceptanceRules.remove(rule)
                }
            }
        }
        return acceptanceRules
    }


    fun processBusinessRules(
        from: CountryRisk,
        to: CountryRisk,
        countries: List<CountryRisk>,
        businessRules: List<Rule>,
        valueSets: String,
        payload: String,
    ): List<DCCFailableItem> {
        val failingItems = ArrayList<DCCFailableItem>()

        if (to.ruleEngineEnabled != false) {
            dcc?.let { dcc ->
                failingItems.addAll(
                    filterByRuleEngine(
                        to = to,
                        businessRules = businessRules,
                        payload = payload,
                        dcc = dcc,
                        valueSets = valueSets,
                    )
                )
            }
        }

        if (from.isIndecisive() || to.isIndecisive()) {
            return listOf(DCCFailableItem(DCCFailableType.UndecidableFrom))
        }

        if (to.getPassType() == CountryRiskPass.NLRules) {
            val age = dcc?.getDateOfBirth()?.yearDifference() ?: 99
            if (age <= 11) {
                return emptyList()
            }
            failingItems.addAll(processNLBusinessRules(from))
        }

        return failingItems
    }

    private fun filterByRuleEngine(
        to: CountryRisk,
        businessRules: List<Rule>,
        payload: String,
        dcc: DCC,
        valueSets: String,
    ): List<DCCFailableItem> {
        val objectMapper = ObjectMapper()
        val certLogicEngine = DefaultCertLogicEngine(
            jsonLogicValidator = DefaultJsonLogicValidator(),
            affectedFieldsDataRetriever = DefaultAffectedFieldsDataRetriever(
                objectMapper.readTree(
                    JSON_SCHEMA_V1
                ), objectMapper
            )
        )
        val valueSetsMap = valueSets.getValueSetMap()
        val instantExpirationTime: Instant = Instant.ofEpochMilli(this.expirationTime!!)
        val instantIssuedAt: Instant = Instant.ofEpochMilli(this.issuedAt!!)
        val externalParameter = ExternalParameter(
            validationClock = ZonedDateTime.now(ZoneId.of(ZoneOffset.UTC.id)),
            valueSets = valueSetsMap,
            countryCode = to.code ?: "",
            exp = ZonedDateTime.ofInstant(instantExpirationTime, ZoneId.systemDefault()),
            iat = ZonedDateTime.ofInstant(instantIssuedAt, ZoneId.systemDefault()),
            issuerCountryCode = to.code ?: "",
            kid = "",
        )

        val payloadData: String = JsonParser.parseString(payload).asJsonObject.get("dcc").toString()
        val validationResults =
            certLogicEngine.validate(
                dcc.getEngineCertificateType(),
                dcc.version,
                businessRules.filterRules(
                    ZonedDateTime.now(ZoneId.of(ZoneOffset.UTC.id)),
                    to.code ?: "",
                    dcc.getEngineCertificateType(),
                ),
                externalParameter,
                payloadData
            )
        val failingItems = ArrayList<DCCFailableItem>()
        validationResults.map { validationResult ->
            if (validationResult.result == Result.FAIL) {
                failingItems.add(
                    DCCFailableItem(
                        DCCFailableType.CustomFailure,
                        customMessage = validationResult.rule.descriptions["en"]
                    )
                )
            }
        }
        return failingItems
    }


    private fun processNLBusinessRules(from: CountryRisk): List<DCCFailableItem> {
        val fromColorCode = from.getColourCode()
        val failingItems = ArrayList<DCCFailableItem>()
        val requireTestInsideEU = fromColorCode == CountryColorCode.ORANGE_SHIPS_FLIGHT && from.isEU == true
        val requireTestOutsideEU = (fromColorCode == CountryColorCode.ORANGE_HIGH_INCIDENCE ||  fromColorCode == CountryColorCode.ORANGE_SHIPS_FLIGHT) && from.isEU == false
        val requireTest = requireTestOutsideEU || requireTestInsideEU
        if (requireTest && (dcc?.tests == null || dcc.tests.isEmpty())) {
            failingItems.add(DCCFailableItem(DCCFailableType.MissingRequiredTest))
        }

        return failingItems
    }

    fun shouldShowGreenOverride(from: CountryRisk, to: CountryRisk): Boolean {
        return to.getPassType() == CountryRiskPass.NLRules && from.getColourCode() == CountryColorCode.GREEN
                && from.isEU == true
    }

}