package com.swahilib.core.data.worker

import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager

/**
 * Guards every call site that touches [WorkManager.getInstance] against a
 * class of real-device crashes where [WorkManager.initialize] itself throws.
 *
 * The concrete case seen in production: some OEM builds misreport
 * `Build.VERSION.SDK_INT` as 34+ without actually shipping the matching
 * `JobScheduler.forNamespace()` framework method, so WorkManager's internal
 * `SystemJobScheduler` setup throws `NoSuchMethodError` (an [Error], not an
 * [Exception] - a plain try/catch won't see it). Left uncaught in
 * `Application.onCreate()`, that's a fatal, unrecoverable crash-loop for
 * every affected user.
 *
 * Call [tryInitialize] once from `Application.onCreate()`. Every scheduler
 * (notifications, widget refresh, sync workers) should check [isAvailable]
 * before calling `WorkManager.getInstance(context)` - on the (hopefully
 * rare) devices where init fails, those features are silently skipped
 * instead of crashing the app.
 */
object WorkManagerReadiness {
    private const val TAG = "WorkManagerReadiness"

    @Volatile private var attempted = false
    @Volatile private var available = false

    val isAvailable: Boolean get() = available

    fun tryInitialize(context: Context, configuration: Configuration): Boolean {
        if (attempted) return available
        attempted = true
        available = try {
            WorkManager.initialize(context.applicationContext, configuration)
            true
        } catch (t: Throwable) {
            // Deliberately catches Throwable, not just Exception - NoSuchMethodError
            // and similar framework-mismatch errors are Errors, not Exceptions.
            Log.w(TAG, "WorkManager.initialize() failed - background notifications, widget refresh, and sync are disabled on this device.", t)
            false
        }
        return available
    }
}
