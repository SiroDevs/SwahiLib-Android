package com.swahilib.core.helpers

import android.util.Log
import com.swahilib.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import java.net.UnknownHostException

object Supabase {
    object ErrorMessages {
        const val BAD_REQUEST_EXCEPTION =
            "You have entered an invalid code, please double check or " +
                    "ask the admin to share the correct code."

        const val UNKNOWN_HOST_EXCEPTION =
            "You seem to have no network connection. \nPlease connect to a network to continue."
    }
}

private val serializer =
    KotlinXSerializer(
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        },
    )

fun createSupabase(): SupabaseClient =
    createSupabaseClient(
        supabaseUrl = BuildConfig.SupabaseUrl,
        supabaseKey = BuildConfig.SupabaseKey,
    ) {
        install(Auth)
        install(Postgrest)
        defaultSerializer = serializer
    }

private fun String?.toValidErrorMessage(default: String): String {
    return when {
        this.isNullOrBlank() -> default
        this.contains(
            "unable to resolve host",
            true,
        ) -> Supabase.ErrorMessages.UNKNOWN_HOST_EXCEPTION

        this.contains(BuildConfig.SupabaseUrl) -> this.replace(BuildConfig.SupabaseUrl, "********")
        else -> this
    }
}

suspend fun <T> safeSupabaseCall(
    defaultErrorMessage: String = "supabase failed",
    block: suspend () -> T,
): NetworkResult<T> {
    return try {
        val data = block()
        NetworkResult.Success(data)
    } catch (e: NotFoundRestException) {
        Log.d("TAG", "Resource not found.")
        NetworkResult.Failure(Exception("Resource Not Found!"))
    } catch (e: BadRequestRestException) {
        Log.d("TAG", "Bad Request.")
        NetworkResult.Failure(Exception(Supabase.ErrorMessages.BAD_REQUEST_EXCEPTION))
    } catch (e: UnknownHostException) {
        Log.d("TAG", "Unknown Host.")
        NetworkResult.Failure(Exception(Supabase.ErrorMessages.UNKNOWN_HOST_EXCEPTION))
    } catch (e: Exception) {
        Log.d("TAG", "Supabase Call Failed")
        val message = e.message.toValidErrorMessage(default = defaultErrorMessage)
        NetworkResult.Failure(Exception(message))
    }
}
