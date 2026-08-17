package com.swahilib.core.network.api

import android.util.Log
import com.swahilib.core.common.library.LibraryKeys
import com.swahilib.core.common.utils.ApiConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KamusiApi @Inject constructor(
    val client: OkHttpClient,
) {
    suspend fun fetchETag(endpoint: Endpoint, storedETag: String?): String? =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${ApiConstants.KAMUSI_API}${endpoint.path}")
                    .apply { if (storedETag != null) header("If-None-Match", storedETag) }
                    .build()

                client.newCall(request).execute().use { response ->
                    when (response.code) {
                        304 -> {
                            Log.d(TAG, "✅ ${endpoint.path} unchanged (304)")
                            null
                        }

                        200 -> {
                            val etag = response.header("ETag")
                            Log.d(TAG, "🔄 ${endpoint.path} changed — new ETag: $etag")
                            etag
                        }

                        else -> {
                            Log.w(TAG, "⚠️ ${endpoint.path} unexpected status ${response.code}")
                            null
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ ETag check failed for ${endpoint.path}: ${e.message}", e)
                null
            }
        }

    suspend inline fun <reified T> fetchJson(endpoint: Endpoint): List<T>? =
        withContext(Dispatchers.IO) {
            try {
                val request =
                    Request.Builder().url("${ApiConstants.KAMUSI_API}${endpoint.path}").build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e(TAG, "❌ ${endpoint.path} fetch failed: ${response.code}")
                        return@withContext null
                    }
                    val body = response.body.string() ?: return@withContext null
                    val json = Json { ignoreUnknownKeys = true }
                    json.decodeFromString<List<T>>(body)
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ JSON fetch failed for ${endpoint.path}: ${e.message}", e)
                null
            }
        }

    /**
     * Fetches the raw JSON body for an endpoint without decoding it into a typed list.
     * Used for Library collections, whose shapes vary (flat array vs. grouped object,
     * nested fields, ...) and are parsed per-collection by `LibraryMapper`.
     */
    suspend fun fetchRawJson(endpoint: Endpoint): String? =
        withContext(Dispatchers.IO) {
            try {
                val request =
                    Request.Builder().url("${ApiConstants.KAMUSI_API}${endpoint.path}").build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e(TAG, "❌ ${endpoint.path} fetch failed: ${response.code}")
                        return@withContext null
                    }
                    response.body.string()
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ raw fetch failed for ${endpoint.path}: ${e.message}", e)
                null
            }
        }

    enum class Endpoint(
        val path: String,
        val prefKey: String,
        val libraryCollectionKey: String? = null,
    ) {
        WORDS("kamusi/words.json", "etag_words"),
        IDIOMS("kamusi/idioms.json", "etag_idioms"),
        PROVERBS("kamusi/proverbs.json", "etag_proverbs"),
        SAYINGS("kamusi/sayings.json", "etag_sayings"),

        LIBRARY_CAPS("maktaba/caps.json", "etag_caps", LibraryKeys.CAPS),
        LIBRARY_COUNTRIES("maktaba/countries.json", "etag_countries", LibraryKeys.COUNTRIES),
        LIBRARY_FAMILY("maktaba/family.json", "etag_family", LibraryKeys.FAMILY),
        LIBRARY_FISH("maktaba/fish.json", "etag_fish", LibraryKeys.FISH),
        LIBRARY_GREETING("maktaba/greetings.json", "etag_greetings", LibraryKeys.GREETING),
        LIBRARY_INSECTS("maktaba/insects.json", "etag_insects", LibraryKeys.INSECTS),
        LIBRARY_KIDGAMES(
            "maktaba/kid_games.json",
            "etag_kid_games",
            LibraryKeys.KIDGAMES,
        ),
        LIBRARY_PUNCTUATION(
            "maktaba/punctuation.json",
            "etag_punctuation",
            LibraryKeys.PUNCTUATION
        ),
        LIBRARY_SEAS("maktaba/seas.json", "etag_seas", LibraryKeys.SEAS);

        companion object {
            fun forLibraryKey(key: String): Endpoint? =
                entries.find { it.libraryCollectionKey == key }
        }
    }

    companion object {
        const val TAG = "KamusiApi"
    }
}
