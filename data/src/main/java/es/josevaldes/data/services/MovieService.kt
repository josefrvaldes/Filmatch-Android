package es.josevaldes.data.services

import es.josevaldes.data.responses.DiscoverMoviesResponse
import es.josevaldes.data.responses.MovieResponse
import es.josevaldes.data.results.ApiResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET("/3/discover/movie")
    suspend fun getDiscoverMovies(
        @Query("page") page: Int,
        @Query("language") language: String? = "en-US",
        @Query("sort_by") sortBy: String? = "popularity.desc",
        @Query("with_genres") withGenres: String? = null,
        @Query("with_watch_providers") withProviders: String? = null,
        @Query("primary_release_date.gte") withReleaseYearFrom: String? = null,
        @Query("primary_release_date.lte") withReleaseYearTo: String? = null,
        @Query("vote_average.gte") withVoteAverageGte: Float? = null,
        @Query("with_runtime.lte") withDuration: Int? = null,
    ): ApiResult<DiscoverMoviesResponse>

    @GET("/3/movie/{id}?append_to_response=credits,videos")
    suspend fun findById(
        @Path("id") id: Int,
        @Query("language") language: String? = "en-US",
    ): ApiResult<MovieResponse>
}