package es.josevaldes.data.repositories

import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.AuthRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val _authRemoteDataSource: AuthRemoteDataSource
) {

    fun auth(): Flow<ApiResult<Unit>> = flow {
        val result = _authRemoteDataSource.auth()
        if (result is ApiResult.Success) {
            if (result.data.success) {
                emit(ApiResult.Success(Unit))
            } else {
                emit(ApiResult.Error(ApiError.InvalidCredentials))
            }
        } else if (result is ApiResult.Error) {
            emit(result)
        }
    }.catch { _ ->
        emit(ApiResult.Error(ApiError.Unknown))
    }
}