package es.josevaldes.filmatch.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.di.IoDispatcher
import es.josevaldes.data.model.DiscoverItemData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _cleanedMovies = MutableStateFlow<List<DiscoverItemData>>(emptyList())
    val cleanedMovies = _cleanedMovies.asStateFlow()

    private suspend fun cleanVisitedItems(movies: List<DiscoverItemData>): List<DiscoverItemData> {
        return withContext(ioDispatcher) {
            movies.filter {
                it.id % 2 == 0
            }
        }
    }

    suspend fun mySuspendFun() {
        delay(1000)
        val moviesToBeCleaned = listOf(
            DiscoverItemData(1, false, "Overview 1", emptyList()),
            DiscoverItemData(2, false, "Overview 2", emptyList()),
            DiscoverItemData(3, false, "Overview 3", emptyList()),
            DiscoverItemData(4, false, "Overview 4", emptyList()),
            DiscoverItemData(5, false, "Overview 5", emptyList()),
            DiscoverItemData(6, false, "Overview 6", emptyList()),
            DiscoverItemData(7, false, "Overview 7", emptyList()),
            DiscoverItemData(8, false, "Overview 8", emptyList()),
            DiscoverItemData(9, false, "Overview 9", emptyList()),
            DiscoverItemData(10, false, "Overview 10", emptyList())
        )
        val cleanedMovies = cleanVisitedItems(moviesToBeCleaned)
        _cleanedMovies.value = cleanedMovies
    }
}