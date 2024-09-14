package es.josevaldes.filmatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import es.josevaldes.filmatch.ui.theme.FilmatchTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FilmatchApp {
                SlideMovieScreen()
            }
        }
    }
}


@Composable
fun FilmatchApp(content: @Composable () -> Unit) {
    FilmatchTheme(darkTheme = true) {
        content()
    }
}


@Preview
@Composable
fun AppPreview() {
    FilmatchApp {
        SlideMovieScreen()
    }
}