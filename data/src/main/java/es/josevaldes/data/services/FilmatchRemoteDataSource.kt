package es.josevaldes.data.services

import retrofit2.http.POST

interface FilmatchRemoteDataSource {

    @POST("/user/content")
    fun markMediaAsVisited(mediaId: Int, status: Boolean)

}