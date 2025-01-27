package es.josevaldes.filmatch.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import es.josevaldes.data.model.DetailsItemData
import es.josevaldes.filmatch.ui.screens.HomeScreen
import es.josevaldes.filmatch.ui.screens.MovieDetailsScreen
import es.josevaldes.filmatch.ui.screens.OnBoardingScreen
import es.josevaldes.filmatch.ui.screens.WelcomeScreen
import kotlin.reflect.typeOf

@Composable
fun MainNavHost(startDestination: Route) {
    val animationDuration = 150
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth }, // starts from the right
                animationSpec = tween(animationDuration)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth }, // ends at the left
                animationSpec = tween(animationDuration)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth }, // from the left when going back
                animationSpec = tween(animationDuration)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth }, // to the right when going back
                animationSpec = tween(animationDuration)
            )
        }
    ) {
        composable<Route.HomeScreenRoute> {
            HomeScreen(navController)
        }

        composable<Route.MovieDetailsRoute>(typeMap = mapOf(typeOf<DetailsItemData>() to DetailsItemDataParameterType)) { backStackEntry ->
            val movieDetailsRoute = backStackEntry.toRoute<Route.MovieDetailsRoute>()
            MovieDetailsScreen(movieDetailsRoute.movie, backStackEntry)
        }
        composable<Route.OnBoardingRoute> {
            OnBoardingScreen(onNavigateToWelcomeScreen = {
                navController.navigate(Route.WelcomeRoute) {
                    // this will clean the stack up to SlideMovieScreen except for AuthScreen itself
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true // avoid multiple instances of AuthScreen
                }
            })
        }
        composable<Route.WelcomeRoute> {
            WelcomeScreen(onNavigateToSlideMovieScreen = {
                navController.navigate(Route.SlideMovieRoute) {
                    // this will clean the stack up to SlideMovieScreen except for SlideMovieScreen itself
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true // avoid multiple instances of SlideMovieScreen
                }
            })
        }
    }
}