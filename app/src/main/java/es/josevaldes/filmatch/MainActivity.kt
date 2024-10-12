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
import dagger.hilt.android.AndroidEntryPoint
import es.josevaldes.data.services.AuthService
import es.josevaldes.filmatch.navigation.Screen
import es.josevaldes.filmatch.ui.screens.AuthScreen
import es.josevaldes.filmatch.ui.screens.LoginScreen
import es.josevaldes.filmatch.ui.screens.OnBoardingScreen
import es.josevaldes.filmatch.ui.screens.RegisterScreen
import es.josevaldes.filmatch.ui.screens.SlideMovieScreen
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.utils.SimplePreferencesManager
import javax.inject.Inject

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
                    Screen.SlideMovieScreen
                } else {
                    if (SimplePreferencesManager(this).isOnboardingFinished()) {
                        Screen.AuthScreen
                    } else {
                        Screen.OnBoardingScren
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
fun FilmatchApp(startDestination: Screen) {
    val navController = rememberNavController()
    FilmatchTheme(darkTheme = true) {
        NavHost(navController = navController, startDestination = startDestination.route) {
            composable(Screen.LoginScreen.route) {
                LoginScreen(navController)
            }
            composable(Screen.OnBoardingScren.route) {
                OnBoardingScreen(navController)
            }
            composable(Screen.AuthScreen.route) {
                AuthScreen(navController)
            }
            composable(Screen.SlideMovieScreen.route) {
                SlideMovieScreen(navController)
            }
            composable(Screen.RegisterScreen.route) {
                RegisterScreen(navController)
            }
        }
    }
}


@Preview
@Composable
fun AppPreview() {
    FilmatchApp(Screen.AuthScreen)
}