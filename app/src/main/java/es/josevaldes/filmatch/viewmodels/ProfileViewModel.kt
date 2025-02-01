package es.josevaldes.filmatch.viewmodels

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.model.DiscoverItemData
import es.josevaldes.data.model.InterestStatus
import es.josevaldes.data.model.Provider
import es.josevaldes.data.model.User
import es.josevaldes.data.repositories.MediaRepository
import es.josevaldes.data.repositories.ProviderRepository
import es.josevaldes.data.responses.MediaType
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.AuthService
import es.josevaldes.filmatch.utils.DeviceLocaleProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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

    var selectedMediaType: MediaType = MediaType.MOVIE


    init {
        getProviders()
        getLoggedUser()
    }


    val myWatchList = getUserMediaList(InterestStatus.INTERESTED)
    val myWatchedList = getUserMediaList(InterestStatus.WATCHED)
    val myNotInterestedList = getUserMediaList(InterestStatus.NOT_INTERESTED)
    val mySuperLikeList = getUserMediaList(InterestStatus.SUPER_INTERESTED)


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun getUserMediaList(
        interestStatus: InterestStatus
    ): Flow<PagingData<DiscoverItemData>> = flow {
        val user = authService.getUser()
        user?.let {
            emitAll(
                when (interestStatus) {
                    InterestStatus.INTERESTED -> mediaRepository.getWatchList(it, selectedMediaType)
                    InterestStatus.WATCHED -> mediaRepository.getWatched(it, selectedMediaType)
                    InterestStatus.NOT_INTERESTED -> mediaRepository.getNotInterested(
                        it,
                        selectedMediaType
                    )

                    InterestStatus.SUPER_INTERESTED -> mediaRepository.getSuperInterested(
                        it,
                        selectedMediaType
                    )

                    InterestStatus.NONE -> flowOf(PagingData.from(emptyList()))
                }
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