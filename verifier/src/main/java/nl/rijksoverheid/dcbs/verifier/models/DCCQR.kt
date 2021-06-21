package nl.rijksoverheid.dcbs.verifier.models

import nl.rijksoverheid.dcbs.verifier.models.data.CountryCode
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

    fun getIssuer(): CountryCode? {
        return CountryCode.fromValue(issuer)
    }

    @ExperimentalStdlibApi
    fun getName(): String {
        return (dcc?.name?.lastName ?: "").uppercase() + " " + (dcc?.name?.firstName ?: "").uppercase()

    }

    fun getBirthDate(): String {
        return dcc?.dateOfBirth ?: ""
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
}