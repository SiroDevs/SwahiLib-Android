package com.swahilib.core.ui.components.game

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
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
}

@Singleton
class GameSoundPlayer @Inject constructor(
    @ApplicationContext context: Context,
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
    )

    var enabled: Boolean = true

    fun play(sound: GameSound, volume: Float = 1f) {
        if (!enabled) return
        val id = soundIds[sound] ?: return
        soundPool.play(id, volume, volume, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}
