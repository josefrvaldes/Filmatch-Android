package es.josevaldes.filmatch.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import es.josevaldes.data.extensions.mappers.toDetailsItemData
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.navigation.Route


@Composable
fun HomeScreen(mainNavController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()


    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        bottomBar = {
            BottomNavBar(
                onItemSelected = { index ->
                    selectedItem = index
                    when (index) {
                        0 -> navigateInBottomBar(navController, Route.SlideMovieRoute)
                        1 -> navigateInBottomBar(navController, Route.SearchRoute)
                        2 -> navigateInBottomBar(navController, Route.MatchesRoute)
                        3 -> navigateInBottomBar(navController, Route.RoomsRoute)
                        4 -> navigateInBottomBar(navController, Route.ProfileRoute)
                    }
                },
                selectedEntry = navBackStackEntry
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.SlideMovieRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Route.SlideMovieRoute> {
                SlideMovieScreen(onNavigateToMovieDetailsScreen = {
                    mainNavController.navigate(Route.MovieDetailsRoute(it.toDetailsItemData()))
                })
            }
            composable<Route.SearchRoute> { SearchScreen() }
            composable<Route.MatchesRoute> { MatchesScreen() }
            composable<Route.RoomsRoute> { RoomsScreen() }
            composable<Route.ProfileRoute> { ProfileScreen() }
        }
    }
}

private fun navigateInBottomBar(navController: NavController, route: Route) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

data class BottomNavBarRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

@Composable
fun BottomNavBar(
    selectedEntry: NavBackStackEntry?,
    onItemSelected: (Int) -> Unit,
) {

    val currentDestination = selectedEntry?.destination
    val topLevelRoutes = listOf(
        BottomNavBarRoute(
            stringResource(R.string.bottom_bar_menu_discover_title),
            Route.SlideMovieRoute,
            Icons.Default.Favorite
        ),
        BottomNavBarRoute(
            stringResource(R.string.bottom_bar_menu_search_title),
            Route.SearchRoute,
            Icons.Default.Search
        ),
        BottomNavBarRoute(
            stringResource(R.string.bottom_bar_menu_matches_title),
            Route.MatchesRoute,
            Icons.Default.ThumbUp
        ),
        BottomNavBarRoute(
            stringResource(R.string.bottom_bar_menu_rooms_title),
            Route.RoomsRoute,
            Icons.Default.Home
        ),
        BottomNavBarRoute(
            stringResource(R.string.bottom_bar_menu_profile_title),
            Route.ProfileRoute,
            Icons.Default.Person
        )
    )

    NavigationBar {
        topLevelRoutes.forEachIndexed { index, currentItem ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any {
                    val currentRoute = currentItem.route.toRouteString
                    val destinationRoute = it.route
                    destinationRoute == currentRoute
                } == true,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(imageVector = currentItem.icon, contentDescription = currentItem.name)
                },
                label = { Text(text = currentItem.name) }
            )
        }
    }
}