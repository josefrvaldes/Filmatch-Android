package es.josevaldes.filmatch.services

import es.josevaldes.filmatch.responses.DiscoverMoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {
    @GET("/3/discover/movie")
    suspend fun getDiscoverMovies(
        @Query("page") page: Int,
        @Query("language") language: String? = "en-US",
        @Query("sort_by") sortBy: String? = "popularity.desc"
    ): Response<DiscoverMoviesResponse>
}