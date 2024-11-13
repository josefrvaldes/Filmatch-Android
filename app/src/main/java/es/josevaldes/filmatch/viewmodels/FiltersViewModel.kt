package es.josevaldes.filmatch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.model.Genre
import es.josevaldes.data.repositories.GenreRepository
import es.josevaldes.data.results.ApiResult
import es.josevaldes.filmatch.model.SelectableItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val genresRepository: GenreRepository
) : ViewModel() {

    enum class ContentType(private val displayName: String) {
        ALL("All"),
        MOVIES("Movies"),
        TV_SHOWS("TV Shows");

        override fun toString(): String {
            return displayName
        }
    }

    private val _contentTypes = MutableStateFlow(
        listOf(
            SelectableItem(ContentType.ALL, true),
            SelectableItem(ContentType.MOVIES, false),
            SelectableItem(ContentType.TV_SHOWS, false)
        )
    )
    val contentTypes = _contentTypes.asStateFlow()


    private val _filtersGenre = MutableStateFlow<List<SelectableItem<Genre>>>(listOf())
    val filtersGenre = _filtersGenre.asStateFlow()

    private val _tvGenres = mutableListOf<SelectableItem<Genre>>()
    private val _movieGenres = mutableListOf<SelectableItem<Genre>>()

    init {
        getAllGenres()
    }

    private fun getAllGenres() {
        viewModelScope.launch {
            genresRepository.getAllMovieGenres().collect { result ->
                if (result is ApiResult.Success) {
                    _movieGenres.add(SelectableItem(Genre(-1, "All"), true))
                    _movieGenres.addAll(result.data.genres.map { SelectableItem(it, false) })
                    _filtersGenre.value = mergeGenresList()
                } else {
                    // TODO: Handle error
                }
            }

            genresRepository.getAllTvGenres().collect { result ->
                if (result is ApiResult.Success) {
                    _tvGenres.add(SelectableItem(Genre(-1, "All"), true))
                    _tvGenres.addAll(result.data.genres.map { SelectableItem(it, false) })
                    _filtersGenre.value = mergeGenresList()
                } else {
                    // TODO: Handle error
                }
            }
        }
    }

    private fun mergeGenresList(): List<SelectableItem<Genre>> {
        val mergedGenres = mutableListOf<SelectableItem<Genre>>()
        for (genre in _movieGenres) {
            if (mergedGenres.none { it.item.id == genre.item.id }) {
                mergedGenres.add(genre)
            }
        }

        for (genre in _tvGenres) {
            if (mergedGenres.none { it.item.id == genre.item.id }) {
                mergedGenres.add(genre)
            }
        }

        val allItem = mergedGenres.first { it.item.id == -1 }
        mergedGenres.remove(allItem)
        mergedGenres.sortBy { it.item.name }
        mergedGenres.add(0, allItem)
        return mergedGenres.toList()
    }


    fun contentTypeClicked(contentType: SelectableItem<ContentType>) {
        val types = _contentTypes.value.toMutableList()
        val index = types.indexOf(contentType)
        types.forEachIndexed { i, item ->
            types[i] = item.copy(isSelected = i == index)
        }
        _contentTypes.value = types

        when (contentType.item) {
            ContentType.ALL -> {
                _filtersGenre.value = mergeGenresList()
            }

            ContentType.MOVIES -> {
                _filtersGenre.value = _movieGenres.toList()
            }

            ContentType.TV_SHOWS -> {
                _filtersGenre.value = _tvGenres.toList()
            }
        }


        val count = _filtersGenre.value.count { it.isSelected && it.item.id != -1 }
        if (count > 0) {
            deselectGenreFilterTypeAll()
        } else {
            selectGenreFilterTypeAll()
        }
    }

    private fun deselectGenreFilterTypeAll() {
        setGenreFilterTypeAll(false)
    }

    private fun setGenreFilterTypeAll(isSelected: Boolean) {
        val genres = _filtersGenre.value.toMutableList()
        if (genres.isNotEmpty() && genres[0].item.id == -1) {
            genres[0] = genres[0].copy(isSelected = isSelected)
        }

        if (_movieGenres.isNotEmpty() && _movieGenres.first().item.id == -1) {
            _movieGenres[0] = _movieGenres[0].copy(isSelected = isSelected)
        }

        if (_tvGenres.isNotEmpty() && _tvGenres.first().item.id == -1) {
            _tvGenres[0] = _tvGenres[0].copy(isSelected = isSelected)
        }

        _filtersGenre.value = genres.toList()
    }

    private fun selectGenreFilterTypeAll() {
        setGenreFilterTypeAll(true)
    }

    private fun deselectAllFiltersExceptForAllType() {
        val genres = _filtersGenre.value.toMutableList()
        genres.forEachIndexed { i, item ->
            genres[i] = item.copy(isSelected = (i == 0))
        }

        _movieGenres.forEachIndexed { i, item ->
            _movieGenres[i] = item.copy(isSelected = (i == 0))
        }

        _tvGenres.forEachIndexed { i, item ->
            _tvGenres[i] = item.copy(isSelected = (i == 0))
        }

        _filtersGenre.value = genres.toList()
    }

    fun genreClicked(genreClicked: SelectableItem<Genre>) {
        if (genreClicked.item.id == -1) {
            deselectAllFiltersExceptForAllType()
            return
        } else {
            deselectGenreFilterTypeAll()
        }

        val invertedStatusGenre = genreClicked.copy(isSelected = !genreClicked.isSelected)

        val indexMovie = _movieGenres.indexOfFirst { it.item.id == genreClicked.item.id }
        if (indexMovie != -1) {
            _movieGenres[indexMovie] = invertedStatusGenre
        }
        val indexTv = _tvGenres.indexOfFirst { it.item.id == genreClicked.item.id }
        if (indexTv != -1) {
            _tvGenres[indexTv] = invertedStatusGenre
        }
        val genres = _filtersGenre.value.toMutableList()
        val index = genres.indexOfFirst { it.item.id == genreClicked.item.id }
        if (index != -1) {
            genres[index] = invertedStatusGenre
        }

        _filtersGenre.value = genres.toList()

        val selectedCount = genres.count { it.isSelected }
        if (selectedCount == 0) {
            selectGenreFilterTypeAll()
        }
    }

    fun resetFilters() {
        deselectAllFiltersExceptForAllType()
        contentTypeClicked(_contentTypes.value.first())
    }


}
