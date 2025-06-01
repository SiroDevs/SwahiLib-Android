package com.swahilib.core.helpers

import android.util.Log
import com.swahilib.data.models.ErrorResponse
import io.ktor.client.call.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

sealed interface NetworkResult<out T> {
    data class Failure(val exception: Exception) : NetworkResult<Nothing>

    data class Success<T>(val data: T) : NetworkResult<T>
}

suspend inline fun <reified T> safeApiCall(block: () -> HttpResponse): NetworkResult<T> {
    return try {
        val response = block.invoke()
        NetworkResult.Success(data = response.body())
    } catch (e: NoTransformationFoundException) {
        Log.d("TAG", e.toString())
        NetworkResult.Failure(exception = e)
    } catch (e: DoubleReceiveException) {
        Log.d("TAG", e.toString())
        NetworkResult.Failure(exception = e)
    } catch (e: Exception) {
        Log.d("TAG", e.toString())
        NetworkResult.Failure(exception = e)
    }
}

suspend inline fun <reified T> safeGithubApiCall(block: () -> HttpResponse): NetworkResult<T> {
    return try {
        val response = block.invoke()
        if (response.status.isSuccess()) {
            NetworkResult.Success(data = response.body())
        } else {
            NetworkResult.Failure(exception = Exception(response.body<ErrorResponse>().message))
        }
    } catch (e: Exception) {
        NetworkResult.Failure(e)
    }
}
