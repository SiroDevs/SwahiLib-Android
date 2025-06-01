package com.swahilib.core.helpers

import android.util.Log
import com.swahilib.BuildConfig
import io.github.jan.supabase.*
import io.github.jan.supabase.exceptions.*
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import java.net.UnknownHostException

object MySupabase {

    object ErrorMessages {
        const val BAD_REQUEST_EXCEPTION =
            "You have entered an invalid code, please double check or ask the admin to share the correct code."

        const val UNKNOWN_HOST_EXCEPTION =
            "You seem to have no network connection. \nPlease connect to a network to continue."
    }

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SupabaseUrl,
            supabaseKey = BuildConfig.SupabaseKey,
        ) {
            install(Postgrest)
            defaultSerializer = KotlinXSerializer(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }
}

private fun String?.toValidErrorMessage(default: String): String {
    return when {
        this.isNullOrBlank() -> default
        this.contains("unable to resolve host", true) -> MySupabase.ErrorMessages.UNKNOWN_HOST_EXCEPTION
        this.contains(BuildConfig.SupabaseUrl) -> this.replace(BuildConfig.SupabaseUrl, "********")
        else -> this
    }
}

suspend fun <T> safeSupabaseCall(
    defaultErrorMessage: String = "Something went wrong with Supabase.",
    block: suspend () -> T,
): NetworkResult<T> {
    return try {
        val data = block()
        NetworkResult.Success(data)
    } catch (e: NotFoundRestException) {
        Log.d("Supabase", "Resource not found.")
        NetworkResult.Failure(Exception("Resource Not Found!"))
    } catch (e: BadRequestRestException) {
        Log.d("Supabase", "Bad Request.")
        NetworkResult.Failure(Exception(MySupabase.ErrorMessages.BAD_REQUEST_EXCEPTION))
    } catch (e: UnknownHostException) {
        Log.d("Supabase", "Unknown Host.")
        NetworkResult.Failure(Exception(MySupabase.ErrorMessages.UNKNOWN_HOST_EXCEPTION))
    } catch (e: Exception) {
        Log.d("Supabase", "Generic Supabase Error: ${e.message}")
        val message = e.message.toValidErrorMessage(defaultErrorMessage)
        NetworkResult.Failure(Exception(message))
    }
}
