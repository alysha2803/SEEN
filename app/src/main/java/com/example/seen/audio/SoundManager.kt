package com.example.seen.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.seen.R

enum class SoundEffect {
    UI_TAP,
    LOCK_SWIPE,
    MESSAGE_OPEN,
    TENSION_STING,
    MINIGAME_START,
    MINIGAME_CORRECT,
    MINIGAME_WRONG,
    MINIGAME_COMPLETE,
    PHOTO_TAP,
    SWIPE_PHOTO,
    NOTIFICATION_POP,
    CLIMAX_STING,
    RESOLUTION_CHIME
}

val LocalSoundManager = staticCompositionLocalOf<SoundManager?> { null }

class SoundManager(context: Context) {

    private val pool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val sfxIds = mutableMapOf<SoundEffect, Int>()
    private var bgPlayer: MediaPlayer? = null
    private var bgStarted = false

    init {
        fun load(effect: SoundEffect, resId: Int) {
            runCatching { sfxIds[effect] = pool.load(context, resId, 1) }
        }
        load(SoundEffect.UI_TAP,            R.raw.ui_tap)
        load(SoundEffect.LOCK_SWIPE,        R.raw.lock_swipe)
        load(SoundEffect.MESSAGE_OPEN,      R.raw.message_open)
        load(SoundEffect.TENSION_STING,     R.raw.tension_sting)
        load(SoundEffect.MINIGAME_START,    R.raw.minigame_start)
        load(SoundEffect.MINIGAME_CORRECT,  R.raw.minigame_correct)
        load(SoundEffect.MINIGAME_WRONG,    R.raw.minigame_wrong)
        load(SoundEffect.MINIGAME_COMPLETE, R.raw.minigame_complete)
        load(SoundEffect.PHOTO_TAP,         R.raw.photo_tap)
        load(SoundEffect.SWIPE_PHOTO,       R.raw.swipe_photo)
        load(SoundEffect.NOTIFICATION_POP,  R.raw.notification_pop)
        load(SoundEffect.CLIMAX_STING,      R.raw.climax_sting)
        load(SoundEffect.RESOLUTION_CHIME,  R.raw.resolution_chime)

        runCatching {
            bgPlayer = MediaPlayer.create(context, R.raw.bg_music)?.apply {
                isLooping = true
                setVolume(0.35f, 0.35f)
            }
        }
    }

    fun play(effect: SoundEffect, volume: Float = 1f) {
        val id = sfxIds[effect] ?: return
        if (id > 0) runCatching { pool.play(id, volume, volume, 1, 0, 1f) }
    }

    fun startBgMusic() {
        if (bgStarted) return
        runCatching { bgPlayer?.start() }
        bgStarted = true
    }

    fun pauseBgMusic() {
        runCatching { if (bgPlayer?.isPlaying == true) bgPlayer?.pause() }
    }

    fun resumeBgMusic() {
        if (!bgStarted) return
        runCatching { if (bgPlayer?.isPlaying == false) bgPlayer?.start() }
    }

    fun release() {
        runCatching { bgPlayer?.release() }
        bgPlayer = null
        runCatching { pool.release() }
    }
}
