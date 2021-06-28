package nl.rijksoverheid.dcbs.verifier.models

import androidx.annotation.StringRes

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

class IsoCountryInfo(
    val name: String,
    val numeric: String,
    val alpha2: String,
    val alpha3: String,
    val calling: String,
    val currency: String,
    val continent: String
)
