package es.josevaldes.local.datasources

import es.josevaldes.local.dao.VisitedMoviesDao
import es.josevaldes.local.entities.VisitedFiltersEntity
import es.josevaldes.local.entities.VisitedMovieEntity
import javax.inject.Inject

class MoviesLocalDataSource @Inject constructor(
    private val moviesDao: VisitedMoviesDao
) {
    suspend fun getVisitedMovies(): List<VisitedMovieEntity> {
        return moviesDao.getVisitedMovies()
    }

    suspend fun insertVisitedMovie(visitedMovieEntity: VisitedMovieEntity) {
        moviesDao.insertVisitedMovie(visitedMovieEntity)
    }

    suspend fun deleteVisitedMovie(movieId: String) {
        moviesDao.deleteVisitedMovie(movieId)
    }

    suspend fun isMovieVisited(movieId: String): Boolean {
        return moviesDao.isMovieVisited(movieId)
    }

    suspend fun insertVisitedMovies(visitedMovies: List<VisitedMovieEntity>) {
        moviesDao.insertVisitedMovies(visitedMovies)
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