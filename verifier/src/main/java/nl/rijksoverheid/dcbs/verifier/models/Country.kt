package nl.rijksoverheid.dcbs.verifier.models

import androidx.annotation.StringRes

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

class Country(
    val code: String,
    @StringRes val name: Int
)