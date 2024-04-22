package org.example.lib

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun getLocalDate() =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

fun getLocalDateTime() =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
