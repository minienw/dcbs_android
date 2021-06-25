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

fun Date.setTime(hour: Int, minute: Int, second: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.set(Calendar.SECOND, second)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.HOUR_OF_DAY, hour)

    return calendar.time
}

fun Date.resetToMidnight(): Date {
    return this.setTime(0, 0, 0)
}

fun Date.resetToEndOfTheDay(): Date {
    return this.setTime(23, 59, 59)
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


