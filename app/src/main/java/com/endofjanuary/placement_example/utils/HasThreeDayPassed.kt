package com.endofjanuary.placement_example.utils

import java.util.concurrent.TimeUnit

fun hasThreeDaysPassed(creationTime: Long): Boolean {
    val currentTime = System.currentTimeMillis()
    val threeDaysInMillis = TimeUnit.DAYS.toMillis(3)

    return (currentTime - creationTime) >= threeDaysInMillis
}