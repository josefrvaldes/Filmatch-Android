package es.josevaldes.data.network

import com.google.gson.Gson
import es.josevaldes.core.utils.Either
import es.josevaldes.data.responses.ApiErrorResponse
import retrofit2.Response

object ApiResponseHandler {

    fun <T> handleApiResponse(response: Response<T>): Either<ApiErrorResponse, T> {
        return if (response.isSuccessful) {
            response.body()?.let {
                Either.Right(it)
            } ?: Either.Left(ApiErrorResponse(false, 506, "Response body is null"))
        } else {
            val errorBody = response.errorBody()?.string()
            val apiError = errorBody?.let {
                try {
                    Gson().fromJson(it, ApiErrorResponse::class.java)
                } catch (e: Exception) {
                    ApiErrorResponse(false, response.code(), errorBody)
                }
            } ?: run {
                ApiErrorResponse(false, response.code(), "Unknown error")
            }
            Either.Left(apiError)
        }
    }
}