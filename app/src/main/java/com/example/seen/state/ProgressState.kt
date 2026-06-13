package com.example.seen.state

import com.example.seen.data.AppId

data class ProgressState(
    val acceptedContentWarning: Boolean = false,
    val highestCompletedOrder: Int = 0,
    val firedBeatIds: Set<String> = emptySet(),
    val visitedApps: Set<AppId> = emptySet(),
    val finished: Boolean = false
)
