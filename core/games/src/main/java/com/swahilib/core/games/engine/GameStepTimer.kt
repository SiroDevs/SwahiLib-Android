package com.swahilib.core.games.engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Simple per-step countdown. [onTick] fires once immediately with the full
 * duration and then every second thereafter; [onExpire] fires once when it
 * hits zero, at which point the caller is expected to auto-advance to the
 * next step rather than wait for the player.
 */
class GameStepTimer(
    private val scope: CoroutineScope,
    private val onTick: (secondsRemaining: Int) -> Unit,
    private val onExpire: () -> Unit,
) {
    private var job: Job? = null

    fun start(totalSeconds: Int) {
        job?.cancel()
        job = scope.launch {
            var remaining = totalSeconds
            onTick(remaining)
            while (remaining > 0) {
                delay(1000)
                remaining -= 1
                onTick(remaining)
            }
            onExpire()
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
