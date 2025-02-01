package es.josevaldes.filmatch

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import es.josevaldes.data.model.DiscoverItemData
import es.josevaldes.data.model.DiscoverMovieData
import es.josevaldes.data.model.InterestStatus
import es.josevaldes.data.model.Provider
import es.josevaldes.data.model.User
import es.josevaldes.data.repositories.MediaRepository
import es.josevaldes.data.repositories.ProviderRepository
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.services.AuthService
import es.josevaldes.filmatch.utils.DeviceLocaleProvider
import es.josevaldes.filmatch.viewmodels.ProfileViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class ProfileViewModelTest {
    private val authService = mockk<AuthService>()
    private val providerRepository = mockk<ProviderRepository>()
    private val localeProvider = mockk<DeviceLocaleProvider>()
    private val mediaRepository = mockk<MediaRepository>()

    private lateinit var profileViewModel: ProfileViewModel


    @Before
    fun setUp() {
        every { localeProvider.getDeviceLocale() } returns "en-US"
        every { localeProvider.getDeviceCountry() } returns "US"

        coEvery {
            mediaRepository.getWatchList(any(), any())
        } returns flowOf(PagingData.from(emptyList()))

        coEvery {
            providerRepository.getMovieProviders(any(), any())
        } returns flowOf(ApiResult.Success(emptyList()))

        coEvery {
            authService.getUser()
        } returns User(
            "13",
            "josevaldes",
            "jose.v@square1.io",
            "https://www.square1.io",
            "13"
        )

        profileViewModel = ProfileViewModel(
            providerRepository,
            mediaRepository,
            localeProvider,
            authService,
        )
    }


    @Test
    fun `getLoggedUser should update loggedUser when user is returned`() = runTest {
        val expectedUser =
            User(uid = "123", email = "test@example.com", username = "TestUser", id = "123")

        coEvery { authService.getUser() } returns expectedUser

        profileViewModel.getLoggedUser()

        assertEquals(expectedUser, profileViewModel.loggedUser.value)
    }

    @Test
    fun `getLoggedUser should update loggedUser with null when no user is returned`() = runTest {
        coEvery { authService.getUser() } returns null

        profileViewModel.getLoggedUser()

        assertNull(profileViewModel.loggedUser.value)
    }

    @Test
    fun `getProviders should return providers on success`() = runTest {
        val expectedProviders = listOf(
            Provider(
                id = 1,
                name = "Netflix",
                logoPath = "path",
                displayPriority = 1,
                displayPriorities = emptyMap()
            ),
            Provider(
                id = 2, name = "Amazon Prime",
                logoPath = "path",
                displayPriority = 2,
                displayPriorities = emptyMap()
            )
        )

        coEvery { providerRepository.getMovieProviders(any(), any()) } returns flowOf(
            ApiResult.Success(expectedProviders)
        )

        profileViewModel.getProviders()

        assertEquals(expectedProviders, profileViewModel.providers.value)
    }

    @Test
    fun `getProviders should not update providers on error`() = runTest {
        coEvery { providerRepository.getMovieProviders(any(), any()) } returns flowOf(
            ApiResult.Error(ApiError.Unknown)
        )

        profileViewModel.getProviders()

        assertEquals(profileViewModel.providers.value.size, 0)
    }

    @Test
    fun `getProviders should update providers with empty list when API returns no providers`() =
        runTest {
            coEvery { providerRepository.getMovieProviders(any(), any()) } returns flowOf(
                ApiResult.Success(emptyList())
            )

            profileViewModel.getProviders()

            assertEquals(emptyList<Provider>(), profileViewModel.providers.value)
        }


    @Test
    fun `myWatchList should emit data when user is logged in and repository returns success`() =
        runTest {

            val expectedMovies = listOf(
                DiscoverMovieData(id = 1, title = "Movie 1"),
                DiscoverMovieData(id = 2, title = "Movie 2")
            )

            coEvery {
                mediaRepository.getWatchList(any(), any())
            } returns flowOf(PagingData.from(expectedMovies))


            val result = profileViewModel.myWatchList

            val receivedData = result.asSnapshot()

            assertEquals(expectedMovies, receivedData)
        }


    @Test
    fun `myWatchList should emit empty list when user is not logged in`() =
        runTest {
            coEvery { authService.getUser() } returns null

            coEvery {
                mediaRepository.getWatchList(any(), any())
            } returns flowOf(PagingData.from(emptyList()))


            profileViewModel.getLoggedUser()

            val items: Flow<PagingData<DiscoverItemData>> = profileViewModel.myWatchList
            val itemsSnapshot: List<DiscoverItemData> = items.asSnapshot()

            assertEquals(
                emptyList<DiscoverItemData>(),
                itemsSnapshot
            )
        }


    @Test
    fun `getUserMediaList should paginate correctly across multiple pages for all interest statuses`() =
        runTest {
            val allMovies = (1..50).map { DiscoverMovieData(id = it, title = "Movie $it") }
            val pages = listOf(
                allMovies.subList(0, 20), // Page 1
                allMovies.subList(20, 40), // Page 2
                allMovies.subList(40, 50)  // Page 3
            )

            val statuses = listOf(
                InterestStatus.INTERESTED,
                InterestStatus.WATCHED,
                InterestStatus.NOT_INTERESTED,
                InterestStatus.SUPER_INTERESTED
            )

            statuses.forEach { interestStatus ->
                println("Testing pagination para $interestStatus")

                // first page
                coEvery {
                    when (interestStatus) {
                        InterestStatus.INTERESTED -> mediaRepository.getWatchList(any(), any())
                        InterestStatus.WATCHED -> mediaRepository.getWatched(any(), any())
                        InterestStatus.NOT_INTERESTED -> mediaRepository.getNotInterested(
                            any(),
                            any()
                        )

                        InterestStatus.SUPER_INTERESTED -> mediaRepository.getSuperInterested(
                            any(),
                            any()
                        )

                        InterestStatus.NONE -> flowOf(PagingData.from(emptyList()))
                    }
                } returns flowOf(PagingData.from(pages[0]))

                val result1 = profileViewModel.getUserMediaList(interestStatus).asSnapshot {
                    scrollTo(index = 1)
                }
                assertEquals(pages[0], result1)

                // Second page
                coEvery {
                    when (interestStatus) {
                        InterestStatus.INTERESTED -> mediaRepository.getWatchList(any(), any())
                        InterestStatus.WATCHED -> mediaRepository.getWatched(any(), any())
                        InterestStatus.NOT_INTERESTED -> mediaRepository.getNotInterested(
                            any(),
                            any()
                        )

                        InterestStatus.SUPER_INTERESTED -> mediaRepository.getSuperInterested(
                            any(),
                            any()
                        )

                        InterestStatus.NONE -> flowOf(PagingData.from(emptyList()))
                    }
                } returns flowOf(PagingData.from(pages[1]))

                val result2 = profileViewModel.getUserMediaList(interestStatus).asSnapshot {
                    scrollTo(index = 20)
                }
                assertEquals(pages[1], result2)

                // Third page
                coEvery {
                    when (interestStatus) {
                        InterestStatus.INTERESTED -> mediaRepository.getWatchList(any(), any())
                        InterestStatus.WATCHED -> mediaRepository.getWatched(any(), any())
                        InterestStatus.NOT_INTERESTED -> mediaRepository.getNotInterested(
                            any(),
                            any()
                        )

                        InterestStatus.SUPER_INTERESTED -> mediaRepository.getSuperInterested(
                            any(),
                            any()
                        )

                        InterestStatus.NONE -> flowOf(PagingData.from(emptyList()))
                    }
                } returns flowOf(PagingData.from(pages[2]))

                val result3 = profileViewModel.getUserMediaList(interestStatus).asSnapshot {
                    scrollTo(index = 40)
                }
                assertEquals(pages[2], result3)

                // let's verify that all pages are correctly concatenated
                assertEquals(
                    allMovies,
                    result1 + result2 + result3
                )
            }
        }

    @Test
    fun `getUserMediaList should emit data when user is logged in and repository returns success`() =
        runTest {
            val expectedMovies = listOf(
                DiscoverMovieData(id = 1, title = "Movie 1"),
                DiscoverMovieData(id = 2, title = "Movie 2")
            )

            val statuses = listOf(
                InterestStatus.INTERESTED,
                InterestStatus.WATCHED,
                InterestStatus.NOT_INTERESTED,
                InterestStatus.SUPER_INTERESTED
            )

            statuses.forEach { interestStatus ->
                coEvery {
                    when (interestStatus) {
                        InterestStatus.INTERESTED -> mediaRepository.getWatchList(any(), any())
                        InterestStatus.WATCHED -> mediaRepository.getWatched(any(), any())
                        InterestStatus.NOT_INTERESTED -> mediaRepository.getNotInterested(
                            any(),
                            any()
                        )

                        InterestStatus.SUPER_INTERESTED -> mediaRepository.getSuperInterested(
                            any(),
                            any()
                        )

                        InterestStatus.NONE -> flowOf(PagingData.from(emptyList()))
                    }
                } returns flowOf(PagingData.from(expectedMovies))

                val result = profileViewModel.getUserMediaList(interestStatus).asSnapshot()

                assertEquals(expectedMovies, result)
            }
        }

    @Test
    fun `getUserMediaList should emit empty list when user is not logged in`() = runTest {
        coEvery { authService.getUser() } returns null

        val statuses = listOf(
            InterestStatus.INTERESTED,
            InterestStatus.WATCHED,
            InterestStatus.NOT_INTERESTED,
            InterestStatus.SUPER_INTERESTED
        )

        statuses.forEach { interestStatus ->
            coEvery {
                when (interestStatus) {
                    InterestStatus.INTERESTED -> mediaRepository.getWatchList(any(), any())
                    InterestStatus.WATCHED -> mediaRepository.getWatched(any(), any())
                    InterestStatus.NOT_INTERESTED -> mediaRepository.getNotInterested(any(), any())
                    InterestStatus.SUPER_INTERESTED -> mediaRepository.getWatchList(any(), any())
                    InterestStatus.NONE -> mediaRepository.getWatchList(any(), any())
                }
            } returns flowOf(PagingData.empty())

            val result = profileViewModel.getUserMediaList(interestStatus).asSnapshot()

            assertEquals(
                emptyList<DiscoverItemData>(),
                result
            )
        }
    }
}