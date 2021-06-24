package nl.rijksoverheid.dcbs.verifier.utils

import java.text.SimpleDateFormat
import java.time.*
import java.util.*

fun String.formatDate(): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
    return try {
        val date = inputFormat.parse(this)
        outputFormat.format(date)
    } catch (e: Exception) {
        null
    }
}

fun String.toDate(): Date? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    return try {
        return inputFormat.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun Date.hourDifference(): Int {
    val localDateTime = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    val diff: Duration = Duration.between(
        localDateTime,
        LocalDateTime.now()
    )
    return diff.toHours().toInt()
}

fun Date.yearDifference(): Int {
    val localDate = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val diff: Period = Period.between(
        localDate,
        LocalDate.now()
    )
    return diff.years
}


