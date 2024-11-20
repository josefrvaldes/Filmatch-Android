package es.josevaldes.filmatch.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import es.josevaldes.core.utils.getDeviceCountry
import es.josevaldes.core.utils.getDeviceLocale
import es.josevaldes.data.model.ImageProvider
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.model.SelectableItem
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.ui.theme.getDefaultAccentButtonColors
import es.josevaldes.filmatch.viewmodels.FiltersViewModel


val otherFilters = listOf(
    SelectableItem("< 95 minutes", false),
    SelectableItem("< 2 hours", false),
    SelectableItem("Score > 50", false),
    SelectableItem("Score > 75", false),
    SelectableItem("2020s", false),
    SelectableItem("2010s", false),
    SelectableItem("2010s", false),
    SelectableItem("2000s", false),
    SelectableItem("1990s", false),
    SelectableItem("1980s", false),
    SelectableItem("1970s", false),
)


@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun FiltersScreen() {
    val viewModel: FiltersViewModel = hiltViewModel()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        val language = getDeviceLocale()
        val country = getDeviceCountry()
        viewModel.getAllGenres()
        viewModel.getAllProviders(language, country)
    }

    val genres by viewModel.filtersGenre.collectAsState()
    val providers by viewModel.providers.collectAsState()
    val contentTypes by viewModel.contentTypes.collectAsState()

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
            "and more...",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 10.dp)
        )

        HorizontalScrollGrid(otherFilters) {
            Toast.makeText(context, "Genre: $it", Toast.LENGTH_SHORT).show()
        }

        Box(modifier = Modifier.weight(1f))

        Button(
            onClick = {
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

@Composable
fun <T> HorizontalList(
    elements: List<SelectableItem<T>>,
    onItemClicked: (SelectableItem<T>) -> Unit = {}
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        items(elements.size) { index ->
            val currentElement = elements[index]
            FilterListItem(currentElement, onItemClicked)
        }
    }
}

@Composable
fun <T> HorizontalScrollGrid(
    elements: List<SelectableItem<T>>,
    displayIcon: Boolean = false,
    onItemClicked: (SelectableItem<T>) -> Unit = {}
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
                    if (displayIcon && item.item is ImageProvider) {
                        val castedItem = item as SelectableItem<ImageProvider>
                        FilterLogoListItem(castedItem, onItemClicked)
                    } else {
                        FilterListItem(item, onItemClicked)
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> FilterLogoListItem(
    item: SelectableItem<ImageProvider>,
    onItemClicked: (SelectableItem<T>) -> Unit
) {
    val color = if (item.isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    val borderWidth = if (item.item.photoUrl.isEmpty() || item.isSelected) 1.5.dp else 0.dp

    Box(
        modifier = Modifier
            .width(66.dp)
            .height(66.dp)
            .border(
                borderWidth,
                color,
                RoundedCornerShape(4.dp)
            )
            .clickable { onItemClicked(item as SelectableItem<T>) },
        contentAlignment = Alignment.Center
    ) {
        if (item.item.photoUrl.isEmpty()) {
            Text(text = item.item.toString(), overflow = TextOverflow.Ellipsis)
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.item.photoUrl)
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
    item: SelectableItem<T>,
    onItemClicked: (SelectableItem<T>) -> Unit
) {
    val color = if (item.isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(44.dp)
            .border(
                1.5.dp,
                color,
                RoundedCornerShape(4.dp)
            )
            .clickable { onItemClicked(item) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.item.toString(),
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


@Preview
@Composable
fun FiltersScreenPreview() {
    FilmatchTheme(darkTheme = true) {
        FiltersScreen()
    }
}