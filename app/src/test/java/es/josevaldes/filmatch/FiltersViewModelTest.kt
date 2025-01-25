package es.josevaldes.filmatch

import es.josevaldes.data.model.ContentType
import es.josevaldes.data.model.Duration
import es.josevaldes.data.model.Filter
import es.josevaldes.data.model.GenreData
import es.josevaldes.data.model.MediaFilters
import es.josevaldes.data.model.OtherFilters
import es.josevaldes.data.model.Provider
import es.josevaldes.data.model.Score
import es.josevaldes.data.repositories.GenreRepository
import es.josevaldes.data.repositories.ProviderRepository
import es.josevaldes.data.results.ApiResult
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

    private val allGenre = GenreData(-1, "All")
    private val actionGenre = GenreData(1, "Action")
    private val comedyGenre = GenreData(2, "Comedy")
    private val dramaGenre = GenreData(3, "Drama")
    private val tvOnlyGenre = GenreData(4, "TvOnlyGenre")
    private val movieOnlyGenre = GenreData(5, "MovieOnlyGenre")


    @Before
    fun setUp() {
        every { deviceLocaleProvider.getDeviceLocale() } returns "en"
        every { deviceLocaleProvider.getDeviceCountry() } returns "US"
        coEvery { genresRepository.getAllMovieGenres() } returns flowOf(
            ApiResult.Success(listOf(actionGenre, comedyGenre, dramaGenre, movieOnlyGenre))
        )
        coEvery { genresRepository.getAllTvGenres() } returns flowOf(
            ApiResult.Success(listOf(actionGenre, comedyGenre, dramaGenre, tvOnlyGenre))
        )
        coEvery {
            providersRepository.getMovieProviders(any(), any())
        } returns flowOf(ApiResult.Success(listOf(netflixProvider, hboProvider)))
        viewModel = FiltersViewModel(genresRepository, providersRepository, deviceLocaleProvider)
    }

    @Test
    fun `getAllGenres should load genres into filtersGenre`() {
        viewModel.getAllGenres() // it should return movies genres by default

        // the result will be sorted by name except the all filter which will always be the first one
        val expectedFilters = listOf(
            Filter(actionGenre, false),
            Filter(comedyGenre, false),
            Filter(dramaGenre, false),
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
        val genre = Filter(actionGenre, false)
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
    @Suppress("UNCHECKED_CAST")
    fun `otherFilterClicked should toggle time filter`() {
        val timeFilter = Filter(Duration(120), false)
        viewModel.otherFilterClicked(timeFilter as Filter<Any>, true)

        val toggledFilter = timeFilter.copy(isSelected = true)
        assertTrue(viewModel.timeFilters.value.contains(toggledFilter as Filter<Duration>))
    }

    @Test
    fun `getSelectedFilters should return all selected filters`() {
        val expectedFilters = MediaFilters(
            contentType = ContentType.MOVIES,
            genres = listOf(actionGenre),
            providers = listOf(netflixProvider),
            yearFrom = 2000,
            yearTo = 2020
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


    @Test
    fun `genres and providers should handle default, deselect all, and reselect all behaviors correctly`() {
        // Verify that "All" is selected by default
        assertTrue(viewModel.filtersGenre.value.first { it.item.id == -1 }.isSelected)
        assertTrue(viewModel.providers.value.first { it.item.id == -1 }.isSelected)

        // Select a genre (not "All") and verify that "All" gets deselected
        viewModel.genreClicked(Filter(actionGenre, false))
        assertFalse(viewModel.filtersGenre.value.first { it.item.id == -1 }.isSelected)
        assertTrue(viewModel.filtersGenre.value.first { it.item.id == actionGenre.id }.isSelected)

        // Select a provider (not "All") and verify that "All" gets deselected
        viewModel.providerClicked(
            Filter(
                netflixProvider,
                false,
                imageUrl = netflixProvider.logoUrl
            )
        )
        assertFalse(viewModel.providers.value.first { it.item.id == -1 }.isSelected)
        assertTrue(viewModel.providers.value.first { it.item.id == netflixProvider.id }.isSelected)

        // Deselect all genres and verify that "All" is automatically selected
        viewModel.genreClicked(Filter(actionGenre, true)) // Deselect "Action"
        assertTrue(viewModel.filtersGenre.value.first { it.item.id == -1 }.isSelected)
        assertFalse(viewModel.filtersGenre.value.first { it.item.id == actionGenre.id }.isSelected)

        // Deselect all providers and verify that "All" is automatically selected
        viewModel.providerClicked(
            Filter(
                netflixProvider,
                true,
                imageUrl = netflixProvider.logoUrl
            )
        ) // Deselect "Netflix"
        assertTrue(viewModel.providers.value.first { it.item.id == -1 }.isSelected)
        assertFalse(viewModel.providers.value.first { it.item.id == netflixProvider.id }.isSelected)
    }

    @Test
    fun `content types should behave like radio buttons`() {
        // Verify that "Movies" is selected by default
        assertTrue(viewModel.contentTypes.value.first { it.item == ContentType.MOVIES }.isSelected)
        assertFalse(viewModel.contentTypes.value.first { it.item == ContentType.TV_SHOWS }.isSelected)

        // Select "TV Shows" and verify that it is selected and "Movies" is deselected
        viewModel.contentTypeClicked(Filter(ContentType.TV_SHOWS, false))
        assertTrue(viewModel.contentTypes.value.first { it.item == ContentType.TV_SHOWS }.isSelected)
        assertFalse(viewModel.contentTypes.value.first { it.item == ContentType.MOVIES }.isSelected)

        // Select "Movies" again and verify that it is selected and "TV Shows" is deselected
        viewModel.contentTypeClicked(Filter(ContentType.MOVIES, false))
        assertFalse(viewModel.contentTypes.value.first { it.item == ContentType.TV_SHOWS }.isSelected)
        assertTrue(viewModel.contentTypes.value.first { it.item == ContentType.MOVIES }.isSelected)

        // Select "Movies" again and verify that it remains selected
        // Clicking on the already selected item
        viewModel.contentTypeClicked(Filter(ContentType.MOVIES, true))

        assertTrue(viewModel.contentTypes.value.first { it.item == ContentType.MOVIES }.isSelected)
    }


    @Test
    fun `genres should update and retain selection states when switching between content types`() {
        // Initial state: "All" is selected by default
        assertTrue(viewModel.contentTypes.value.first { it.item == ContentType.MOVIES }.isSelected)

        // Verify content type genres (in this case, Movies)
        val expectedAllGenres = listOf(
            Filter(actionGenre, false),
            Filter(comedyGenre, false),
            Filter(dramaGenre, false),
            Filter(movieOnlyGenre, false)
        ).sortedBy { it.item.name }
            .toMutableList().apply { add(0, Filter(allGenre, true)) }
        assertEquals(expectedAllGenres, viewModel.filtersGenre.value)

        // Switch to "TV Shows" and verify only TV genres are displayed
        viewModel.contentTypeClicked(Filter(ContentType.TV_SHOWS, false))
        val expectedTvGenres = listOf(
            Filter(actionGenre, false),
            Filter(comedyGenre, false),
            Filter(dramaGenre, false),
            Filter(tvOnlyGenre, false)
        ).sortedBy { it.item.name }
            .toMutableList().apply { add(0, Filter(allGenre, true)) }
        assertEquals(expectedTvGenres, viewModel.filtersGenre.value)

        // Select a genre specific to TV (e.g., "TvOnlyGenre")
        viewModel.genreClicked(Filter(tvOnlyGenre, false)) // Select "TvOnlyGenre"
        assertTrue(viewModel.filtersGenre.value.first { it.item.id == tvOnlyGenre.id }.isSelected)

        // Switch to "Movies" and verify only Movie genres are displayed
        viewModel.contentTypeClicked(Filter(ContentType.MOVIES, false))
        val expectedMovieGenres = listOf(
            Filter(actionGenre, false),
            Filter(comedyGenre, false),
            Filter(dramaGenre, false),
            Filter(movieOnlyGenre, false)
        ).sortedBy { it.item.name }
            .toMutableList().apply { add(0, Filter(allGenre, true)) }
        assertEquals(expectedMovieGenres, viewModel.filtersGenre.value)

        // Select a genre specific to Movies (e.g., "MovieOnlyGenre")
        viewModel.genreClicked(Filter(movieOnlyGenre, false)) // Select "MovieOnlyGenre"
        assertTrue(viewModel.filtersGenre.value.first { it.item.id == movieOnlyGenre.id }.isSelected)

        // Switch back to "TV Shows" and verify "TvOnlyGenre" is still selected
        viewModel.contentTypeClicked(Filter(ContentType.TV_SHOWS, false))
        assertTrue(viewModel.filtersGenre.value.first { it.item.id == tvOnlyGenre.id }.isSelected)

        // Switch back to "All" and verify merged genres retain their states
        val expectedMergedGenresWithSelection = listOf(
            Filter(actionGenre, false),
            Filter(comedyGenre, false),
            Filter(dramaGenre, false),
            Filter(movieOnlyGenre, true) // Still selected
        ).sortedBy { it.item.name }
            .toMutableList().apply { add(0, Filter(allGenre, false)) }
        viewModel.contentTypeClicked(Filter(ContentType.MOVIES, false))
        assertEquals(expectedMergedGenresWithSelection, viewModel.filtersGenre.value)
    }


    @Test
    @Suppress("UNCHECKED_CAST")
    fun `otherFilterClicked should allow only one filter per type to be selected at a time`() {
        // Select a duration (95 minutes) and verify it's the only selected one
        val duration95 = OtherFilters.timeFilters[0]
        viewModel.otherFilterClicked(duration95 as Filter<Any>, true)
        assertTrue(viewModel.timeFilters.value.first { it.item == Duration(95) }.isSelected)
        assertFalse(viewModel.timeFilters.value.any { it.item != Duration(95) && it.isSelected })

        // Select another duration (120 minutes) and verify the previous one is deselected
        val duration120 = OtherFilters.timeFilters[1]
        viewModel.otherFilterClicked(duration120 as Filter<Any>, true)
        assertTrue(viewModel.timeFilters.value.first { it.item == Duration(120) }.isSelected)
        assertFalse(viewModel.timeFilters.value.any { it.item == Duration(95) && it.isSelected })

        // Deselect the current duration (120 minutes) and verify none are selected
        viewModel.otherFilterClicked(duration120 as Filter<Any>, true)
        assertFalse(viewModel.timeFilters.value.any { it.isSelected })

        // Select a score (50%) and verify it's the only selected one
        val score50 = OtherFilters.scoreFilters[0]
        viewModel.otherFilterClicked(score50 as Filter<Any>, true)
        assertTrue(viewModel.scoreFilters.value.first { it.item == Score(5f) }.isSelected)
        assertFalse(viewModel.scoreFilters.value.any { it.item != Score(5f) && it.isSelected })

        // Select another score (75%) and verify the previous one is deselected
        val score75 = OtherFilters.scoreFilters[1]
        viewModel.otherFilterClicked(score75 as Filter<Any>, true)
        assertTrue(viewModel.scoreFilters.value.first { it.item == Score(7.5f) }.isSelected)
        assertFalse(viewModel.scoreFilters.value.any { it.item == Score(5f) && it.isSelected })

        // Deselect the current score (75%) and verify none are selected
        viewModel.otherFilterClicked(score75 as Filter<Any>, true)
        assertFalse(viewModel.scoreFilters.value.any { it.isSelected })

        // Ensure that selecting a duration does not affect scores
        viewModel.otherFilterClicked(duration95 as Filter<Any>, true)
        assertTrue(viewModel.timeFilters.value.first { it.item == Duration(95) }.isSelected)
        assertFalse(viewModel.scoreFilters.value.any { it.isSelected })

        // Ensure that selecting a score does not affect durations
        viewModel.otherFilterClicked(score50 as Filter<Any>, true)
        assertTrue(viewModel.scoreFilters.value.first { it.item == Score(5f) }.isSelected)
        assertTrue(viewModel.timeFilters.value.first { it.item == Duration(95) }.isSelected)
    }
}