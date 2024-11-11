package es.josevaldes.filmatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import es.josevaldes.filmatch.ui.theme.FilmatchTheme


@Composable
fun FiltersScreen() {
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Row {
                Text("Hola")
            }
        }

    }
}

@Preview
@Composable
fun FiltersScreenPreview() {
    FilmatchTheme(darkTheme = true) {
        FiltersScreen()
    }
}