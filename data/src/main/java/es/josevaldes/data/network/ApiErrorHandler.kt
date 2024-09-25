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
            } ?: Either.Left(ApiErrorResponse(false, 501, "Response body is null"))
        } else {
            val errorBody = response.errorBody()?.string()
            val apiError = errorBody?.let {
                Gson().fromJson(it, ApiErrorResponse::class.java)
            } ?: ApiErrorResponse(false, 501, "Unknown error")
            Either.Left(apiError)
        }
    }
}