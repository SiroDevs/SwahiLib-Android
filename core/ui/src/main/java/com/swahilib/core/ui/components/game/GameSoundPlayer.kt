package com.swahilib.core.ui.components.game

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.swahilib.core.data.repos.utils.PrefsRepo
import com.swahilib.core.ui.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class GameSound {
    TAP,
    SUBMIT,
    CORRECT,
    WRONG,
    TICK,
    LEVEL_COMPLETE,
    LOCKED,
    TIME_UP,
    CHEER,
}

@Singleton
class GameSoundPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefsRepo: PrefsRepo,
) {
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundIds: Map<GameSound, Int> = mapOf(
        GameSound.TAP to soundPool.load(context, R.raw.game_tap, 1),
        GameSound.SUBMIT to soundPool.load(context, R.raw.game_submit, 1),
        GameSound.CORRECT to soundPool.load(context, R.raw.game_correct, 1),
        GameSound.WRONG to soundPool.load(context, R.raw.game_wrong, 1),
        GameSound.TICK to soundPool.load(context, R.raw.game_tick, 1),
        GameSound.LEVEL_COMPLETE to soundPool.load(context, R.raw.game_level_complete, 1),
        GameSound.LOCKED to soundPool.load(context, R.raw.game_locked, 1),
        GameSound.TIME_UP to soundPool.load(context, R.raw.game_timeup, 1),
        GameSound.CHEER to soundPool.load(context, R.raw.game_cheer, 1),
    )

    private var musicPlayer: MediaPlayer? = null

    var sfxEnabled: Boolean
        get() = prefsRepo.gameSfxEnabled
        set(value) {
            prefsRepo.gameSfxEnabled = value
        }

    var musicEnabled: Boolean
        get() = prefsRepo.gameMusicEnabled
        set(value) {
            prefsRepo.gameMusicEnabled = value
            if (value) resumeMusicIfNeeded() else musicPlayer?.pause()
        }

    fun play(sound: GameSound, volume: Float = 1f) {
        if (!sfxEnabled) return
        val id = soundIds[sound] ?: return
        soundPool.play(id, volume, volume, 1, 0, 1f)
    }

    fun startMusic() {
        var lastTrackIndex = -1

        val musicTracks = listOf(
            R.raw.game_music_loop_1,
            R.raw.game_music_loop_2,
            R.raw.game_music_loop_3
        )

        if (musicPlayer == null) {
            var index: Int
            do {
                index = musicTracks.indices.random()
            } while (index == lastTrackIndex)
            lastTrackIndex = index

            musicPlayer = MediaPlayer.create(context, R.raw.game_music_loop_1)?.apply {
                isLooping = true
                setVolume(0.4f, 0.4f)
            }
        }
        resumeMusicIfNeeded()
    }

    private fun resumeMusicIfNeeded() {
        val player = musicPlayer ?: return
        if (musicEnabled && !player.isPlaying) player.start()
    }

    /** Call when leaving a game screen so music doesn't keep playing over the rest of the app. */
    fun stopMusic() {
        musicPlayer?.let {
            if (it.isPlaying) it.pause()
            it.seekTo(0)
        }
    }

    fun release() {
        soundPool.release()
        musicPlayer?.release()
        musicPlayer = null
    }
}
