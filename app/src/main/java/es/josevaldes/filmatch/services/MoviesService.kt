package es.josevaldes.filmatch.services

import es.josevaldes.filmatch.responses.DiscoverMoviesResponse
import retrofit2.http.GET

interface MoviesService {
    @GET("/3/discover/movie")
    suspend fun getDiscoverMovies(): DiscoverMoviesResponse
}