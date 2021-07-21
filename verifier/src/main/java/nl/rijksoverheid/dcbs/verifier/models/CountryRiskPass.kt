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

enum class CountryRiskPass(val value: String) {
    Pass("pass"),
    Inconclusive("inconclusive"),
    NLRules("nl_rules");

    companion object {
        fun fromValue(value: String?): CountryRiskPass? {
            return values().firstOrNull { it.value == value }
        }
    }
}