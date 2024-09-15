package es.josevaldes.filmatch

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
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
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
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
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import es.josevaldes.filmatch.model.Movie
import es.josevaldes.filmatch.model.SwipeableMovie
import es.josevaldes.filmatch.model.User
import es.josevaldes.filmatch.ui.theme.BackButtonBackground
import es.josevaldes.filmatch.ui.theme.BackgroundDark
import es.josevaldes.filmatch.ui.theme.DislikeButtonBackground
import es.josevaldes.filmatch.ui.theme.LikeButtonBackground
import es.josevaldes.filmatch.ui.theme.usernameTitleStyle
import kotlinx.coroutines.launch
import okhttp3.internal.cacheGet
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.random.Random


val user = User(
    1,
    "Joselete Valdés",
    "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/6018d2fb-507f-4b50-af6a-b593b6c6eeb9/db1so0b-cd9d0be3-3691-4728-891b-f1505b7e1dc8.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7InBhdGgiOiJcL2ZcLzYwMThkMmZiLTUwN2YtNGI1MC1hZjZhLWI1OTNiNmM2ZWViOVwvZGIxc28wYi1jZDlkMGJlMy0zNjkxLTQ3MjgtODkxYi1mMTUwNWI3ZTFkYzgucG5nIn1dXSwiYXVkIjpbInVybjpzZXJ2aWNlOmZpbGUuZG93bmxvYWQiXX0.9awWi0q7WpdwQDXG9quXvnDVo0NUDqF_S9ygzRxCbEM"
)

val swipeableMovies = mutableListOf(
    SwipeableMovie(
        movie = Movie(
            1,
            "Alien Romulus",
            "https://pics.filmaffinity.com/alien_romulus-177464034-large.jpg"
        ),
    ),
    SwipeableMovie(
        movie = Movie(
            2,
            "Borderlands",
            "https://pics.filmaffinity.com/borderlands-479068097-large.jpg"
        ),
    ),
    SwipeableMovie(
        movie = Movie(
            3,
            "Un Silence",
            "https://pics.filmaffinity.com/un_silence-754363757-large.jpg"
        ),
    ),
    SwipeableMovie(
        movie = Movie(
            4,
            "Speak No Evil",
            "https://pics.filmaffinity.com/speak_no_evil-102462605-large.jpg"
        ),
    ),
    SwipeableMovie(
        movie = Movie(
            5,
            "The Last Duel",
            "https://pics.filmaffinity.com/the_last_duel-563139924-large.jpg"
        ),
    ),
    SwipeableMovie(
        movie = Movie(
            6,
            "Bitelchús Bitelchús",
            "https://pics.filmaffinity.com/beetlejuice_beetlejuice-890586814-large.jpg"
        ),
    ),
)


@Composable
fun SlideMovieScreen() {
    Scaffold(
        topBar = { UserTopBar(user) },
        bottomBar = {
            BottomLikeDislike(
                onLikeClicked = { Log.d("SlideMovieScreen", "Like clicked") },
                onDislikeClicked = { Log.d("SlideMovieScreen", "Dislike clicked") }
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
                GetImages(swipeableMovies)
            }
        }
    }
}

@Composable
private fun GetImages(allMovies: MutableList<SwipeableMovie>) {

    if (allMovies.isEmpty()) return

    val vibrator = LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val coroutineScope = rememberCoroutineScope()
    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels
    val swipedMaxOffset = screenWidth / 2


    val observableMovies = remember {
        allMovies.toMutableStateList()
    }

    val moviesToShow = observableMovies.take(3).reversed()

    moviesToShow.forEachIndexed { index, movie ->
        if (movie.rotation == null) {
            val rotation = Random.nextDouble(0.0, 4.0) * if (observableMovies.size % 2 == 0) 1 else -1
            movie.rotation = rotation.toFloat()
            val translation = Random.nextDouble(0.0, 8.0)
            movie.traslationY = translation.toFloat()
        }
        val offsetX = remember { Animatable(0f) }

        val blurRadius = getProperBlurRadius(index = index, listSize = moviesToShow.size)

        Box(
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = movie.rotation ?: 0f
                    translationY = movie.traslationY ?: 0f
                }
                .zIndex(index.toFloat())
                .offset {
                    IntOffset(offsetX.value.roundToInt(), 0)
                }
                .draggable(
                    enabled = index == moviesToShow.size - 1,
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val previousOffset = offsetX.value
                        val newOffset = offsetX.value + delta
                        coroutineScope.launch {
                            offsetX.snapTo(newOffset)
                        }
                        if (previousOffset.absoluteValue < swipedMaxOffset && newOffset.absoluteValue >= swipedMaxOffset) {
                            Log.d("SlideMovieScreen", "Swiped reached")
                            vibrator.vibrate(
                                VibrationEffect.createOneShot(
                                    20,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        }
                        Log.d("SlideMovieScreen", "offsetX: ${offsetX.value}")
                    },
                    onDragStopped = {
                        if (offsetX.value.absoluteValue > swipedMaxOffset) {
                            Log.d("SlideMovieScreen", "Swiped confirmed")
                            val result = offsetX.animateTo(
                                screenWidth * if (offsetX.value > 0) 1f else -1f,
                                animationSpec = spring(stiffness = StiffnessHigh)
                            )
                            if (result.endReason == AnimationEndReason.Finished) {
                                val lastMovie = moviesToShow.last()
                                observableMovies.remove(lastMovie)
                                offsetX.snapTo(0f)
                            }
                        } else {
                            offsetX.animateTo(
                                0f,
                                animationSpec = spring(stiffness = 500f)
                            )
                        }
                    }
                )
        ) {
            AsyncImage(
                modifier = Modifier
                    .padding(20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .height(525.dp)
                    .blur(
                        radius = blurRadius.value,
                        edgeTreatment = BlurredEdgeTreatment.Companion.Unbounded
                    )
                    .background(BackButtonBackground),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.movie.photoUrl)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCacheKey(movie.movie.photoUrl)
                    .build(),
                contentScale = ContentScale.FillHeight,
                alignment = Alignment.Center,
                contentDescription = movie.movie.title,
            )
        }
    }
}

@Composable
private fun getProperBlurRadius(index: Int, listSize: Int): State<Dp> {
    return animateDpAsState(
        targetValue = when (index) {
            listSize - 1 -> 0.dp
            listSize - 2 -> 5.dp
            else -> 20.dp
        },
        animationSpec = tween(durationMillis = 200),
        label = "Dp Blur Radius"
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
private fun BottomLikeDislike(onLikeClicked: () -> Unit, onDislikeClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp, bottom = 30.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
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