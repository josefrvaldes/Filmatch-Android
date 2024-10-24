package es.josevaldes.data.services

import es.josevaldes.data.model.Movie
import es.josevaldes.data.responses.DiscoverMoviesResponse
import es.josevaldes.data.results.ApiResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET("/3/discover/movie")
    suspend fun getDiscoverMovies(
        @Query("page") page: Int,
        @Query("language") language: String? = "en-US",
        @Query("sort_by") sortBy: String? = "popularity.desc"
    ): ApiResult<DiscoverMoviesResponse>


    @GET("/3/movie/{id}")
    suspend fun findById(
        @Path("id") id: Int,
        @Query("language") language: String? = "en-US",
    ): ApiResult<Movie>
}