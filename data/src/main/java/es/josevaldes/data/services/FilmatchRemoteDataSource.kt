package es.josevaldes.data.services

import es.josevaldes.data.requests.MarkMediaAsVisitedRequest
import es.josevaldes.data.responses.GetVisitStatusResponse
import es.josevaldes.data.responses.GetVisitsByIdsResponse
import es.josevaldes.data.responses.MarkMediaAsVisitedResponse
import es.josevaldes.data.results.ApiResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmatchRemoteDataSource {

    @POST("/user/content")
    suspend fun markMediaAsVisited(
        @Body requestBody: MarkMediaAsVisitedRequest
    ): ApiResult<MarkMediaAsVisitedResponse>

    @GET("/visit/movie/{id}/status")
    suspend fun getMovieVisitStatus(
        @Path("id") status: Int
    ): ApiResult<GetVisitStatusResponse>

    @GET("/visit/tv/{id}/status")
    suspend fun getTvVisitStatus(
        @Path("id") status: Int
    ): ApiResult<GetVisitStatusResponse>

    @GET("/visited/tv")
    suspend fun getTvVisitsByIds(
        @Query("ids", encoded = true) ids: String
    ): ApiResult<GetVisitsByIdsResponse>

    @GET("/visited/movie")
    suspend fun getMovieVisitsByIds(
        @Query("ids") ids: String
    ): ApiResult<GetVisitsByIdsResponse>

}