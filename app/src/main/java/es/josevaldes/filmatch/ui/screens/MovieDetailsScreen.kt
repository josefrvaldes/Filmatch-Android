package es.josevaldes.filmatch.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import es.josevaldes.core.utils.getDeviceLocale
import es.josevaldes.data.model.Genre
import es.josevaldes.data.model.Movie
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.ui.theme.DislikeButtonBackground
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.viewmodels.MovieDetailsViewModel

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

    MovieDetailsScreenContent(scrollState, fullMovie, isLoading)
}

@Composable
private fun MovieDetailsScreenContent(scrollState: ScrollState, movie: Movie?, isLoading: Boolean) {
    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Column(
            modifier = Modifier
                .padding(
                    top = 0.dp,
                    bottom = padding.calculateBottomPadding(),
                )
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
                        .data(movie?.posterUrl)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCacheKey(movie?.posterUrl)
                        .networkCachePolicy(CachePolicy.READ_ONLY)
                        .build(),
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.TopStart,
                    contentDescription = movie?.title,
                    placeholder = painterResource(id = android.R.drawable.ic_menu_report_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .padding(bottom = 16.dp)
                )
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                PercentageTitleAndDurationRow(movie)

                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(movie?.title ?: "", style = MaterialTheme.typography.titleLarge)
                    movie?.getReleaseYear()?.let {
                        Text(
                            "($it)",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Text(
                    movie?.overview ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun PercentageTitleAndDurationRow(movie: Movie?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val percentage = movie?.voteAverage?.times(10)?.toInt()
        CircularProgressBar(
            percentage = percentage ?: 0
        )

        val genresString = movie?.getGenresString(stringResource(R.string.and))
        Text(
            "$genresString",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 10.dp)
        )


        Box(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(id = R.drawable.ic_duration),
            contentDescription = "duration",
            modifier = Modifier
                .size(16.dp)
        )


        Text(
            movie?.getDurationString() ?: "",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Composable
private fun CircularProgressBar(
    percentage: Int,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier.size(size)
) {
    val mediumColor = DislikeButtonBackground
    val badColor = Color(0xFFC62828)
    val color = when {
        percentage < 50 -> badColor
        percentage in 50..70 -> mediumColor
        else -> Color.Green
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.size(size)) {
            drawArc(
                color = color.copy(alpha = 0.45f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(13f, cap = StrokeCap.Round)
            )

            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * (percentage.toFloat() / 100),
                useCenter = false,
                style = Stroke(13f, cap = StrokeCap.Round)
            )
        }

        PercentageText(percentage)
    }
}


@Composable
private fun PercentageText(percentage: Int) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = "$percentage",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "%",
            style = MaterialTheme.typography.bodySmall//.copy(baselineShift = BaselineShift.Superscript)
        )
    }
}


@Composable
@Preview
fun MovieDetailsScreenPreview() {
    val movie = Movie(
        id = 1184918,
        title = "The Wild Robot",
        releaseDate = "2024-09-12",
        posterPath = "/wTnV3PCVW5O92JMrFvvrRcV39RU.jpg",
        voteAverage = 8.6,
        overview = "After a shipwreck, an intelligent robot called Roz is stranded on an uninhabited island. To survive the harsh environment, Roz bonds with the island's animals and cares for an orphaned baby goose.",
        genres = listOf(
            Genre(id = 1, name = "Action"),
            Genre(id = 2, name = "Adventure"),
            Genre(id = 3, name = "Comedy"),
        )
    )
    val isLoading = false
    val scrollState = rememberScrollState()

    FilmatchTheme(darkTheme = true) {
        MovieDetailsScreenContent(
            movie = movie,
            scrollState = scrollState,
            isLoading = isLoading
        )
    }
}