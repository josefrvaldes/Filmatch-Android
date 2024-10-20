package es.josevaldes.filmatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import es.josevaldes.data.model.Movie
import es.josevaldes.data.services.AuthService
import es.josevaldes.filmatch.navigation.MovieParameterType
import es.josevaldes.filmatch.navigation.Route
import es.josevaldes.filmatch.ui.screens.MovieDetailsScreen
import es.josevaldes.filmatch.ui.screens.OnBoardingScreen
import es.josevaldes.filmatch.ui.screens.SlideMovieScreen
import es.josevaldes.filmatch.ui.screens.WelcomeScreen
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.utils.SimplePreferencesManager
import javax.inject.Inject
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FilmatchApp(
                startDestination = if (isLoggedIn()) {
                    Route.SlideMovieRoute
                } else {
                    if (SimplePreferencesManager(this).isOnboardingFinished()) {
                        Route.WelcomeRoute
                    } else {
                        Route.OnBoardingRoute
                    }
                }
            )
        }
    }

    private fun isLoggedIn(): Boolean {
        return authService.isLoggedIn()
    }
}


@Composable
fun FilmatchApp(startDestination: Route) {
    val navController = rememberNavController()
    FilmatchTheme(darkTheme = true) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable<Route.MovieDetailsRoute>(typeMap = mapOf(typeOf<Movie>() to MovieParameterType)) { backStackEntry ->
                val movieDetailsRoute = backStackEntry.toRoute<Route.MovieDetailsRoute>()
                MovieDetailsScreen(movieDetailsRoute.movie)
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
            composable<Route.SlideMovieRoute> {
                SlideMovieScreen(onNavigateToMovieDetailsScreen = { movie ->
                    navController.navigate(Route.MovieDetailsRoute(movie))
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
}


@Preview
@Composable
fun AppPreview() {
    FilmatchApp(Route.WelcomeRoute)
}