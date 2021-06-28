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
    GREEN("code_green"),
    YELLOW("code_yellow"),
    ORANGE("code_orange"),
    ORANGE_HIGH_INCIDENCE("code_orange_high_incidence"),
    ORANGE_SHIPS_FLIGHT("code_orange_ships_flight"),
    RED("code_red");

    fun getDisplayName(context: Context): String {
        return context.getString(
            when (this) {
                GREEN -> R.string.code_green
                YELLOW -> R.string.code_yellow
                ORANGE -> R.string.code_orange
                ORANGE_HIGH_INCIDENCE -> R.string.code_orange_high_incidence
                ORANGE_SHIPS_FLIGHT -> R.string.code_orange_ships_flight
                RED -> R.string.code_red
            }
        )
    }

    companion object {
        fun fromValue(value: String?): CountryColorCode? {
            return values().firstOrNull { it.value == value }
        }

        fun fromDisplayName(context: Context, displayName: String?): CountryColorCode? {
            return values().firstOrNull { it.getDisplayName(context) == displayName }
        }
    }
}