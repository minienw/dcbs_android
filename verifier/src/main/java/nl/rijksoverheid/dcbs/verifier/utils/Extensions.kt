package nl.rijksoverheid.dcbs.verifier.utils

import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

fun String.formatDate(): String? {
    val outputFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    return try {
        this.toDate()?.let {
            outputFormat.format(it)
        } ?: run {
            this
        }
    } catch (e: Exception) {
        this
    }
}

fun String.toDate(): Date? {
    return try {
        val temporalAccessor = DateTimeFormatter.ISO_DATE_TIME.parse(this)
        val i = Instant.from(temporalAccessor)
        Date.from(i)
    } catch (e: Exception) {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        try {
            inputFormat.parse(this)
        } catch (e: Exception) {
            null
        }
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

    return if (diff.toMinutes() % 60 > 0) {
        diff.toHours().toInt() + 1
    } else diff.toHours().toInt()
}

fun Date.yearDifference(): Int {
    val localDate = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val diff: Period = Period.between(
        localDate,
        LocalDate.now()
    )
    return diff.years
}

fun Date.toLocalDate(): LocalDate {
    return this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
}

fun Date.timeAgo(
    daysLabel: String,
    hoursLabel: String,
    dayLabel: String,
    hourLabel: String,
    oldLabel: String,
): String {
    val elapsedDays = this.daysElapsed()
    val elapsedHours = this.hoursElapsed()
    var output = ""
    if (elapsedDays > 0) output += amountLabelTextTimeDate(elapsedDays.toInt(), dayLabel, daysLabel)
    if (elapsedDays > 0 || elapsedHours > 0) output += amountLabelTextTimeDate(
        elapsedHours.toInt(),
        hourLabel,
        hoursLabel
    )
    return "$output$oldLabel"
}

private fun amountLabelTextTimeDate(amount: Int, labelOne: String, labelMultiple: String): String {
    return "${if (amount > 1) labelMultiple.format(amount) else labelOne.format(amount)} "
}

fun Date.hoursElapsed(): Long {
    val secondsInMilli: Long = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    var difference: Long = System.currentTimeMillis() - this.time
    difference %= daysInMilli()
    return difference / hoursInMilli
}


fun daysInMilli(): Long {
    val secondsInMilli: Long = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    return hoursInMilli * 24
}

fun Date.daysElapsed(): Long {
    val difference: Long = System.currentTimeMillis() - this.time
    val secondsInMilli: Long = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24
    return difference / daysInMilli
}

