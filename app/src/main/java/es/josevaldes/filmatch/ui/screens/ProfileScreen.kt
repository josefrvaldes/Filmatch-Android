package es.josevaldes.filmatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import es.josevaldes.data.model.DiscoverItemData
import es.josevaldes.data.model.Provider
import es.josevaldes.data.model.User
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen() {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val user = profileViewModel.loggedUser.collectAsState(null).value
    val providers = profileViewModel.providers.collectAsState(emptyList()).value

    ProfileScreenContent(user, providers)
}

@Composable
private fun ProfileScreenContent(
    user: User?,
    providers: List<Provider>
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(top = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            user?.let {
                UserHeader(it)
                MyProvidersBox(providers)
                MyWatchListBox()
                MyWatchedListBox()
                MyNotInterestedListBox()
                MySuperlikesListBox()
            }
        }
    }
}

@Composable
fun MediaRow(title: String, lazyItems: LazyPagingItems<DiscoverItemData>) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelLarge,
            )

            Text(
                stringResource(R.string.see_more_title),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable {

                }
            )
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(lazyItems.itemCount) { index ->
                val item = lazyItems[index]
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .height(165.dp)
                        .clip(RoundedCornerShape(4.dp)),
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(item?.posterUrl)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .build(),
                        contentDescription = stringResource(R.string.content_description_user_avatar),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
fun MyWatchListBox() {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val watchList = profileViewModel.myWatchList.collectAsLazyPagingItems()
    MediaRow("Watchlist", watchList)
}

@Composable
fun MyWatchedListBox() {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val watchList = profileViewModel.myWatchedList.collectAsLazyPagingItems()
    MediaRow("Watched", watchList)
}

@Composable
fun MyNotInterestedListBox() {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val watchList = profileViewModel.myNotInterestedList.collectAsLazyPagingItems()
    MediaRow("Not Interested", watchList)
}

@Composable
fun MySuperlikesListBox() {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val watchList = profileViewModel.mySuperLikeList.collectAsLazyPagingItems()
    MediaRow("Superlikes", watchList)
}

@Composable
fun MyProvidersBox(providers: List<Provider>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.filters_screen_provider_title),
                style = MaterialTheme.typography.labelLarge,
            )

            Text(
                stringResource(R.string.see_more_title),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable {

                }
            )
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(providers.size) { index ->
                val provider = providers[index]
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(provider.logoUrl)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCacheKey(provider.logoUrl)
                            .networkCachePolicy(CachePolicy.READ_ONLY)
                            .build(),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = provider.name,
                        modifier = Modifier
                            .width(66.dp)
                            .height(66.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}


@Composable
private fun UserHeader(user: User) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.2f))
        ) {
            if (user.photoUrl.isNullOrEmpty()) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.content_description_user_avatar),
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.Gray
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(user.photoUrl)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .build(),
                    contentDescription = stringResource(R.string.content_description_user_avatar),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        if (user.username.isNullOrEmpty().not()) {
            Text(text = user.username ?: "")
        }
        Text(text = user.email)
    }
}


@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreview() {
    FilmatchTheme {
        ProfileScreenContent(
            User(
                "1",
                "josevaldes",
                "joselete@gmail.com",
//                "https://lh3.googleusercontent.com/a/ACg8ocLeivAj5SFb_FkVyanwy7304pWWKqrIY5TgoFeY5yy-PDe9UVk=s96-c",
                null,
                "123",
            ),
            listOf(
                Provider(
                    1,
                    "Netflix",
                    "https://upload.wikimedia.org/wikipedia/commons/0/08/Netflix_2015_logo.svg",
                    1,
                    mapOf("Netflix" to 1),
                ),
            ),
        )
    }
}