package com.simplemobiletools.calendar.helpers

import android.content.Context
import com.simplemobiletools.calendar.R
import com.simplemobiletools.calendar.extensions.config
import com.simplemobiletools.calendar.extensions.seconds
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat

object Formatter {
    val DAYCODE_PATTERN = "YYYYMMdd"
    val YEAR_PATTERN = "YYYY"
    val TIME_PATTERN = "HHmmss"
    private val DAY_PATTERN = "d"
    private val DAY_OF_WEEK_PATTERN = "EEE"
    private val PATTERN_TIME_12 = "hh:mm a"
    private val PATTERN_TIME_24 = "HH:mm"

    private val PATTERN_HOURS_12 = "h a"
    private val PATTERN_HOURS_24 = "HH"

    fun getDateFromCode(context: Context, dayCode: String, shortMonth: Boolean = false): String {
        val dateTime = getDateTimeFromCode(dayCode)
        val day = dateTime.toString(DAY_PATTERN)
        val year = dateTime.toString(YEAR_PATTERN)
        val monthIndex = Integer.valueOf(dayCode.substring(4, 6))!!
        var month = getMonthName(context, monthIndex)
        if (shortMonth)
            month = month.substring(0, Math.min(month.length, 3))
        var date = "$month $day"
        if (year != DateTime().toString(YEAR_PATTERN))
            date += " $year"
        return date
    }

    fun getDayTitle(context: Context, dayCode: String, addDayOfWeek: Boolean = true): String {
        val date = getDateFromCode(context, dayCode)
        val dateTime = getDateTimeFromCode(dayCode)
        val day = dateTime.toString(DAY_OF_WEEK_PATTERN)
        return if (addDayOfWeek)
            "$date ($day)"
        else
            date
    }

    fun getDate(context: Context, dateTime: DateTime, addDayOfWeek: Boolean = true) = getDayTitle(context, getDayCodeFromDateTime(dateTime), addDayOfWeek)

    fun getHours(context: Context, dateTime: DateTime) = dateTime.toString(getHourPattern(context))

    fun getTime(context: Context, dateTime: DateTime) = dateTime.toString(getTimePattern(context))

    fun getDateTimeFromCode(dayCode: String) = DateTimeFormat.forPattern(DAYCODE_PATTERN).withZone(DateTimeZone.UTC).parseDateTime(dayCode)

    fun getLocalDateTimeFromCode(dayCode: String) = DateTimeFormat.forPattern(DAYCODE_PATTERN).withZone(DateTimeZone.getDefault()).parseDateTime(dayCode)

    fun getTimeFromTS(context: Context, ts: Int) = getTime(context, getDateTimeFromTS(ts))

    fun getDayStartTS(dayCode: String) = getLocalDateTimeFromCode(dayCode).seconds()

    fun getDayEndTS(dayCode: String) = getLocalDateTimeFromCode(dayCode).plusDays(1).minusMinutes(1).seconds()

    fun getDayCodeFromDateTime(dateTime: DateTime) = dateTime.toString(DAYCODE_PATTERN)

    fun getDateTimeFromTS(ts: Int) = DateTime(ts * 1000L, DateTimeZone.getDefault())

    // use manually translated month names, as DateFormat and Joda have issues with a lot of languages
    fun getMonthName(context: Context, id: Int) = context.resources.getStringArray(R.array.months)[id - 1]

    fun getHourPattern(context: Context) = if (context.config.use24hourFormat) PATTERN_HOURS_24 else PATTERN_HOURS_12

    fun getTimePattern(context: Context) = if (context.config.use24hourFormat) PATTERN_TIME_24 else PATTERN_TIME_12

    fun getExportedTime(ts: Int): String {
        val dateTime = getDateTimeFromTS(ts)
        return "${dateTime.toString(DAYCODE_PATTERN)}T${dateTime.toString(TIME_PATTERN)}Z"
    }

    fun getDayCodeFromTS(ts: Int): String {
        val daycode = getDateTimeFromTS(ts).toString(DAYCODE_PATTERN)
        return if (daycode.isNotEmpty())
            daycode
        else
            "0"
    }
}
