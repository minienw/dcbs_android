package nl.rijksoverheid.dcbs.verifier.ui.about

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
@Parcelize
data class AboutThisAppData(
    val versionName: String,
    val versionCode: String,
    val readMoreItems: List<ReadMoreItem> = listOf(),
    val trustListUpdatedDate: Date?
) : Parcelable {

    @Parcelize
    data class ReadMoreItem(val text: String, val url: String) : Parcelable
}
