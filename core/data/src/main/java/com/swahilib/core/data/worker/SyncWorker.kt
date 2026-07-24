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
import com.swahilib.core.network.api.KamusiApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val idiomRepo: IdiomRepo,
    private val proverbRepo: ProverbRepo,
    private val sayingRepo: SayingRepo,
    private val wordRepo: WordRepo,
    private val prefsRepo: PrefsRepo,
    private val api: KamusiApi,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.w(TAG, "⚠️ No network — skipping sync")
            return Result.success()
        }

        return try {
            Log.d(TAG, "▶ SyncWorker starting")

            coroutineScope {
                KamusiApi.Endpoint.entries.map { endpoint ->
                    async { syncEndpoint(endpoint) }
                }.forEach { it.await() }
            }

            prefsRepo.isDataLoaded = true
            prefsRepo.lastSyncedAt = System.currentTimeMillis()
            Log.d(TAG, "✅ SyncWorker done")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "❌ SyncWorker failed: ${e.message}", e)
            Result.retry()
        }
    }

    private suspend fun syncEndpoint(endpoint: KamusiApi.Endpoint) {
        val storedETag = prefsRepo.getETag(endpoint)
        val newETag = api.fetchETag(endpoint, storedETag)

        if (newETag == null) {
            Log.d(TAG, "⏭ ${endpoint.path} — no changes")
            return
        }

        Log.d(TAG, "⬇ ${endpoint.path} changed — downloading")
        val success = when (endpoint) {
            KamusiApi.Endpoint.WORDS -> wordRepo.fetchRemoteData().isSuccess
            KamusiApi.Endpoint.IDIOMS -> idiomRepo.fetchRemoteData().isSuccess
            KamusiApi.Endpoint.PROVERBS -> proverbRepo.fetchRemoteData().isSuccess
            KamusiApi.Endpoint.SAYINGS -> sayingRepo.fetchRemoteData().isSuccess
        }

        if (success) {
            prefsRepo.setETag(endpoint, newETag)
            Log.d(TAG, "💾 ${endpoint.path} ETag saved: $newETag")
        } else {
            Log.w(TAG, "⚠️ ${endpoint.path} seed failed — ETag not saved, will retry next launch")
        }
    }

    companion object {
        const val TAG = "SyncWorker"
        const val SYNC_WORK_NAME = "swahilib_sync"
    }
}
