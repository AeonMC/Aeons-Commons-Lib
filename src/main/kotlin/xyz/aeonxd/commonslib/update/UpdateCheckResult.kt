package xyz.aeonxd.commonslib.update

import kotlin.time.Duration

data class UpdateCheckResult(
    val oldVersion: String,
    val newVersion: String,
    val checkDuration: Duration
)