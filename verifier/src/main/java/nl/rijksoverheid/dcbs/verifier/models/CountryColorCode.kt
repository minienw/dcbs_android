package nl.rijksoverheid.dcbs.verifier.models

import android.content.Context
import nl.rijksoverheid.dcbs.verifier.R

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

enum class CountryColorCode(val value: String) {
    GREEN("green"),
    YELLOW("yellow"),
    ORANGE("orange"),
    ORANGE_HIGH_INCIDENCE("orange_high_incidence"),
    ORANGE_SHIPS_FLIGHT("orange_very_high_risk"),
    RED("red");

    companion object {
        fun fromValue(value: String?): CountryColorCode? {
            return values().firstOrNull { it.value == value }
        }
    }
}