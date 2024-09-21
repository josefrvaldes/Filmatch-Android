package es.josevaldes.filmatch.paging

import androidx.paging.PagingConfig

object MovieDBPagingConfig {
    private const val PAGE_SIZE = 20
    private const val PREFETCH_DISTANCE = 5

    val pagingConfig = PagingConfig(
        pageSize = PAGE_SIZE,
        prefetchDistance = PREFETCH_DISTANCE,
        enablePlaceholders = false
    )
}