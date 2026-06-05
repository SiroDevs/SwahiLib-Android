package com.swahilib.core.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.swahilib.core.common.helpers.NetworkUtils
import com.swahilib.core.data.repos.IdiomRepo
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.data.repos.ProverbRepo
import com.swahilib.core.data.repos.SayingRepo
import com.swahilib.core.data.repos.WordRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Background worker that fetches all dictionary data from Supabase and
 * persists it to the local Room database.
 *
 * Enqueued in two scenarios:
 *  1. First install  – runs immediately, app proceeds to HOME straight away.
 *  2. Once per day   – re-syncs changes without blocking the UI.
 *
 * Uses @HiltWorker so all repo dependencies are injected by Hilt.
 * Register [HiltWorkerFactory] in [SwahiLibApp] (see app module changes).
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val idiomRepo: IdiomRepo,
    private val proverbRepo: ProverbRepo,
    private val sayingRepo: SayingRepo,
    private val wordRepo: WordRepo,
    private val prefsRepo: PrefsRepo,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.w(TAG, "No network – retrying later")
            // Retry: WorkManager will back-off and try again when network is available
            return Result.retry()
        }

        return try {
            Log.d(TAG, "▶ SyncWorker starting…")

            coroutineScope {
                val idioms   = async { idiomRepo.fetchRemoteData() }
                val proverbs = async { proverbRepo.fetchRemoteData() }
                val sayings  = async { sayingRepo.fetchRemoteData() }
                val words    = async { wordRepo.fetchRemoteData() }

                idioms.await()
                proverbs.await()
                sayings.await()
                words.await()
            }

            prefsRepo.isDataLoaded = true
            prefsRepo.lastSyncedAt = System.currentTimeMillis()
            Log.d(TAG, "✅ SyncWorker completed successfully")
            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "❌ SyncWorker failed: ${e.message}", e)
            // Retry up to WorkManager's default back-off limit
            Result.retry()
        }
    }

    companion object {
        const val TAG = "SyncWorker"
        /** Unique name for the one-time daily sync request so duplicates are ignored. */
        const val DAILY_SYNC_WORK_NAME = "swahilib_daily_sync"
        /** Unique name for the first-install sync request. */
        const val INSTALL_SYNC_WORK_NAME = "swahilib_install_sync"
    }
}
