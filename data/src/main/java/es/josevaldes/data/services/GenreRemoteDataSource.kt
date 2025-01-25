package es.josevaldes.data.services

import es.josevaldes.data.responses.GenresListResponse
import es.josevaldes.data.results.ApiResult
import retrofit2.http.GET

interface GenreRemoteDataSource {
    @GET("/3/genre/movie/list")
    suspend fun getAllMovieGenres(): ApiResult<GenresListResponse>


    @GET("/3/genre/tv/list")
    suspend fun getAllTvGenres(): ApiResult<GenresListResponse>
}