package es.josevaldes.data.services

import es.josevaldes.data.responses.AuthResponse
import es.josevaldes.data.results.ApiResult
import retrofit2.http.POST

interface AuthRemoteDataSource {

    @POST("/user/auth")
    suspend fun auth(): ApiResult<AuthResponse>

}