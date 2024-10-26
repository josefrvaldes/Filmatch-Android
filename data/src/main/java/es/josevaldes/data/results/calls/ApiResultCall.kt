package es.josevaldes.data.results.calls

import es.josevaldes.core.utils.serialization.JsonMapper
import es.josevaldes.data.network.ApiResponseHandler
import es.josevaldes.data.responses.ApiErrorResponse
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.results.mapErrorCodeToApiError
import es.josevaldes.data.results.mapHttpCodeToApiError
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiResultCall<T>(private val delegate: Call<T>) :
    Call<ApiResult<T>> {

    override fun enqueue(callback: Callback<ApiResult<T>>) {
        delegate.enqueue(
            object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            val result = ApiResponseHandler.handleApiResponse(response)
                            callback.onResponse(
                                this@ApiResultCall,
                                Response.success(
                                    result
                                )
                            )
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            try {
                                val apiErrorResponse =
                                    JsonMapper.objectMapper.readValue(
                                        it,
                                        ApiErrorResponse::class.java
                                    )
                                callback.onResponse(
                                    this@ApiResultCall,
                                    Response.success(
                                        ApiResult.Error(mapErrorCodeToApiError(apiErrorResponse.code))
                                    )
                                )
                            } catch (e: Exception) {
                                callback.onResponse(
                                    this@ApiResultCall,
                                    Response.success(
                                        ApiResult.Error(mapHttpCodeToApiError(response.code()))
                                    )
                                )
                            }
                        } ?: run {
                            callback.onResponse(
                                this@ApiResultCall,
                                Response.success(
                                    ApiResult.Error(ApiError.Unknown)
                                )
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    callback.onResponse(
                        this@ApiResultCall,
                        Response.success(ApiResult.Error(ApiError.Unknown))
                    )
                }
            }
        )
    }

    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun execute(): Response<ApiResult<T>> {
        return Response.success(ApiResult.Success(delegate.execute().body()!!))
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun clone(): Call<ApiResult<T>> {
        return ApiResultCall(delegate.clone())
    }

    override fun request(): Request {
        return delegate.request()
    }

    override fun timeout(): Timeout {
        return delegate.timeout()
    }
}
