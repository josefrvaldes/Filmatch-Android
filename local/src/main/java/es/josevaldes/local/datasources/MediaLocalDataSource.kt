package es.josevaldes.local.datasources

import es.josevaldes.local.dao.VisitedMediaDao
import es.josevaldes.local.entities.InterestStatus
import es.josevaldes.local.entities.MediaEntityType
import es.josevaldes.local.entities.VisitedFiltersEntity
import es.josevaldes.local.entities.VisitedMediaWithItem
import javax.inject.Inject

class MediaLocalDataSource @Inject constructor(
    private val moviesDao: VisitedMediaDao
) {

    suspend fun saveVisitedMedia(visitedMediaItemEntity: VisitedMediaWithItem) {
        moviesDao.insertVisitedMediaWithItem(visitedMediaItemEntity)
    }

    suspend fun isMovieVisited(movieId: Int, type: MediaEntityType): Boolean {
        return moviesDao.isVisitedMedia(movieId, type)
    }

    suspend fun getMediaStatus(mediaId: Int, type: MediaEntityType): InterestStatus? {
        return moviesDao.getMediaStatus(mediaId, type)
    }


    suspend fun insertVisitedFiltersIfMaxPageIsHigher(visitedFilters: VisitedFiltersEntity) {
        val maxPage = moviesDao.getMaxPage(visitedFilters.filtersHash)
        if (maxPage == null || visitedFilters.maxPage > maxPage) {
            moviesDao.insertVisitedFilters(visitedFilters)
        }
    }

    suspend fun updateMaxPage(filtersHash: String, maxPage: Int) {
        moviesDao.updateMaxPage(filtersHash, maxPage)
    }

    suspend fun getMaxPage(filtersHash: String): Int? {
        return moviesDao.getMaxPage(filtersHash)
    }

    suspend fun deleteVisitedFilters(filtersHash: String) {
        moviesDao.deleteVisitedFilters(filtersHash)
    }
}