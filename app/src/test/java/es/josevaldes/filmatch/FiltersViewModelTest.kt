package es.josevaldes.filmatch

import es.josevaldes.data.model.Genre
import es.josevaldes.data.model.GenresList
import es.josevaldes.data.model.Provider
import es.josevaldes.data.repositories.GenreRepository
import es.josevaldes.data.repositories.ProviderRepository
import es.josevaldes.data.results.ApiResult
import es.josevaldes.filmatch.model.ContentType
import es.josevaldes.filmatch.model.Duration
import es.josevaldes.filmatch.model.Filter
import es.josevaldes.filmatch.utils.DeviceLocaleProvider
import es.josevaldes.filmatch.viewmodels.FiltersViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDateTime

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class FiltersViewModelTest {

    private val genresRepository = mockk<GenreRepository>()
    private val providersRepository = mockk<ProviderRepository>()
    private val deviceLocaleProvider = mockk<DeviceLocaleProvider>()

    private lateinit var viewModel: FiltersViewModel

    private val allProvider = Provider(-1, "All", null, 0, emptyMap())
    private val netflixProvider = Provider(1, "Netflix", "netflix.png", 1, emptyMap())
    private val hboProvider = Provider(2, "HBO", "hbo.png", 2, emptyMap())

    private val allGenre = Genre(-1, "All")
    private val actionGenre = Genre(1, "Action")
    private val comedyGenre = Genre(2, "Comedy")
    private val dramaGenre = Genre(3, "Drama")
    private val tvOnlyGenre = Genre(4, "TvOnlyGenre")
    private val movieOnlyGenre = Genre(5, "MovieOnlyGenre")


    @Before
    fun setUp() {
        every { deviceLocaleProvider.getDeviceLocale() } returns "en"
        every { deviceLocaleProvider.getDeviceCountry() } returns "US"
        coEvery { genresRepository.getAllMovieGenres() } returns flowOf(
            ApiResult.Success(
                GenresList(
                    listOf(actionGenre, comedyGenre, dramaGenre, movieOnlyGenre)
                )
            )
        )
        coEvery { genresRepository.getAllTvGenres() } returns flowOf(
            ApiResult.Success(
                GenresList(
                    listOf(actionGenre, comedyGenre, dramaGenre, tvOnlyGenre)
                )
            )
        )
        coEvery {
            providersRepository.getMovieProviders(any(), any())
        } returns flowOf(ApiResult.Success(listOf(netflixProvider, hboProvider)))
        viewModel = FiltersViewModel(genresRepository, providersRepository, deviceLocaleProvider)
    }

    @Test
    fun `getAllGenres should load genres into filtersGenre`() {
        viewModel.getAllGenres()

        // the result will be sorted by name except the all filter which will always be the first one
        val expectedFilters = listOf(
            Filter(actionGenre, false),
            Filter(comedyGenre, false),
            Filter(dramaGenre, false),
            Filter(tvOnlyGenre, false),
            Filter(movieOnlyGenre, false),
        ).sortedBy { it.item.name }.toMutableList()
            .apply { add(0, Filter(allGenre, true)) }
        assertEquals(expectedFilters, viewModel.filtersGenre.value)
    }

    @Test
    fun `getAllProviders should load providers into providers`() {
        val providers = listOf(
            netflixProvider,
            hboProvider
        )
        coEvery {
            providersRepository.getMovieProviders(
                any(),
                any()
            )
        } returns flowOf(ApiResult.Success(providers))

        viewModel.getAllProviders("en", "US")

        val expectedFilters = listOf(
            Filter(allProvider, true),
            Filter(netflixProvider, false, imageUrl = netflixProvider.logoUrl),
            Filter(hboProvider, false, imageUrl = hboProvider.logoUrl)
        )
        assertEquals(expectedFilters, viewModel.providers.value)
    }

    @Test
    fun `contentTypeClicked should update genres based on content type`() {
        val contentType = Filter(ContentType.TV_SHOWS, true)
        viewModel.contentTypeClicked(contentType)
        assertEquals(
            viewModel.filtersGenre.value,
            viewModel.filtersGenre.value.filter { it.item is Genre })
    }

    @Test
    fun `resetFilters should reset all filters to default values`() {
        // Simulate changes
        viewModel.fromYearSelected(2010)
        viewModel.toYearSelected(2020)
        viewModel.resetFilters()

        assertEquals(2000, viewModel.fromYear.value)
        assertEquals(LocalDateTime.now().year, viewModel.toYear.value)
        assertTrue(viewModel.providers.value.all { !it.isSelected || it.item.id == -1 })
        assertTrue(viewModel.filtersGenre.value.all { !it.isSelected || it.item.id == -1 })
    }

    @Test
    fun `genreClicked should toggle genre selection`() {
        val genre = Filter(Genre(1, "Action"), false)
        viewModel.genreClicked(genre)

        val toggledGenre = genre.copy(isSelected = true)
        assertTrue(viewModel.filtersGenre.value.contains(toggledGenre))
        assertFalse(viewModel.filtersGenre.value.contains(genre))
    }

    @Test
    fun `providerClicked should toggle provider selection`() {
        val provider = Filter(netflixProvider, false, imageUrl = netflixProvider.logoUrl)
        viewModel.providerClicked(provider)

        val toggledProvider = provider.copy(isSelected = true)
        assertTrue(viewModel.providers.value.contains(toggledProvider))
        assertFalse(viewModel.providers.value.contains(provider))
    }

    @Test
    fun `otherFilterClicked should toggle time filter`() {
        val timeFilter = Filter(Duration(120), false)
        viewModel.otherFilterClicked(timeFilter as Filter<Any>)

        val toggledFilter = timeFilter.copy(isSelected = true)
        assertTrue(viewModel.timeFilters.value.contains(toggledFilter as Filter<Duration>))
    }

    @Test
    fun `getSelectedFilters should return all selected filters`() {
        val expectedFilters = listOf(
            Filter(ContentType.MOVIES, true),
            Filter(actionGenre, true),
            Filter(netflixProvider, true, imageUrl = netflixProvider.logoUrl),
            Filter(2000, true), // from
            Filter(2020, true) // to
        )
        // any whateverClicked is expecting to receive the value isSelected untouched, and
        // the method will toggle it. So for example, if the filter is selected, it will be
        // toggled to false and vice versa.
        // In this case, we want to set the filters to selected, so we pass false as the second argument
        // so the method will toggle them to true
        viewModel.contentTypeClicked(Filter(ContentType.MOVIES, false))
        viewModel.genreClicked(Filter(actionGenre, false))
        viewModel.providerClicked(
            Filter(
                netflixProvider,
                false,
                imageUrl = netflixProvider.logoUrl
            )
        )
        viewModel.toYearSelected(2020)

        val selectedFilters = viewModel.getSelectedFilters()
        assertEquals(expectedFilters, selectedFilters)
    }
}