package es.josevaldes.filmatch.viewmodels

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.model.Provider
import es.josevaldes.data.model.User
import es.josevaldes.data.repositories.MediaRepository
import es.josevaldes.data.repositories.ProviderRepository
import es.josevaldes.data.responses.MediaType
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.AuthService
import es.josevaldes.filmatch.utils.DeviceLocaleProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val mediaRepository: MediaRepository,
    private val localeProvider: DeviceLocaleProvider,
    private val authService: AuthService,
) : ViewModel() {

    private val _providers = MutableStateFlow<List<Provider>>(emptyList())
    val providers = _providers.asStateFlow()


    private val _loggedUser = MutableStateFlow<User?>(null)
    val loggedUser = _loggedUser.asStateFlow()


    init {
        getProviders()
        getLoggedUser()
    }


    val myWatchList = flow {
        val user = authService.getUser()
        user?.let {
            emitAll(
                mediaRepository.getWatchList(it, MediaType.MOVIE)
            )
        }
    }


    val myWatchedList = flow {
        val user = authService.getUser()
        user?.let {
            emitAll(
                mediaRepository.getWatched(it, MediaType.MOVIE)
            )
        }
    }


    val myNotInterestedList = flow {
        val user = authService.getUser()
        user?.let {
            emitAll(
                mediaRepository.getNotInterested(it, MediaType.MOVIE)
            )
        }
    }

    
    val mySuperLikeList = flow {
        val user = authService.getUser()
        user?.let {
            emitAll(
                mediaRepository.getWatchList(it, MediaType.MOVIE)
            )
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun getLoggedUser() {
        viewModelScope.launch {
            _loggedUser.value = authService.getUser()
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun getProviders() {
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