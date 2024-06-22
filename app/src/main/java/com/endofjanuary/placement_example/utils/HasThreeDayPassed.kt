package com.endofjanuary.placement_example.utils

import java.util.concurrent.TimeUnit

fun hasFiveDaysPassed(creationTime: Long): Boolean {
    val currentTime = System.currentTimeMillis()
    val threeDaysInMillis = TimeUnit.DAYS.toMillis(5)

    return (currentTime - creationTime) >= threeDaysInMillis
}