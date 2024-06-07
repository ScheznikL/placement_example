package com.endofjanuary.placement_example.utils

import java.text.SimpleDateFormat
import java.util.Date

private const val DEFAULT_TIMESTAMP = 0
fun convertToReadableFormat(unixTimestamp: Long): String {
    return if (unixTimestamp > DEFAULT_TIMESTAMP) {
        val timestampAsDate = Date(unixTimestamp)
        SimpleDateFormat.getDateTimeInstance().format(timestampAsDate)
    } else {
        "No app opening registered yet."
    }
}