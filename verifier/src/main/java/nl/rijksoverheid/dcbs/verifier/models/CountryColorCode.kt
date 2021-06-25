package nl.rijksoverheid.dcbs.verifier.models

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

enum class CountryColorCode(val value: String) {
    GREEN("code_green"),
    YELLOW("code_yellow"),
    ORANGE("code_orange"),
    ORANGE_HIGH_INCIDENCE("code_orange_high_incidence"),
    ORANGE_SHIPS_FLIGHT("code_orange_ships_flight"),
    RED("code_red");

    fun getDisplayName(): String {
        return when (this) {
            GREEN -> "Groen"
            YELLOW -> "Geel"
            ORANGE -> "Oranje"
            ORANGE_HIGH_INCIDENCE -> "Oranje hoge incidentie"
            ORANGE_SHIPS_FLIGHT -> "Oranje erg hoog risco"
            RED -> "Rood"
        }
    }

    companion object {
        fun fromValue(value: String?): CountryColorCode? {
            return values().firstOrNull { it.value == value }
        }

        fun fromDisplayName(displayName: String?): CountryColorCode? {
            return values().firstOrNull { it.getDisplayName() == displayName }
        }
    }
}