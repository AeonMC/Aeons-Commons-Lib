package xyz.aeonxd.commonslib.update

import kotlin.time.Duration

data class UpdateCheckResult(
    val oldVersion: PluginVersion,
    val newVersion: PluginVersion,
    val checkDuration: Duration
)