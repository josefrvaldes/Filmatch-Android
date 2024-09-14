package es.josevaldes.filmatch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import es.josevaldes.filmatch.model.User
import es.josevaldes.filmatch.ui.theme.BackButtonBackground
import es.josevaldes.filmatch.ui.theme.DislikeButtonBackground
import es.josevaldes.filmatch.ui.theme.LikeButtonBackground
import es.josevaldes.filmatch.ui.theme.usernameTitleStyle

val user = User(
    1,
    "Joselete Valdés",
    "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/6018d2fb-507f-4b50-af6a-b593b6c6eeb9/db1so0b-cd9d0be3-3691-4728-891b-f1505b7e1dc8.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7InBhdGgiOiJcL2ZcLzYwMThkMmZiLTUwN2YtNGI1MC1hZjZhLWI1OTNiNmM2ZWViOVwvZGIxc28wYi1jZDlkMGJlMy0zNjkxLTQ3MjgtODkxYi1mMTUwNWI3ZTFkYzgucG5nIn1dXSwiYXVkIjpbInVybjpzZXJ2aWNlOmZpbGUuZG93bmxvYWQiXX0.9awWi0q7WpdwQDXG9quXvnDVo0NUDqF_S9ygzRxCbEM"
)

val moviePosters = listOf(
    "https://pics.filmaffinity.com/alien_romulus-177464034-large.jpg",
    "https://pics.filmaffinity.com/borderlands-479068097-large.jpg",
    "https://pics.filmaffinity.com/un_silence-754363757-large.jpg"
)

@Composable
fun SlideMovieScreen() {
    Scaffold(
        topBar = { UserTopBar(user) },
        bottomBar = {
            BottomLikeDislike()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BackButtonRow()
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    modifier = Modifier
                        .padding(20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .height(525.dp)
                        .background(BackButtonBackground),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(moviePosters.first())
                        .build(),
                    contentScale = ContentScale.FillHeight,
                    alignment = Alignment.Center,
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = moviePosters.first()
                )
            }
        }
    }
}

private fun getImages() = @Composable {
    moviePosters.forEachIndexed { index, it ->
        AsyncImage(
            modifier = Modifier
                .padding(20.dp)
                .clip(RoundedCornerShape(16.dp))
                .height(525.dp)
                .background(BackButtonBackground),
            model = ImageRequest.Builder(LocalContext.current)
                .data(it)
                .build(),
            contentScale = ContentScale.FillHeight,
            alignment = Alignment.Center,
            placeholder = painterResource(R.drawable.ic_launcher_background),
            contentDescription = it
        )
    }
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
private fun BottomLikeDislike() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp, bottom = 30.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { },
            modifier = Modifier
                .clip(CircleShape)
                .size(100.dp)
                .background(DislikeButtonBackground)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_thumbs_down),
                contentDescription = stringResource(R.string.back_button_content_description),
            )
        }
        IconButton(
            onClick = { },
            modifier = Modifier
                .clip(CircleShape)
                .size(100.dp)
                .background(LikeButtonBackground)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_thumbs_up),
                contentDescription = stringResource(R.string.back_button_content_description),
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