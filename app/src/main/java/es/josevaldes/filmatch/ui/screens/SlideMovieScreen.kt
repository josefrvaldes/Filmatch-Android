package es.josevaldes.filmatch.ui.screens


import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring.StiffnessHigh
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.Coil
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import es.josevaldes.data.model.Movie
import es.josevaldes.data.model.MovieFilters
import es.josevaldes.data.model.User
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.errors.ErrorMessageWrapper
import es.josevaldes.filmatch.model.MovieSwipedStatus
import es.josevaldes.filmatch.model.SwipeableMovie
import es.josevaldes.filmatch.ui.theme.BackButtonBackground
import es.josevaldes.filmatch.ui.theme.DislikeButtonBackground
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.ui.theme.LikeButtonBackground
import es.josevaldes.filmatch.ui.theme.getDefaultAccentButtonColors
import es.josevaldes.filmatch.ui.theme.usernameTitleStyle
import es.josevaldes.filmatch.utils.VibrationUtils
import es.josevaldes.filmatch.viewmodels.SlideMovieViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


@Composable
fun SlideMovieScreen(onNavigateToMovieDetailsScreen: (Movie) -> Unit) {
    val viewModel: SlideMovieViewModel = hiltViewModel()
    val context = LocalContext.current
    val vibrationManager = remember { VibrationUtils(context) }
    val likeButtonAction by viewModel.likeButtonAction.collectAsState()


    Scaffold(
        modifier = Modifier
            .statusBarsPadding(),
        topBar = {
            TopBar { filters ->
                viewModel.onNewFiltersSelected(filters)
            }
        },
        bottomBar = {
            LikeDislikeBottomSection(
                enabled = likeButtonAction == null,
                onLikeClicked = {
                    vibrationManager.vibrateOneShot()
                    viewModel.onLikeButtonClicked()
                },
                onDislikeClicked = {
                    vibrationManager.vibrateOneShot()
                    viewModel.onDislikeButtonClicked()
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            SwipeableMoviesComponent(onNavigateToMovieDetailsScreen)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheetDialog(
    showFiltersBottomSheet: MutableState<Boolean>,
    selectedFilters: MovieFilters,
    onFiltersSelected: (MovieFilters) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { showFiltersBottomSheet.value = false },
        sheetState = sheetState,
    ) {
        FiltersScreen(selectedFilters) {
            onFiltersSelected(it)
            showFiltersBottomSheet.value = false
        }
    }
}


@Preview
@Composable
fun PreviewBottomLikeDislike() {
    FilmatchTheme {
        LikeDislikeBottomSection(
            onLikeClicked = {},
            onDislikeClicked = {},
            enabled = true
        )
    }
}

@Composable
private fun SwipeableMoviesComponent(onNavigateToMovieDetailsScreen: (Movie) -> Unit) {
    val viewModel = hiltViewModel<SlideMovieViewModel>()
    val context = LocalContext.current

    val observableMovies = viewModel.observableMovies.collectAsState()
    val likeButtonAction = viewModel.likeButtonAction.collectAsState()
    val movieThatWillBeObservableNext = viewModel.movieThatWillBeObservableNext.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState(null)
    val errorMessageWrapper = remember { ErrorMessageWrapper(context) }


    LaunchedEffect(movieThatWillBeObservableNext.value) {
        preloadMoviePoster(context, movieThatWillBeObservableNext.value?.movie)
    }

    errorMessage?.let { error ->
        val stringError = errorMessageWrapper.getErrorMessage(error)
        Text(stringError)
    } ?: run {
        if (isLoading && observableMovies.value.isEmpty()) {
            CircularProgressIndicator()
        } else if (observableMovies.value.isEmpty()) {
            Text(stringResource(R.string.no_movies_to_show))
        } else {
            observableMovies.value.reversed().forEachIndexed { index, movie ->
                key(movie.movie.id) {
                    SwipeableMovieView(
                        likeButtonAction = likeButtonAction.value,
                        observableMoviesCount = observableMovies.value.size,
                        movie = movie,
                        index = index,
                        onSwipeCompleted = { movie ->
                            viewModel.onSwipe(movie)
                        },
                        onMovieClicked = { movie ->
                            onNavigateToMovieDetailsScreen(movie)
                        },
                    )
                }
            }
        }
    }
}


private fun preloadMoviePoster(
    context: Context,
    movie: Movie?,
) {
    movie?.let {
        Coil.imageLoader(context).enqueue(
            ImageRequest.Builder(context)
                .data(movie.posterUrl)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
        )
    }
}

@Composable
private fun SwipeableMovieView(
    likeButtonAction: SlideMovieViewModel.LikeButtonAction?,
    observableMoviesCount: Int,
    movie: SwipeableMovie,
    index: Int,
    onSwipeCompleted: (SwipeableMovie) -> Unit,
    onMovieClicked: (Movie) -> Unit,
) {
    val translationOffset = remember { Animatable(0f) }
    val rotationOffset = getProperRotation(movie, index, observableMoviesCount)
    val currentSwipedStatus = remember { mutableStateOf(movie.swipedStatus) }

    val blurRadius = getProperBlurRadius(index = index, listSize = observableMoviesCount)
    val tint = getProperTint(currentSwipedStatus)
    val context = LocalContext.current

    // every time the like button action changes, we animate the movie
    LaunchedEffect(likeButtonAction) {
        performAnimationAccordingToLikeButtonAction(
            context,
            likeButtonAction,
            index,
            observableMoviesCount,
            rotationOffset,
            translationOffset,
            onSwipeCompleted,
            movie
        )
    }

    Box(
        modifier = Modifier
            .setupMovieGraphics(movie, rotationOffset)
            .zIndex(index.toFloat())
            .offset { IntOffset(translationOffset.value.roundToInt(), 0) }
            .swipeHandler(
                enabled = index == observableMoviesCount - 1,
                translationOffset = translationOffset,
                rotationOffset = rotationOffset,
                movie = movie,
                currentSwipedStatus = currentSwipedStatus,
                onSwipeCompleted = onSwipeCompleted
            )
    ) {
        PosterImageView(
            movie = movie,
            blurRadius = blurRadius,
            tint = tint,
            onMovieClicked = onMovieClicked
        )
    }
}

private suspend fun performAnimationAccordingToLikeButtonAction(
    context: Context,
    likeButtonAction: SlideMovieViewModel.LikeButtonAction?,
    index: Int,
    observableMoviesCount: Int,
    rotationOffset: Animatable<Float, AnimationVector1D>,
    translationOffset: Animatable<Float, AnimationVector1D>,
    onSwipeCompleted: (SwipeableMovie) -> Unit,
    movie: SwipeableMovie
) {
    if (index == observableMoviesCount - 1 && likeButtonAction != null) {
        val targetTranslationOffset = when (likeButtonAction) {
            SlideMovieViewModel.LikeButtonAction.LIKE -> context.resources.displayMetrics.widthPixels.toFloat() + 300
            SlideMovieViewModel.LikeButtonAction.DISLIKE -> -context.resources.displayMetrics.widthPixels.toFloat() - 300
        }
        val targetRotationOffset = when (likeButtonAction) {
            SlideMovieViewModel.LikeButtonAction.LIKE -> 25f
            SlideMovieViewModel.LikeButtonAction.DISLIKE -> -25f
        }


        coroutineScope {
            val rotationJob = async {
                rotationOffset.animateTo(
                    targetRotationOffset,
                    animationSpec = tween(200)
                )
            }
            val translationJob = async {
                translationOffset.animateTo(
                    targetTranslationOffset,
                    animationSpec = tween(200)
                )
            }
            awaitAll(rotationJob, translationJob)
        }

        onSwipeCompleted(movie)
    }
}

@Composable
private fun getProperRotation(
    movie: SwipeableMovie,
    index: Int,
    observableMoviesCount: Int
): Animatable<Float, AnimationVector1D> {
    val rotation = remember { Animatable(movie.rotation ?: 0f) }
    if (index == observableMoviesCount - 1) {
        LaunchedEffect(Unit) {
            rotation.animateTo(0f)
        }
    }
    return rotation
}

@Composable
private fun PosterImageView(
    movie: SwipeableMovie,
    blurRadius: State<Dp>,
    tint: State<Color>,
    onMovieClicked: (Movie) -> Unit
) {
    AsyncImage(
        filterQuality = FilterQuality.Medium,
        modifier = Modifier
            .padding(20.dp)
            .clip(RoundedCornerShape(16.dp))
            .height(525.dp)
            .blur(
                radius = blurRadius.value,
                edgeTreatment = BlurredEdgeTreatment.Unbounded
            )
            .background(BackButtonBackground)
            .clickable { onMovieClicked(movie.movie) }
            .drawWithContent {
                drawContent()
                drawRect(
                    color = tint.value,
                    size = size
                )
            },
        model = ImageRequest.Builder(LocalContext.current)
            .data(movie.movie.posterUrl)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCacheKey(movie.movie.posterUrl)
            .networkCachePolicy(CachePolicy.READ_ONLY)
            .build(),
        contentScale = ContentScale.FillHeight,
        alignment = Alignment.Center,
        contentDescription = movie.movie.title,
    )
}


private suspend fun handleSwipeRelease(
    translationOffset: Animatable<Float, AnimationVector1D>,
    rotationOffset: Animatable<Float, AnimationVector1D>,
    swipedMaxOffset: Int,
    movie: SwipeableMovie,
    currentSwipedStatus: MutableState<MovieSwipedStatus>,
    screenWidth: Int,
    onSwipeCompleted: (SwipeableMovie) -> Unit
) {
    if (translationOffset.value.absoluteValue > swipedMaxOffset) {
        Timber.tag("SlideMovieScreen").d("Swiped confirmed")


        // animating outside the screen
        val result = translationOffset.animateTo(
            screenWidth * if (translationOffset.value > 0) 1f else -1f,
            animationSpec = spring(stiffness = StiffnessHigh)
        )

        if (result.endReason == AnimationEndReason.Finished) {
            // let's remove the last movie
            Timber.tag("SlideMovieScreen").d("Removing movie: ${movie.movie.title}")
            onSwipeCompleted(movie)
            translationOffset.snapTo(0f)
        }


        Timber.tag("SlideMovieScreen").d("Removing tint")
        movie.swipedStatus = MovieSwipedStatus.NONE
        currentSwipedStatus.value = movie.swipedStatus
    } else {
        coroutineScope {
            val rotationJob = async {
                rotationOffset.animateTo(0f, animationSpec = spring(stiffness = 500f))
            }
            val translationJob = async {
                translationOffset.animateTo(0f, animationSpec = spring(stiffness = 500f))
            }
            rotationJob.await()
            translationJob.await()
        }
    }
}

private fun handleSwipeMovement(
    translationOffset: Animatable<Float, AnimationVector1D>,
    rotationOffset: Animatable<Float, AnimationVector1D>,
    delta: Float,
    coroutineScope: CoroutineScope,
    swipedMaxOffset: Int,
    vibrationUtils: VibrationUtils,
    movie: SwipeableMovie,
    currentSwipedStatus: MutableState<MovieSwipedStatus>
) {
    val previousTranslationOffset = translationOffset.value
    val newTranslationOffset = translationOffset.value + delta

    val newRotationOffset = rotationOffset.value + delta / 50

    coroutineScope.launch {
        translationOffset.snapTo(newTranslationOffset)
        rotationOffset.snapTo(newRotationOffset)
    }
    if (previousTranslationOffset.absoluteValue < swipedMaxOffset && newTranslationOffset.absoluteValue >= swipedMaxOffset) {
        Timber.tag("SlideMovieScreen").d("Swiped reached")
        vibrationUtils.vibrateOneShot()

        // box
        movie.swipedStatus = if (newTranslationOffset > 0) {
            Timber.tag("SlideMovieScreen").d("Tinting green")
            MovieSwipedStatus.LIKED
        } else {
            Timber.tag("SlideMovieScreen").d("Tinting red")
            MovieSwipedStatus.DISLIKED
        }
        currentSwipedStatus.value = movie.swipedStatus


    } else if (previousTranslationOffset.absoluteValue >= swipedMaxOffset && newTranslationOffset.absoluteValue < swipedMaxOffset) {
        Timber.tag("SlideMovieScreen").d("Removing tint")
        movie.swipedStatus = MovieSwipedStatus.NONE
        currentSwipedStatus.value = movie.swipedStatus
    }
}

@Composable
private fun Modifier.swipeHandler(
    enabled: Boolean,
    translationOffset: Animatable<Float, AnimationVector1D>,
    rotationOffset: Animatable<Float, AnimationVector1D>,
    movie: SwipeableMovie,
    currentSwipedStatus: MutableState<MovieSwipedStatus>,
    onSwipeCompleted: (SwipeableMovie) -> Unit
): Modifier {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val screenWidth = context.resources.displayMetrics.widthPixels
    val swipedMaxOffset = screenWidth / 3
    val vibrationUtils = remember { VibrationUtils(context) }
    return draggable(
        enabled = enabled,
        orientation = Orientation.Horizontal,
        state = rememberDraggableState { delta ->
            handleSwipeMovement(
                translationOffset,
                rotationOffset,
                delta,
                coroutineScope,
                swipedMaxOffset,
                vibrationUtils,
                movie,
                currentSwipedStatus
            )
        },
        onDragStopped = {
            handleSwipeRelease(
                translationOffset,
                rotationOffset,
                swipedMaxOffset,
                movie,
                currentSwipedStatus,
                screenWidth,
                onSwipeCompleted
            )
        }
    )
}

private fun Modifier.setupMovieGraphics(
    movie: SwipeableMovie,
    rotation: Animatable<Float, AnimationVector1D>
): Modifier {
    return graphicsLayer {
        rotationZ = rotation.value
        translationY = movie.translationY ?: 0f
    }
}


@Composable
private fun getProperTint(currentSwipedStatus: MutableState<MovieSwipedStatus>) =
    animateColorAsState(
        targetValue = when (currentSwipedStatus.value) {
            MovieSwipedStatus.LIKED -> LikeButtonBackground.copy(0.5f)
            MovieSwipedStatus.DISLIKED -> DislikeButtonBackground.copy(0.5f)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 500),
        label = "swipeColor"
    )

@Composable
private fun getProperBlurRadius(index: Int, listSize: Int): State<Dp> {
    val newValue = when (index) {
        listSize - 1 -> 0.dp
        listSize - 2 -> 5.dp
        else -> 20.dp
    }

    return animateDpAsState(
        targetValue = newValue,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
        label = "blurRadius"
    )
}

@Deprecated("According to the new design, this is not used anymore")
@Composable
private fun BackButtonRow(onBackClicked: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onBackClicked() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(35.dp)
                .clip(CircleShape)
                .background(BackButtonBackground)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.back_button_content_description),
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )
        }

        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = stringResource(R.string.back),
            style = usernameTitleStyle
        )
    }
}


@Composable
private fun LikeDislikeBottomSection(
    onLikeClicked: () -> Unit,
    onDislikeClicked: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp, bottom = 30.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = painterResource(id = R.drawable.btn_thumbs_down),
            contentDescription = stringResource(R.string.back_button_content_description),
            tint = DislikeButtonBackground,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable { if (enabled) onDislikeClicked() },
        )
        Icon(
            painter = painterResource(id = R.drawable.btn_thumbs_up),
            contentDescription = stringResource(R.string.back_button_content_description),
            tint = LikeButtonBackground,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable { if (enabled) onLikeClicked() },
        )
    }
}

@Composable
fun TopBar(onFiltersSelected: (MovieFilters) -> Unit = {}) {

    val showFiltersBottomSheet = remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            stringResource(R.string.discover),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        Button(
            onClick = {
                showFiltersBottomSheet.value = true
            },
            modifier = Modifier
                .padding(vertical = 20.dp),
            colors = getDefaultAccentButtonColors()
        ) {
            Text(
                stringResource(R.string.filters),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }

    if (showFiltersBottomSheet.value) {
        val viewModel = hiltViewModel<SlideMovieViewModel>()
        val selectedFilters = viewModel.movieFilters
        FiltersBottomSheetDialog(showFiltersBottomSheet, selectedFilters, onFiltersSelected)
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TopBarPreview() {
    FilmatchTheme(darkTheme = true) {
        TopBar {}
    }
}

@Deprecated("Use UserTopBar instead")
@Composable
fun UserTopBar() {
    val user = User(
        id = "1",
        username = "Joselete Valdés",
        photoUrl = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/6018d2fb-507f-4b50-af6a-b593b6c6eeb9/db1so0b-cd9d0be3-3691-4728-891b-f1505b7e1dc8.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7InBhdGgiOiJcL2ZcLzYwMThkMmZiLTUwN2YtNGI1MC1hZjZhLWI1OTNiNmM2ZWViOVwvZGIxc28wYi1jZDlkMGJlMy0zNjkxLTQ3MjgtODkxYi1mMTUwNWI3ZTFkYzgucG5nIn1dXSwiYXVkIjpbInVybjpzZXJ2aWNlOmZpbGUuZG93bmxvYWQiXX0.9awWi0q7WpdwQDXG9quXvnDVo0NUDqF_S9ygzRxCbEM",
        email = ""
    )
    Row(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape)
        ) {
            AsyncImage(
                modifier = Modifier.matchParentSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.photoUrl)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.content_description_user_image)
            )
        }
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = user.username,
            style = usernameTitleStyle
        )
    }
}

@Preview
@Composable
fun SlideMovieScreenPreview() {
    FilmatchTheme(darkTheme = true) {
        SlideMovieScreen {}
    }
}