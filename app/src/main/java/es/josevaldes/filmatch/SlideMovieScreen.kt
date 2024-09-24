package es.josevaldes.filmatch

import android.content.Context
import android.util.Log
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
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
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.Coil
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import es.josevaldes.filmatch.model.Movie
import es.josevaldes.filmatch.model.MovieSwipedStatus
import es.josevaldes.filmatch.model.SwipeableMovie
import es.josevaldes.filmatch.model.User
import es.josevaldes.filmatch.ui.theme.BackButtonBackground
import es.josevaldes.filmatch.ui.theme.DislikeButtonBackground
import es.josevaldes.filmatch.ui.theme.LikeButtonBackground
import es.josevaldes.filmatch.ui.theme.usernameTitleStyle
import es.josevaldes.filmatch.utils.VibrationUtils
import es.josevaldes.filmatch.utils.getDeviceLocale
import es.josevaldes.filmatch.viewmodels.SlideMovieViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.random.Random


val user = User(
    1,
    "Joselete Valdés",
    "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/6018d2fb-507f-4b50-af6a-b593b6c6eeb9/db1so0b-cd9d0be3-3691-4728-891b-f1505b7e1dc8.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7InBhdGgiOiJcL2ZcLzYwMThkMmZiLTUwN2YtNGI1MC1hZjZhLWI1OTNiNmM2ZWViOVwvZGIxc28wYi1jZDlkMGJlMy0zNjkxLTQ3MjgtODkxYi1mMTUwNWI3ZTFkYzgucG5nIn1dXSwiYXVkIjpbInVybjpzZXJ2aWNlOmZpbGUuZG93bmxvYWQiXX0.9awWi0q7WpdwQDXG9quXvnDVo0NUDqF_S9ygzRxCbEM"
)

@Composable
fun SlideMovieScreen() {
    val viewModel: SlideMovieViewModel = hiltViewModel()
    val deviceLanguage = getDeviceLocale()
    viewModel.setLanguage(deviceLanguage)
    val context = LocalContext.current
    val vibrationManager = remember { VibrationUtils(context) }

    Scaffold(
        topBar = { UserTopBar(user) },
        bottomBar = {
            BottomLikeDislike(
                viewModel.swipeAction,
                onLikeClicked = { vibrationManager.vibrateOneShot(); viewModel.onLikeButtonClicked() },
                onDislikeClicked = { vibrationManager.vibrateOneShot(); viewModel.onDislikeButtonClicked() }
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
                SwipeableMoviesComponent(viewModel)
            }
        }
    }
}

@Composable
private fun SwipeableMoviesComponent(viewModel: SlideMovieViewModel) {
    var counter by remember { mutableIntStateOf(0) }
    val moviesLazyPaging = viewModel.moviesFlow.collectAsLazyPagingItems()

    when (moviesLazyPaging.loadState.refresh) {
        is LoadState.Loading -> {
            CircularProgressIndicator()
            return
        }

        is LoadState.Error -> {
            Text("Error loading movies")
            return
        }

        is LoadState.NotLoading -> {
            Log.d("SlideMovieScreen", "Not loading")
        }
    }


    val observableMovies = remember {
        val list = mutableListOf<SwipeableMovie>()
        if (moviesLazyPaging.itemSnapshotList.size > 0) {
            moviesLazyPaging.itemSnapshotList.items.take(3)
                .forEach { counter++; list.add(SwipeableMovie(it)) }
        }
        list.toMutableStateList()
    }
    InitializeMovies(counter, observableMovies)
    PreloadMoviePosters(observableMovies)

    // Let's add a new movie to the list after one movie has been swiped, also we will preload its poster.
    // This code will be executed everytime the observableMovies list size changes.
    val context = LocalContext.current
    LaunchedEffect(observableMovies.size) {
        if (observableMovies.size < 3) {
            val nullableCurrentMovie = moviesLazyPaging[counter]
            nullableCurrentMovie?.let { currentMovie ->
                observableMovies.add(SwipeableMovie(currentMovie))
                preloadPoster(context, currentMovie)
                val nullableNextMovie = moviesLazyPaging[counter + 1]
                nullableNextMovie?.let { nextMovie ->
                    preloadPoster(context, nextMovie)
                }
                counter++
            }
        }
    }


    val swipeAction = viewModel.swipeAction.collectAsState()


    observableMovies.reversed().forEachIndexed { index, movie ->
        key(movie.movie.id) {
            SwipeableMovieView(
                swipeAction,
                observableMovies,
                movie,
                index
            ) { viewModel.clearSwipeAction() }
        }
    }
}

@Composable
private fun InitializeMovies(counter: Int, allMovies: List<SwipeableMovie>) {
    allMovies.forEach { swipeableMovie ->
        if (swipeableMovie.rotation == null) {
            swipeableMovie.rotation = if (counter == 0) {
                0f
            } else if (counter % 2 == 0) {
                Random.nextDouble(0.0, 4.0).toFloat()
            } else {
                Random.nextDouble(-4.0, 0.0).toFloat()
            }
            val translation = Random.nextDouble(0.0, 8.0)
            swipeableMovie.traslationY = translation.toFloat()
        }
    }
}

@Composable
private fun PreloadMoviePosters(
    observableMovies: SnapshotStateList<SwipeableMovie>,
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
    swipeAction: State<SlideMovieViewModel.SwipeAction?>,
    observableMovies: SnapshotStateList<SwipeableMovie>,
    movie: SwipeableMovie,
    index: Int,
    onSwipeCompleted: () -> Unit,
) {
    val translationOffset = remember { Animatable(0f) }
    val rotationOffset = getProperRotation(movie, index, observableMovies)
    val currentSwipedStatus = remember { mutableStateOf(movie.swipedStatus) }

    val blurRadius = getProperBlurRadius(index = index, listSize = observableMovies.size)
    val tint = getProperTint(currentSwipedStatus)

    PerformAnimationAccordingToSwipeAction(
        swipeAction,
        index,
        observableMovies,
        rotationOffset,
        translationOffset,
        movie,
        onSwipeCompleted
    )


    Box(
        modifier = Modifier
            .setupMovieGraphics(movie, rotationOffset)
            .zIndex(index.toFloat())
            .offset { IntOffset(translationOffset.value.roundToInt(), 0) }
            .swipeHandler(
                enabled = index == observableMovies.size - 1,
                translationOffset = translationOffset,
                rotationOffset = rotationOffset,
                movie = movie,
                currentSwipedStatus = currentSwipedStatus,
                observableMovies = observableMovies
            )
    ) {
        PosterImageView(
            movie = movie,
            blurRadius = blurRadius,
            tint = tint,
            modifier = Modifier.matchParentSize()
        )
    }
}

@Composable
private fun PerformAnimationAccordingToSwipeAction(
    swipeAction: State<SlideMovieViewModel.SwipeAction?>,
    index: Int,
    observableMovies: SnapshotStateList<SwipeableMovie>,
    rotationOffset: Animatable<Float, AnimationVector1D>,
    translationOffset: Animatable<Float, AnimationVector1D>,
    movie: SwipeableMovie,
    onSwipeCompleted: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(swipeAction.value) {
        if (index == observableMovies.size - 1 && swipeAction.value != null) {
            val targetTranslationOffset = when (swipeAction.value) {
                SlideMovieViewModel.SwipeAction.LIKE -> context.resources.displayMetrics.widthPixels.toFloat() + 300
                SlideMovieViewModel.SwipeAction.DISLIKE -> -context.resources.displayMetrics.widthPixels.toFloat() - 300
                null -> 0f
            }
            val targetRotationOffset = when (swipeAction.value) {
                SlideMovieViewModel.SwipeAction.LIKE -> 25f
                SlideMovieViewModel.SwipeAction.DISLIKE -> -25f
                null -> 0f
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
            observableMovies.remove(movie)
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
    modifier: Modifier
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
            .background(BackButtonBackground),

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
    observableMovies: SnapshotStateList<SwipeableMovie>,
    translationOffset: Animatable<Float, AnimationVector1D>,
    rotationOffset: Animatable<Float, AnimationVector1D>,
    swipedMaxOffset: Int,
    movie: SwipeableMovie,
    currentSwipedStatus: MutableState<MovieSwipedStatus>,
    screenWidth: Int,
) {
    if (translationOffset.value.absoluteValue > swipedMaxOffset) {
        Log.d("SlideMovieScreen", "Swiped confirmed")

        Log.d("SlideMovieScreen", "Removing tint")
        movie.swipedStatus = MovieSwipedStatus.NONE
        currentSwipedStatus.value = movie.swipedStatus

        // animating outside the screen
        val result = translationOffset.animateTo(
            screenWidth * if (translationOffset.value > 0) 1f else -1f,
            animationSpec = spring(stiffness = StiffnessHigh)
        )

        if (result.endReason == AnimationEndReason.Finished) {
            // let's remove the last movie
            Log.d("SlideMovieScreen", "Removing movie: ${movie.movie.title}")
            val firstMovie = observableMovies.first()
            observableMovies.remove(firstMovie)
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
        Log.d("SlideMovieScreen", "Swiped reached")
        vibrationUtils.vibrateOneShot()

        // box
        movie.swipedStatus = if (newTranslationOffset > 0) {
            Log.d("SlideMovieScreen", "Tinting green")
            MovieSwipedStatus.LIKED
        } else {
            Log.d("SlideMovieScreen", "Tinting red")
            MovieSwipedStatus.DISLIKED
        }
        currentSwipedStatus.value = movie.swipedStatus


    } else if (previousTranslationOffset.absoluteValue >= swipedMaxOffset && newTranslationOffset.absoluteValue < swipedMaxOffset) {
        Log.d("SlideMovieScreen", "Removing tint")
        movie.swipedStatus = MovieSwipedStatus.NONE
        currentSwipedStatus.value = movie.swipedStatus
    }
}

@Composable
private fun Modifier.swipeHandler(
    observableMovies: SnapshotStateList<SwipeableMovie>,
    enabled: Boolean,
    translationOffset: Animatable<Float, AnimationVector1D>,
    rotationOffset: Animatable<Float, AnimationVector1D>,
    movie: SwipeableMovie,
    currentSwipedStatus: MutableState<MovieSwipedStatus>,
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
                observableMovies,
                translationOffset,
                rotationOffset,
                swipedMaxOffset,
                movie,
                currentSwipedStatus,
                screenWidth,
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
        translationY = movie.traslationY ?: 0f
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
    swipeAction: StateFlow<SlideMovieViewModel.SwipeAction?>,
    onLikeClicked: () -> Unit,
    onDislikeClicked: () -> Unit
) {
    val swipeActionValue by swipeAction.collectAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp, bottom = 30.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            enabled = swipeActionValue == null,
            onClick = { onDislikeClicked() },
            modifier = Modifier
                .size(100.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.btn_thumbs_down),
                contentDescription = stringResource(R.string.back_button_content_description),
                tint = DislikeButtonBackground
            )
        }
        IconButton(
            enabled = swipeActionValue == null,
            onClick = { onLikeClicked() },
            modifier = Modifier
                .size(100.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.btn_thumbs_up),
                contentDescription = stringResource(R.string.back_button_content_description),
                tint = LikeButtonBackground
            )
        }
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
    FilmatchApp {
        SlideMovieScreen()
    }
}