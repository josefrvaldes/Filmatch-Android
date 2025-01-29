package es.josevaldes.filmatch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.model.Provider
import es.josevaldes.data.model.User
import es.josevaldes.data.repositories.MediaRepository
import es.josevaldes.data.repositories.ProviderRepository
import es.josevaldes.data.responses.MediaType
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.AuthService
import es.josevaldes.filmatch.utils.DeviceLocaleProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val mediaRepository: MediaRepository,
    private val localeProvider: DeviceLocaleProvider,
    private val authService: AuthService
) : ViewModel() {

    private val _providers = MutableStateFlow<List<Provider>>(emptyList())
    val providers = _providers.asStateFlow()


    private val _loggedUser = MutableStateFlow<User?>(null)
    val loggedUser = _loggedUser.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val myWatchList = _loggedUser
        .filterNotNull()
        .flatMapLatest {
            mediaRepository.getWatchList(
//                it,
                User("13", "josevaldes", "jose.v@square1.io", "https://www.square1.io", "13"),
                MediaType.MOVIE
            ).cachedIn(viewModelScope)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val myWatchedList = _loggedUser
        .filterNotNull()
        .flatMapLatest {
            mediaRepository.getWatched(
//                it,
                User("13", "josevaldes", "jose.v@square1.io", "https://www.square1.io", "13"),
                MediaType.MOVIE
            ).cachedIn(viewModelScope)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val myNotInterestedList = _loggedUser
        .filterNotNull()
        .flatMapLatest {
            mediaRepository.getNotInterested(
//                it,
                User("13", "josevaldes", "jose.v@square1.io", "https://www.square1.io", "13"),
                MediaType.MOVIE
            ).cachedIn(viewModelScope)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val mySuperLikeList = _loggedUser
        .filterNotNull()
        .flatMapLatest {
            mediaRepository.getSuperInterested(
//                it,
                User("13", "josevaldes", "jose.v@square1.io", "https://www.square1.io", "13"),
                MediaType.MOVIE
            ).cachedIn(viewModelScope)
        }

    init {
        getProviders()
        getLoggedUser()
    }

    private fun getLoggedUser() {
        viewModelScope.launch {
            _loggedUser.value = authService.getUser()
        }
    }


    private fun getProviders() {
        viewModelScope.launch {
            providerRepository.getMovieProviders(
                localeProvider.getDeviceLocale(),
                localeProvider.getDeviceCountry()
            ).collect {
                if (it is ApiResult.Success) {
                    _providers.value = it.data
                }
            }
        }
    }
}