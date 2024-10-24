package es.josevaldes.filmatch.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import es.josevaldes.core.utils.getDeviceLocale
import es.josevaldes.data.model.Movie
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.viewmodels.MovieDetailsViewModel
import java.time.format.DateTimeFormatter

@Composable
fun MovieDetailsScreen(movie: Movie) {
    val scrollState = rememberScrollState()
    val viewModel: MovieDetailsViewModel = hiltViewModel()
    val deviceLanguage = getDeviceLocale()
    LaunchedEffect(movie.id) {
        viewModel.setInitialMovie(movie)
        viewModel.getMovieById(movie.id, deviceLanguage)
    }

    val fullMovie by viewModel.movie.collectAsState(movie)
    val isLoading by viewModel.isLoading.collectAsState(false)

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Column(
            modifier = Modifier
                .padding(top = 0.dp, bottom = padding.calculateBottomPadding())
                .scrollable(enabled = true, state = scrollState, orientation = Orientation.Vertical)
                .fillMaxSize()
        ) {

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
                this@Column.AnimatedVisibility(
                    visible = isLoading,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    CircularProgressIndicator()
                }
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(fullMovie?.posterUrl)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCacheKey(fullMovie?.posterUrl)
                        .networkCachePolicy(CachePolicy.READ_ONLY)
                        .build(),
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.TopStart,
                    contentDescription = fullMovie?.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .padding(bottom = 16.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Text("ID: ${fullMovie?.id}")
                Text("Title: ${fullMovie?.title}")
            }
            Row {
                Text(fullMovie?.title ?: "")
                Text(
                    fullMovie?.releaseDate?.format(DateTimeFormatter.ofPattern("YYYY")) ?: "",
                    modifier = Modifier.padding(start = 8.dp)
//                    DateTimeFormatter.ofPattern("YYYY").format(movie.releaseDate)
                )
            }
        }
    }
}

@Composable
@Preview
fun MovieDetailsScreenPreview() {
    FilmatchTheme {
        MovieDetailsScreen(
            Movie(
                id = 1,
                title = "Movie Title",
                releaseDate = "22.02.2022",
                posterPath = "8Y43POKjjKDGI9MH89NW0NAzzp8.jpg"
            )
        )
    }
}