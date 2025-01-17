package es.josevaldes.filmatch.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import es.josevaldes.data.model.Duration
import es.josevaldes.data.model.Filter
import es.josevaldes.data.model.MovieFilters
import es.josevaldes.data.model.Score
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.ui.theme.getDefaultAccentButtonColors
import es.josevaldes.filmatch.viewmodels.FiltersViewModel
import java.time.LocalDate

private val itemWidth = 120.dp
private val itemHeight = 50.dp

@Composable
fun Modifier.filterBoxStyle(color: Color = MaterialTheme.colorScheme.outlineVariant): Modifier {
    return this
        .width(itemWidth)
        .height(itemHeight)
        .border(
            BorderStroke(1.5.dp, color),
            RoundedCornerShape(4.dp)
        )
}

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun FiltersScreen(selectedFilters: MovieFilters, onFiltersSelected: (MovieFilters) -> Unit = {}) {
    val viewModel: FiltersViewModel = hiltViewModel()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.setSelectedFilters(selectedFilters)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetFilters()
        }
    }

    val genres by viewModel.filtersGenre.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val contentTypes by viewModel.contentTypes.collectAsState()
    val scoreFilters by viewModel.scoreFilters.collectAsState()
    val timeFilters by viewModel.timeFilters.collectAsState()
    val fromYear by viewModel.fromYear.collectAsState()
    val toYear by viewModel.toYear.collectAsState()

    @Suppress("UNCHECKED_CAST")
    val otherFilters: List<Filter<Any>> = (scoreFilters + timeFilters).map { it as Filter<Any> }

    Column(
        modifier = Modifier
            .verticalScroll(scrollState),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                stringResource(R.string.filters),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                stringResource(R.string.reset),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { viewModel.resetFilters() }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }

        Text(
            stringResource(R.string.filters_screen_content_type_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 10.dp)
        )

        HorizontalList(contentTypes) {
            viewModel.contentTypeClicked(it)
        }

        Text(
            stringResource(R.string.filters_screen_genre_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 10.dp)
        )

        HorizontalScrollGrid(genres) {
            viewModel.genreClicked(it)
        }

        Text(
            stringResource(R.string.filters_screen_provider_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 10.dp)
        )

        HorizontalScrollGrid(providers, true) {
            viewModel.providerClicked(it)
        }

        Text(
            stringResource(R.string.filters_screen_date_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 10.dp)
        )


        YearRangeSelector(
            fromYear = fromYear,
            toYear = toYear,
            onFromSelected = { viewModel.fromYearSelected(it) },
            onToSelected = { viewModel.toYearSelected(it) }
        )


        Text(
            "and more...",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 10.dp)
        )

        HorizontalScrollGrid(otherFilters, textFormatter = {
            if (it.item is Duration) {
                context.getString(R.string.filter_duration_string, it.item)
            } else {
                val intScore = ((it.item as Score).score * 10).toInt().toString()
                context.getString(R.string.filter_score_string, intScore)
            }
        }) {
            viewModel.otherFilterClicked(it, true)
        }

        Box(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                onFiltersSelected(viewModel.getSelectedFilters())
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 60.dp),
            colors = getDefaultAccentButtonColors()
        ) {
            Text(stringResource(R.string.filters_screen_apply_filter_button_text))
        }
    }
}

@Preview(
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    name = "Light Mode",

    )
@Composable
fun HorizontalScrollGridPreview() {
    FilmatchTheme(darkTheme = true) {
        HorizontalScrollGrid(
            listOf(
                Filter(Score(5f), false), Filter(Score(7.5f), false)
            )
        )
    }
}

@Preview(
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
)
@Composable
fun YearRangeSelectorPreview() {
    FilmatchTheme {
        YearRangeSelector(2000, 2024)
    }
}

@Composable
fun YearRangeSelector(
    fromYear: Int,
    toYear: Int,
    onFromSelected: (Int) -> Unit = {},
    onToSelected: (Int) -> Unit = {}
) {
    val currentYear = LocalDate.now().year
    val allYears = (1940..currentYear).toList()

    val fromOptions = allYears.filter { it <= toYear }
    val toOptions = allYears.filter { it >= fromYear }

    Row(
        modifier = Modifier.padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        YearDropdown(
            label = stringResource(R.string.year_range_selector_from_label),
            years = fromOptions,
            selectedYear = fromYear,
            onYearSelected = { onFromSelected(it) }
        )

        YearDropdown(
            label = stringResource(R.string.year_range_selector_to_label),
            years = toOptions,
            selectedYear = toYear,
            onYearSelected = { onToSelected(it) }
        )
    }
}

@Composable
fun YearDropdown(
    label: String,
    years: List<Int>,
    selectedYear: Int,
    onYearSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val menuItemHeight = itemHeight
    val density = LocalDensity.current
    val menuItemInPx = remember {
        with(density) { menuItemHeight.toPx() }
    }

    // let's scroll to the selected item when the dropdown is expanded
    LaunchedEffect(expanded) {
        if (expanded) {
            val index = years.indexOf(selectedYear)
            if (index != -1) {
                val pixelsToScrollTo = index * menuItemInPx.toInt()
                scrollState.scrollTo(pixelsToScrollTo)
            }
        }
    }

    Box(
        modifier = Modifier
            .clickable { expanded = true }
            .filterBoxStyle(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$label: $selectedYear"
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            scrollState = scrollState,
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            years.forEach {
                DropdownMenuItem(
                    text = { Text(it.toString()) },
                    onClick = {
                        onYearSelected(it)
                        expanded = false
                    },
                    modifier = Modifier.height(menuItemHeight)
                )
            }
        }
    }
}

@Composable
fun <T> HorizontalList(
    elements: List<Filter<T>>,
    onItemClicked: (Filter<T>) -> Unit = {}
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        items(elements.size) { index ->
            val currentElement = elements[index]
            FilterListItem(item = currentElement, onItemClicked = onItemClicked)
        }
    }
}

@Composable
fun <T> HorizontalScrollGrid(
    elements: List<Filter<T>>,
    displayIcon: Boolean = false,
    textFormatter: (Filter<T>) -> String = { it.item.toString() },
    onItemClicked: (Filter<T>) -> Unit = {},
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        // let's iterate the list in chunks of 2
        items(elements.chunked(2).size) { index ->

            // let's get the current chunk
            val columnItems = elements.chunked(2)[index]

            // and let's create a column for each chunk
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // let's create a box for each item in the chunk
                columnItems.forEach { item ->
                    if (displayIcon) {
                        FilterLogoListItem(item, onItemClicked)
                    } else {
                        FilterListItem(item, textFormatter, onItemClicked)
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> FilterLogoListItem(
    item: Filter<T>,
    onItemClicked: (Filter<T>) -> Unit
) {
    val color = if (item.isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    val borderWidth = if (item.imageUrl?.isEmpty() == true || item.isSelected) 1.5.dp else 0.dp

    Box(
        modifier = Modifier
            .width(66.dp)
            .height(66.dp)
            .border(
                borderWidth,
                color,
                RoundedCornerShape(4.dp)
            )
            .clickable { onItemClicked(item) },
        contentAlignment = Alignment.Center
    ) {
        item.imageUrl?.let {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(it)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCacheKey(item.item.toString())
                    .networkCachePolicy(CachePolicy.READ_ONLY)
                    .build(),
                contentScale = ContentScale.FillWidth,
                contentDescription = item.item.toString(),
                modifier = Modifier
                    .width(66.dp)
                    .height(66.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        } ?: run {
            Text(text = item.item.toString(), overflow = TextOverflow.Ellipsis)
        }
        if (item.isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color.copy(alpha = 0.2f))
            )
        }
    }
}


@Composable
private fun <T> FilterListItem(
    item: Filter<T>,
    textFormatter: (Filter<T>) -> String = { it.item.toString() },
    onItemClicked: (Filter<T>) -> Unit,
) {
    val color = if (item.isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    Box(
        modifier = Modifier
            .clickable { onItemClicked(item) }
            .filterBoxStyle(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = textFormatter(item),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp)
        )

        if (item.isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color.copy(alpha = 0.2f))
            )
        }
    }
}


