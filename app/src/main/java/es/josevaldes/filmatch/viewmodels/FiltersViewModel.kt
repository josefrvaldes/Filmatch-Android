package es.josevaldes.filmatch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.model.Genre
import es.josevaldes.data.model.Provider
import es.josevaldes.data.repositories.GenreRepository
import es.josevaldes.data.repositories.ProviderRepository
import es.josevaldes.data.results.ApiResult
import es.josevaldes.filmatch.model.Duration
import es.josevaldes.filmatch.model.Filter
import es.josevaldes.filmatch.model.OtherFilters
import es.josevaldes.filmatch.model.Score
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val genresRepository: GenreRepository,
    private val providersRepository: ProviderRepository
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
            Filter(ContentType.ALL, true),
            Filter(ContentType.MOVIES, false),
            Filter(ContentType.TV_SHOWS, false)
        )
    )
    val contentTypes = _contentTypes.asStateFlow()

    private val _filtersGenre = MutableStateFlow<List<Filter<Genre>>>(listOf())
    val filtersGenre = _filtersGenre.asStateFlow()

    private val _providers =
        MutableStateFlow<MutableList<Filter<Provider>>>(mutableListOf())
    val providers = _providers.asStateFlow()

    private val _tvGenres = mutableListOf<Filter<Genre>>()
    private val _movieGenres = mutableListOf<Filter<Genre>>()

    private var _fromYear = 0
    private var _toYear = 0

    private val _scoreFilters = MutableStateFlow(OtherFilters.scoreFilters)
    val scoreFilters = _scoreFilters.asStateFlow()

    private val _timeFilters = MutableStateFlow(OtherFilters.timeFilters)
    val timeFilters = _timeFilters.asStateFlow()

    fun getAllProviders(language: String, region: String) {
        viewModelScope.launch {
            providersRepository.getMovieProviders(language, region).collect { result ->
                if (result is ApiResult.Success) {
                    val providers = result.data.map { Filter(it, false, imageUrl = it.logoUrl) }
                        .toMutableList()
                    providers.add(0, Filter(Provider(-1, "All", null, 0, emptyMap()), true))
                    _providers.value =
                        providers.sortedBy { it.item.displayPriority }.toMutableList()
                } else {
                    // TODO: Handle error
                }
            }
        }
    }

    fun getAllGenres() {
        viewModelScope.launch {
            genresRepository.getAllMovieGenres().collect { result ->
                if (result is ApiResult.Success) {
                    _movieGenres.add(Filter(Genre(-1, "All"), true))
                    _movieGenres.addAll(result.data.genres.map { Filter(it, false) })
                    _filtersGenre.value = mergeGenresList()
                } else {
                    // TODO: Handle error
                }
            }

            genresRepository.getAllTvGenres().collect { result ->
                if (result is ApiResult.Success) {
                    _tvGenres.add(Filter(Genre(-1, "All"), true))
                    _tvGenres.addAll(result.data.genres.map { Filter(it, false) })
                    _filtersGenre.value = mergeGenresList()
                } else {
                    // TODO: Handle error
                }
            }
        }
    }

    private fun mergeGenresList(): List<Filter<Genre>> {
        val mergedGenres = mutableListOf<Filter<Genre>>()
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

    fun contentTypeClicked(contentType: Filter<ContentType>) {
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

    fun genreClicked(genreClicked: Filter<Genre>) {
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
        deselectAllProvidersExceptForAllType()
        contentTypeClicked(_contentTypes.value.first())
    }

    fun providerClicked(provider: Filter<Provider>) {
        if (provider.item.id == -1) {
            deselectAllProvidersExceptForAllType()
            return
        } else {
            deselectProviderFilterTypeAll()
        }
        val providers = _providers.value.toMutableList()
        val index = providers.indexOf(provider)
        providers[index] = provider.copy(isSelected = !provider.isSelected)
        _providers.value = providers

        val selectedCount = providers.count { it.isSelected }
        if (selectedCount == 0) {
            selectProviderFilterTypeAll()
        }
    }

    private fun selectProviderFilterTypeAll() {
        val providers = _providers.value.toMutableList()
        providers[0] = providers[0].copy(isSelected = true)
        _providers.value = providers.toMutableList()
    }

    private fun deselectAllProvidersExceptForAllType() {
        val providers = _providers.value.toMutableList()
        providers.forEachIndexed { i, item ->
            providers[i] = item.copy(isSelected = (i == 0))
        }
        _providers.value = providers
    }

    private fun deselectProviderFilterTypeAll() {
        val providers = _providers.value.toMutableList()
        providers[0] = providers[0].copy(isSelected = false)
        _providers.value = providers.toMutableList()
    }

    fun fromYearSelected(year: Int) {
        _fromYear = year
    }

    fun toYearSelected(year: Int) {
        _toYear = year
    }

    @Suppress("UNCHECKED_CAST")
    fun otherFilterClicked(filter: Filter<Any>) {
        val listToProcess = if (filter.item is Duration) {
            _timeFilters.value.toMutableList() as MutableList<Filter<Any>>
        } else {
            _scoreFilters.value.toMutableList() as MutableList<Filter<Any>>
        }

        val selectedStatus = filter.isSelected
        val index = listToProcess.indexOf(filter)
        listToProcess.forEachIndexed { i, item ->
            if (i == index) {
                listToProcess[i] = item.copy(isSelected = !selectedStatus)
            } else {
                listToProcess[i] = item.copy(isSelected = false)
            }
        }

        if (filter.item is Duration) {
            _timeFilters.value = listToProcess as List<Filter<Duration>>
        } else {
            _scoreFilters.value = listToProcess as List<Filter<Score>>
        }
    }


}
