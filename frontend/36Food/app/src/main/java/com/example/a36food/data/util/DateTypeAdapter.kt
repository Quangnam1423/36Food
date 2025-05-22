package com.example.a36food.data.util

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateTypeAdapter : JsonSerializer<Long>, JsonDeserializer<Long> {

    // Format with 'Z' at the end
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    // Format without 'Z' at the end (matches the actual API response)
    private val isoFormatNoZ = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    // Simple format without milliseconds
    private val simpleFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun serialize(src: Long, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(isoFormatNoZ.format(Date(src)))
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Long {
        val dateStr = json.asString

        return try {
            // Try parsing with the format that matches the API response (no Z)
            isoFormatNoZ.parse(dateStr)?.time
        } catch (e: ParseException) {
            try {
                // Try parsing with Z format
                isoFormat.parse(dateStr)?.time
            } catch (e: ParseException) {
                try {
                    // Try simple format without milliseconds
                    simpleFormat.parse(dateStr)?.time
                } catch (e: ParseException) {
                    // If all parsing fails, try manual parsing
                    parseManually(dateStr)
                }
            }
        } ?: System.currentTimeMillis() // Fallback to current time if parsing fails
    }

    private fun parseManually(dateStr: String): Long {
        try {
            // Handle the date string manually by extracting components
            val datePart = dateStr.substringBefore('T')
            val timePart = dateStr.substringAfter('T')

            // Extract date components
            val yearStr = datePart.substringBefore('-')
            val monthStr = datePart.substring(yearStr.length + 1, datePart.lastIndexOf('-'))
            val dayStr = datePart.substringAfterLast('-')

            val year = yearStr.toInt()
            val month = monthStr.toInt() - 1 // Month is 0-based in Java Calendar
            val day = dayStr.toInt()

            // Extract time components
            val hourStr = timePart.substringBefore(':')
            val minuteStr = timePart.substring(hourStr.length + 1, timePart.lastIndexOf(':'))
            val secondsAndMillis = timePart.substringAfterLast(':')

            val hour = hourStr.toInt()
            val minute = minuteStr.toInt()

            val seconds: Int
            val millis: Int

            if (secondsAndMillis.contains('.')) {
                seconds = secondsAndMillis.substringBefore('.').toInt()
                val millisStr = secondsAndMillis.substringAfter('.')
                    .replace("Z", "") // Remove Z if present
                    .take(3) // Take only the first 3 digits for milliseconds
                    .padEnd(3, '0') // Pad to ensure 3 digits
                millis = millisStr.toInt()
            } else {
                seconds = secondsAndMillis.replace("Z", "").toInt()
                millis = 0
            }

            val calendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.set(year, month, day, hour, minute, seconds)
            calendar.set(java.util.Calendar.MILLISECOND, millis)

            return calendar.timeInMillis
        } catch (e: Exception) {
            e.printStackTrace()
            return System.currentTimeMillis() // Fallback to current time
        }
    }
}
