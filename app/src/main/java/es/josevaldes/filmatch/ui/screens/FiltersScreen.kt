package es.josevaldes.filmatch.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.ui.theme.getDefaultAccentButtonColors


val contentTypes = listOf(
    "All",
    "Movies",
    "TV Shows"
)

val genres = listOf(
    "All",
    "Action",
    "Adventure",
    "Comedy",
    "Biography",
    "Drama",
    "Horror",
    "Romance",
    "Sci-Fi",
    "Thriller",
    "Western"
)

val streamingProviders = listOf(
    "All",
    "Netflix",
    "Amazon Prime Video",
    "Disney+",
    "HBO Max",
    "Hulu",
    "Apple TV+",
    "Peacock",
    "Paramount+",
    "Discovery+"
)

val otherFilters = listOf(
    "All",
    "Year",
    "Rating",
    "Language",
    "Country",
    "Runtime"
)


@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun FiltersScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

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
                    .padding(end = 20.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }

        Text(
            stringResource(R.string.filters_screen_content_type_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 10.dp)
        )

        HorizontalList(contentTypes) {
            Toast.makeText(context, "Genre: $it", Toast.LENGTH_SHORT).show()
        }

        Text(
            stringResource(R.string.filters_screen_genre_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 10.dp)
        )

        HorizontalScrollGrid(genres) {
            Toast.makeText(context, "Genre: $it", Toast.LENGTH_SHORT).show()
        }

        Text(
            stringResource(R.string.filters_screen_provider_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 10.dp)
        )

        HorizontalScrollGrid(streamingProviders) {
            Toast.makeText(context, "Genre: $it", Toast.LENGTH_SHORT).show()
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
fun <T> HorizontalList(elements: List<T>, onItemClicked: (T) -> Unit = {}) {
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
fun <T> HorizontalScrollGrid(elements: List<T>, onItemClicked: (T) -> Unit = {}) {
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
                    FilterListItem(item, onItemClicked)
                }
            }
        }
    }
}

@Composable
private fun <T> FilterListItem(item: T, onItemClicked: (T) -> Unit) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(44.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(4.dp)
            )
            .clickable { onItemClicked(item) }
            .padding(horizontal = 6.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = item.toString(), overflow = TextOverflow.Ellipsis)
    }
}


@Preview
@Composable
fun FiltersScreenPreview() {
    FilmatchTheme(darkTheme = true) {
        FiltersScreen()
    }
}