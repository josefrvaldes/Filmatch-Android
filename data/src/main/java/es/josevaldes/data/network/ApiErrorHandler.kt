package es.josevaldes.data.network

import com.google.gson.Gson
import es.josevaldes.data.responses.ApiErrorResponse
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.results.mapErrorCodeToApiError
import es.josevaldes.data.results.mapHttpCodeToApiError
import retrofit2.Response

object ApiResponseHandler {

    fun <T> handleApiResponse(response: Response<T>): ApiResult<T> {
        return if (response.isSuccessful) {
            response.body()?.let {
                ApiResult.Success(it)
            } ?: ApiResult.Error(ApiError.Unknown)
        } else {
            val errorBody = response.errorBody()?.string()
            errorBody?.let {
                try {
                    val apiErrorResponse = Gson().fromJson(it, ApiErrorResponse::class.java)
                    ApiResult.Error( mapErrorCodeToApiError(apiErrorResponse.code))
                } catch (e: Exception) {
                    ApiResult.Error(mapHttpCodeToApiError(response.code()))
                }
            } ?: run {
                ApiResult.Error(ApiError.Unknown)
            }
        }
    }
}