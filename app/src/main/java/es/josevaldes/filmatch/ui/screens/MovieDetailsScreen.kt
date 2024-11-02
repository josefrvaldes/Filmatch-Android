package es.josevaldes.filmatch.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavBackStackEntry
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import es.josevaldes.core.utils.getDeviceLocale
import es.josevaldes.data.model.CastMember
import es.josevaldes.data.model.Credits
import es.josevaldes.data.model.CrewMember
import es.josevaldes.data.model.Genre
import es.josevaldes.data.model.Movie
import es.josevaldes.data.model.VideoResult
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.extensions.openYoutubeVideo
import es.josevaldes.filmatch.ui.theme.DislikeButtonBackground
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.viewmodels.MovieDetailsViewModel


@Composable
fun MovieDetailsScreen(movie: Movie, backStackEntry: NavBackStackEntry) {
    val viewModel: MovieDetailsViewModel = hiltViewModel()
    val deviceLanguage = getDeviceLocale()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(
        false,
        minActiveState = Lifecycle.State.STARTED
    )

    // what we are doing here is that we are using a mutableStateOf to hold the movie that we are going to show in the screen
    // we initialize it with the movie that we receive as a parameter
    val fullMovieState = remember { mutableStateOf<Movie?>(movie) }

    // on startup, we call the getMovieById method of the viewModel to get the full movie details
    LaunchedEffect(movie.id) {
        viewModel.setInitialMovie(movie)
        viewModel.getMovieById(movie.id, deviceLanguage)
    }

    // we collect the movie from the viewModel and update the fullMovieState.
    // the main important thing here is that we do it after the transition animation has finished
    // otherwise, the transition animation will be laggy
    LaunchedEffect(backStackEntry.lifecycle) {
        backStackEntry.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.movie.collect {
                fullMovieState.value = it
            }
        }
    }

    MovieDetailsScreenContent(
        fullMovie = fullMovieState.value,
        initialMovie = movie,
        isLoading = isLoading
    )
}

@Composable
private fun MovieDetailsScreenContent(
    fullMovie: Movie?,
    initialMovie: Movie? = fullMovie,
    isLoading: Boolean
) {
    val scrollState = rememberScrollState()
    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Column(
            modifier = Modifier
                .padding(
                    top = 0.dp,
                    bottom = padding.calculateBottomPadding(),
                )
                .verticalScroll(enabled = true, state = scrollState)
                .fillMaxSize()
        ) {

            PhotoAndProgressIndicatorSection(initialMovie, isLoading)


            PercentageTitleAndDurationSection(fullMovie)
            TitleAndYearSection(initialMovie)
            OverviewSection(initialMovie)
            DirectedBySection(fullMovie)
            val displayableYoutubeVideos = fullMovie?.displayableYoutubeVideos
            AnimatedVisibility(visible = displayableYoutubeVideos?.isNotEmpty() == true) {
                VideosSection(displayableYoutubeVideos)
            }
            val displayableCast = fullMovie?.displayableCast
            AnimatedVisibility(visible = displayableCast?.isNotEmpty() == true) {
                CastSection(displayableCast)
            }
        }
    }
}

@Composable
private fun CastSection(
    displayableCast: List<CastMember>?
) {
    if (displayableCast?.isNotEmpty() == true) {
        val paddingEnd = 16.dp
        Column {
            Text(
                "Cast",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp)
            )
            LazyRow(modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)) {
                items(
                    count = displayableCast.size,
                    key = { index -> displayableCast[index].id.toString() },
                    contentType = { index -> displayableCast[index] },
                ) {
                    val currentPerson = displayableCast[it]
                    val paddingForFirst = if (it == 0) 16.dp else 0.dp
                    SubcomposeLayout(modifier = Modifier.padding(start = paddingForFirst)) { constraints ->
                        // let's compose and measure the AsyncImage and the Icon
                        val imagePlaceable = subcompose("AsyncImage") {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(currentPerson.profileUrl)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .diskCacheKey(currentPerson.id.toString())
                                    .networkCachePolicy(CachePolicy.READ_ONLY)
                                    .build(),
                                contentScale = ContentScale.Crop,
                                contentDescription = currentPerson.name,
                                modifier = Modifier
                                    .height(280.dp)
                                    .padding(end = paddingEnd)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }.first().measure(
                            constraints.copy(
                                minWidth = 0,
                                maxWidth = constraints.maxWidth
                            )
                        )

                        // now let's use the width of the image to measure the Text
                        val personPlaceable = subcompose("Person") {
                            Text(
                                currentPerson.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(top = 4.dp)

                                    // let's use the calculated width of the image - the end padding
                                    .width(with(LocalDensity.current) { imagePlaceable.width.toDp() - paddingEnd })
                            )
                        }.first().measure(constraints.copy(maxWidth = imagePlaceable.width))


                        // now let's use the width of the image to measure the Text
                        val characterPlaceable = subcompose("Character") {
                            currentPerson.character?.let { character ->
                                Text(
                                    character,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier

                                        // let's use the calculated width of the image - the end padding
                                        .width(with(LocalDensity.current) { imagePlaceable.width.toDp() - paddingEnd })
                                )
                            }
                        }.first().measure(constraints.copy(maxWidth = imagePlaceable.width))

                        // let's put everything together
                        layout(
                            width = imagePlaceable.width,
                            height = imagePlaceable.height + personPlaceable.height + characterPlaceable.height
                        ) {
                            imagePlaceable.placeRelative(0, 0)
                            personPlaceable.placeRelative(0, imagePlaceable.height)
                            characterPlaceable.placeRelative(
                                0,
                                imagePlaceable.height + personPlaceable.height
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VideosSection(
    displayableYoutubeVideos: List<VideoResult>?
) {
    val context = LocalContext.current
    displayableYoutubeVideos?.let { videos ->
        Column {
            Text(
                "Videos",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )
            LazyRow(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)) {
                items(
                    count = videos.size,
                    key = { index -> videos[index].id.toString() },
                    contentType = { index -> videos[index] },
                ) { index ->
                    val currentVideo = videos[index]
                    val paddingEnd = 16.dp
                    val paddingForFirst = if (index == 0) 16.dp else 0.dp
                    SubcomposeLayout(modifier = Modifier.padding(start = paddingForFirst)) { constraints ->
                        // let's compose and measure the AsyncImage and the Icon
                        val imagePlaceable = subcompose("AsyncImage") {
                            Box {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data("https://img.youtube.com/vi/${currentVideo.key}/0.jpg")
                                        .diskCachePolicy(CachePolicy.ENABLED)
                                        .memoryCachePolicy(CachePolicy.ENABLED)
                                        .diskCacheKey(currentVideo.key)
                                        .networkCachePolicy(CachePolicy.READ_ONLY)
                                        .build(),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = currentVideo.name,
                                    modifier = Modifier
                                        .height(180.dp)
                                        .padding(end = paddingEnd)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            currentVideo.openYoutubeVideo(context)
                                        }
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_play_circle),
                                    contentDescription = "play",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(54.dp)
                                )
                            }
                        }.first().measure(
                            constraints.copy(
                                minWidth = 0,
                                maxWidth = constraints.maxWidth
                            )
                        )

                        // now let's use the width of the image to measure the Text
                        val textPlaceable = subcompose("Text") {
                            currentVideo.name?.let { name ->
                                Text(
                                    name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .padding(top = 8.dp)

                                        // let's use the calculated width of the image - the end padding
                                        .width(with(LocalDensity.current) { imagePlaceable.width.toDp() - paddingEnd })
                                )
                            }
                        }.first().measure(constraints.copy(maxWidth = imagePlaceable.width))

                        // let's put everything together
                        layout(
                            width = imagePlaceable.width,
                            height = imagePlaceable.height + textPlaceable.height
                        ) {
                            imagePlaceable.placeRelative(0, 0)
                            textPlaceable.placeRelative(0, imagePlaceable.height)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoAndProgressIndicatorSection(
    movie: Movie?,
    isLoading: Boolean
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
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
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(start = 20.dp)
            )
        }
    }
}

@Composable
private fun TitleAndYearSection(movie: Movie?) {
    Row(
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
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
}

@Composable
private fun OverviewSection(movie: Movie?) {
    Text(
        movie?.overview ?: "",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
    )
}

@Composable
private fun DirectedBySection(movie: Movie?) {
    val directorsString = movie?.getDirectorsString(stringResource(R.string.and))
    val shouldBeDisplayed = directorsString?.isNotEmpty() == true
    AnimatedVisibility(visible = shouldBeDisplayed) {
        Text(
            stringResource(R.string.directed_by, directorsString ?: ""),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        )
    }
}

@Composable
private fun PercentageTitleAndDurationSection(movie: Movie?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val percentage = movie?.voteAverage?.times(10)?.toInt()
        CircularProgressBar(
            percentage = percentage ?: 0
        )

        val genresString = movie?.getGenresString(stringResource(R.string.and))
        AnimatedVisibility(visible = !genresString.isNullOrEmpty()) {
            Text(
                "$genresString",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 10.dp)
            )
        }


        Box(modifier = Modifier.weight(1f))

        AnimatedVisibility(visible = (movie?.runtime ?: 0) > 0) {
            Row {
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
    }
}

@Composable
private fun CircularProgressBar(
    percentage: Int,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier.size(size)
) {
    val mediumColor = Color(0xFFFFA000)
    val badColor = DislikeButtonBackground
    val color = when {
        percentage < 50 -> badColor
        percentage in 50..69 -> mediumColor
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
        ),
        credits = Credits(
            crew = listOf(
                CrewMember(id = 1, name = "Mike White", department = "Directing"),
                CrewMember(id = 2, name = "Brenda Chapman", department = "Directing"),
            )
        ),
    )
    val isLoading = false

    FilmatchTheme(darkTheme = true) {
        MovieDetailsScreenContent(
            fullMovie = movie,
            isLoading = isLoading
        )
    }
}