package nl.rijksoverheid.dcbs.verifier.models

import com.google.gson.annotations.SerializedName
import java.util.*

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

class DCCNameObject(
    @SerializedName("fn")
    val firstName: String?,
    @SerializedName("gn")
    val lastName: String?,
    @SerializedName("gnt")
    val lastNameStandardised: String
) {

    fun retrieveLastName(): String {
        return lastName ?: lastNameStandardised.toLowerCase(Locale.getDefault()).capitalize(Locale.getDefault())
    }
}