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
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import es.josevaldes.filmatch.navigation.Screen
import es.josevaldes.filmatch.ui.screens.AuthScreen
import es.josevaldes.filmatch.ui.screens.LoginScreen
import es.josevaldes.filmatch.ui.screens.SlideMovieScreen
import es.josevaldes.filmatch.ui.theme.FilmatchTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FilmatchApp(
                startDestination = if (isLoggedIn()) Screen.SlideMovieScreen else Screen.AuthScreen
            )
        }
    }

    private fun isLoggedIn(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return user != null
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
            composable(Screen.AuthScreen.route) {
                AuthScreen(navController)
            }
            composable(Screen.SlideMovieScreen.route) {
                SlideMovieScreen(navController)
            }
        }
    }
}


@Preview
@Composable
fun AppPreview() {
    FilmatchApp(Screen.AuthScreen)
}