package nl.rijksoverheid.dcbs.verifier.models

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

object CountryRiskHelper {

    val GREEN = CountryRisk(
        "Green",
        "Groen",
        "",
        "",
        "code_green",
        "green",
        "pass",
        isColourCode = true,
        ruleEngineEnabled = true,
        isEU = true
    )

    val YELLOW = CountryRisk(
        "Yellow",
        "Geel",
        "",
        "",
        "code_yellow",
        "yellow",
        "pass",
        isColourCode = true,
        ruleEngineEnabled = true,
        isEU = true
    )

    val YELLOW_NEU = CountryRisk(
        "Yellow non-EU",
        "Geel niet-EU",
        "",
        "",
        "code_yellow_neu",
        "yellow",
        "pass",
        isColourCode = true,
        ruleEngineEnabled = true,
        isEU = false
    )

    val ORANGE_HIGH_RISK_NEU = CountryRisk(
        "Orange high risk non-EU",
        "Oranje hoog risico niet-EU",
        "",
        "",
        "code_orange_neu",
        "orange",
        "pass",
        isColourCode = true,
        ruleEngineEnabled = true,
        isEU = false
    )

    val ORANGE_VERY_HIGH_RISK_NEU = CountryRisk(
        "Orange very high risk non-EU",
        "Oranje zeer hoog risico niet-EU",
        "",
        "",
        "code_orange_high_incidence_neu",
        "orange_high_incidence",
        "pass",
        isColourCode = true,
        ruleEngineEnabled = true,
        isEU = false
    )

    val ORANGE_VERY_HIGH_RISK_VOC = CountryRisk(
        "Orange very high risk VOC",
        "Oranje zeer hoog risico VOC",
        "",
        "",
        "code_orange_very_high_risk",
        "orange_very_high_risk",
        "pass",
        isColourCode = true,
        ruleEngineEnabled = true,
        isEU = true
    )

    val ORANGE_VERY_HIGH_RISK_VOC_NEU = CountryRisk(
        "Orange very high risk VOC non-EU",
        "Oranje zeer hoog risico VOC niet-EU",
        "",
        "",
        "code_orange_very_high_risk_neu",
        "orange_very_high_risk",
        "pass",
        isColourCode = true,
        ruleEngineEnabled = true,
        isEU = false
    )

    val NL = CountryRisk(
        "Netherlands",
        "Nederland",
        "",
        "",
        "NL",
        "yellow",
        "pass",
        isColourCode = true,
        ruleEngineEnabled = true,
        isEU = true
    )
}