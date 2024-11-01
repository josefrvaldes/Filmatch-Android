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
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import es.josevaldes.core.utils.getDeviceLocale
import es.josevaldes.data.model.Movie
import es.josevaldes.data.model.User
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.model.MovieSwipedStatus
import es.josevaldes.filmatch.model.SwipeableMovie
import es.josevaldes.filmatch.ui.theme.BackButtonBackground
import es.josevaldes.filmatch.ui.theme.DislikeButtonBackground
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.ui.theme.LikeButtonBackground
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


val user = User(
    id = "1",
    username = "Joselete Valdés",
    photoUrl = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/6018d2fb-507f-4b50-af6a-b593b6c6eeb9/db1so0b-cd9d0be3-3691-4728-891b-f1505b7e1dc8.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7InBhdGgiOiJcL2ZcLzYwMThkMmZiLTUwN2YtNGI1MC1hZjZhLWI1OTNiNmM2ZWViOVwvZGIxc28wYi1jZDlkMGJlMy0zNjkxLTQ3MjgtODkxYi1mMTUwNWI3ZTFkYzgucG5nIn1dXSwiYXVkIjpbInVybjpzZXJ2aWNlOmZpbGUuZG93bmxvYWQiXX0.9awWi0q7WpdwQDXG9quXvnDVo0NUDqF_S9ygzRxCbEM",
    email = ""
)

@Composable
fun SlideMovieScreen(onNavigateToMovieDetailsScreen: (Movie) -> Unit) {
    val viewModel: SlideMovieViewModel = hiltViewModel()
    viewModel.setLanguage(getDeviceLocale())
    val context = LocalContext.current
    val vibrationManager = remember { VibrationUtils(context) }

    Scaffold(
        topBar = { UserTopBar(user) },
        bottomBar = {
            BottomLikeDislike(
                onLikeClicked = {
                    if (viewModel.likeButtonAction.value == null) {
                        vibrationManager.vibrateOneShot()
                        viewModel.onLikeButtonClicked()
                    }
                },
                onDislikeClicked = {
                    if (viewModel.likeButtonAction.value == null) {
                        vibrationManager.vibrateOneShot()
                        viewModel.onDislikeButtonClicked()
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BackButtonRow()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                SwipeableMoviesComponent(onNavigateToMovieDetailsScreen)
            }
        }
    }
}


@Preview
@Composable
fun PreviewBottomLikeDislike() {
    FilmatchTheme {
        BottomLikeDislike(
            onLikeClicked = {},
            onDislikeClicked = {}
        )
    }
}

@Composable
private fun SwipeableMoviesComponent(onNavigateToMovieDetailsScreen: (Movie) -> Unit) {
    val viewModel = hiltViewModel<SlideMovieViewModel>()
    val observableMovies = viewModel.observableMovies.collectAsState()

    PreloadMoviePosters(observableMovies.value)
    val likeButtonAction = viewModel.likeButtonAction.collectAsState()
    observableMovies.value.reversed().forEachIndexed { index, movie ->
        key(movie.movie.id) {
            SwipeableMovieView(
                likeButtonAction = likeButtonAction.value,
                observableMovies = observableMovies,
                movie = movie,
                index = index,
                onSwipeCompleted = {
                    viewModel.clearSwipeAction(); viewModel.onSwipe()
                },
                onMovieClicked = { movie ->
                    onNavigateToMovieDetailsScreen(movie)
                }
            )
        }
    }
}

@Composable
private fun PreloadMoviePosters(
    observableMovies: List<SwipeableMovie>,
) {
    val context = LocalContext.current
    LaunchedEffect(observableMovies) {
        observableMovies.forEach { movie ->
            preloadPoster(context, movie.movie)
        }
    }
}


private fun preloadPoster(
    context: Context,
    movie: Movie
) {
    Coil.imageLoader(context).enqueue(
        ImageRequest.Builder(context)
            .data(movie.posterUrl)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
    )
}

@Composable
private fun SwipeableMovieView(
    likeButtonAction: SlideMovieViewModel.LikeButtonAction?,
    observableMovies: State<List<SwipeableMovie>>,
    movie: SwipeableMovie,
    index: Int,
    onSwipeCompleted: () -> Unit,
    onMovieClicked: (Movie) -> Unit
) {
    val translationOffset = remember { Animatable(0f) }
    val rotationOffset = getProperRotation(movie, index, observableMovies.value)
    val currentSwipedStatus = remember { mutableStateOf(movie.swipedStatus) }

    val blurRadius = getProperBlurRadius(index = index, listSize = observableMovies.value.size)
    val tint = getProperTint(currentSwipedStatus)

    PerformAnimationAccordingToLikeButtonAction(
        likeButtonAction,
        index,
        observableMovies,
        rotationOffset,
        translationOffset,
        onSwipeCompleted
    )


    Box(
        modifier = Modifier
            .setupMovieGraphics(movie, rotationOffset)
            .zIndex(index.toFloat())
            .offset { IntOffset(translationOffset.value.roundToInt(), 0) }
            .swipeHandler(
                enabled = index == observableMovies.value.size - 1,
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
            modifier = Modifier
                .matchParentSize(),
            onMovieClicked = onMovieClicked
        )
    }
}

@Composable
private fun PerformAnimationAccordingToLikeButtonAction(
    likeButtonAction: SlideMovieViewModel.LikeButtonAction?,
    index: Int,
    observableMovies: State<List<SwipeableMovie>>,
    rotationOffset: Animatable<Float, AnimationVector1D>,
    translationOffset: Animatable<Float, AnimationVector1D>,
    onSwipeCompleted: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(likeButtonAction) {
        if (index == observableMovies.value.size - 1 && likeButtonAction != null) {
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

            onSwipeCompleted()
        }
    }
}

@Composable
private fun getProperRotation(
    movie: SwipeableMovie,
    index: Int,
    moviesToShow: List<SwipeableMovie>
): Animatable<Float, AnimationVector1D> {
    val rotation = remember { Animatable(movie.rotation ?: 0f) }
    if (index == moviesToShow.size - 1) {
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
    modifier: Modifier,
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
            .clickable { onMovieClicked(movie.movie) },

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

    Box(
        modifier = modifier
            .padding(20.dp)
            .clip(RoundedCornerShape(16.dp))
            .height(525.dp)
            .background(tint.value)
    )

}


private suspend fun handleSwipeRelease(
    translationOffset: Animatable<Float, AnimationVector1D>,
    rotationOffset: Animatable<Float, AnimationVector1D>,
    swipedMaxOffset: Int,
    movie: SwipeableMovie,
    currentSwipedStatus: MutableState<MovieSwipedStatus>,
    screenWidth: Int,
    onSwipeCompleted: () -> Unit
) {
    if (translationOffset.value.absoluteValue > swipedMaxOffset) {
        Timber.tag("SlideMovieScreen").d("Swiped confirmed")

        Timber.tag("SlideMovieScreen").d("Removing tint")
        movie.swipedStatus = MovieSwipedStatus.NONE
        currentSwipedStatus.value = movie.swipedStatus

        // animating outside the screen
        val result = translationOffset.animateTo(
            screenWidth * if (translationOffset.value > 0) 1f else -1f,
            animationSpec = spring(stiffness = StiffnessHigh)
        )

        if (result.endReason == AnimationEndReason.Finished) {
            // let's remove the last movie
            Timber.tag("SlideMovieScreen").d("Removing movie: ${movie.movie.title}")
            onSwipeCompleted()
            translationOffset.snapTo(0f)
        }
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
//    Log.d("SlideMovieScreen", "Rotation offset: $newRotationOffset")

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
    onSwipeCompleted: () -> Unit
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

@Composable
private fun BackButtonRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { /* Handle click */ },
            modifier = Modifier
                .clip(CircleShape)
                .size(35.dp)
                .background(BackButtonBackground)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.back_button_content_description),
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
private fun BottomLikeDislike(
    onLikeClicked: () -> Unit,
    onDislikeClicked: () -> Unit
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
                .clickable { onDislikeClicked() },
        )
        Icon(
            painter = painterResource(id = R.drawable.btn_thumbs_up),
            contentDescription = stringResource(R.string.back_button_content_description),
            tint = LikeButtonBackground,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable { onLikeClicked() },
        )
    }
}

@Composable
fun UserTopBar(user: User) {
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
//                    Canvas(modifier = Modifier.matchParentSize()) {
//                        drawCircle(
//                            brush = Brush.radialGradient(
//                                colors = listOf(
//                                    Color.Transparent,
//                                    Color.Black
//                                ),
//                            ),
//                        )
//                    }
            AsyncImage(
                modifier = Modifier.matchParentSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.photoUrl)
                    .build(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_launcher_background),
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