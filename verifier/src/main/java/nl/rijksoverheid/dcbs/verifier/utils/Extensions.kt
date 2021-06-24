package nl.rijksoverheid.dcbs.verifier.utils

import java.text.SimpleDateFormat
import java.util.*

fun String.formatDate(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
    val date = inputFormat.parse(this)
    return outputFormat.format(date)
}