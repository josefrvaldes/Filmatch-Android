package es.josevaldes.filmatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import es.josevaldes.data.services.AuthService
import es.josevaldes.filmatch.navigation.MainNavHost
import es.josevaldes.filmatch.navigation.Route
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.utils.SimplePreferencesManager
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            val startDestination = if (authService.isLoggedIn()) {
                Route.HomeScreenRoute
            } else {
                if (SimplePreferencesManager(this@MainActivity).isOnboardingFinished()) {
                    Route.WelcomeRoute
                } else {
                    Route.OnBoardingRoute
                }
            }

            setContent {
                FilmatchApp(
                    startDestination = startDestination
                )
            }
        }
    }
}


@Composable
fun FilmatchApp(startDestination: Route) {
    FilmatchTheme {
        MainNavHost(startDestination = startDestination)
    }
}


@Preview
@Composable
fun AppPreview() {
    FilmatchApp(Route.WelcomeRoute)
}