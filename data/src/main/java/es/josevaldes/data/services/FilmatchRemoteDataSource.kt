package es.josevaldes.data.services

import es.josevaldes.data.requests.MarkMediaAsVisitedRequest
import es.josevaldes.data.responses.MarkMediaAsVisitedResponse
import es.josevaldes.data.results.ApiResult
import retrofit2.http.Body
import retrofit2.http.POST

interface FilmatchRemoteDataSource {

    @POST("/user/content")
    suspend fun markMediaAsVisited(
        @Body requestBody: MarkMediaAsVisitedRequest
    ): ApiResult<MarkMediaAsVisitedResponse>

}