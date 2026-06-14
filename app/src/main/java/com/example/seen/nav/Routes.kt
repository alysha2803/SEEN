package com.example.seen.nav

object Routes {
    const val START = "start"
    const val CONTENT_WARNING = "content_warning"
    const val LOCKSCREEN = "lockscreen"
    const val HOME = "home"
    const val RESOURCES = "resources"
    const val RESOLUTION = "resolution"

    const val APP_ROUTE = "app/{appId}"
    const val MINIGAME_ROUTE = "minigame/{minigameId}"

    fun app(appId: String) = "app/$appId"
    fun minigame(id: String) = "minigame/$id"
}
