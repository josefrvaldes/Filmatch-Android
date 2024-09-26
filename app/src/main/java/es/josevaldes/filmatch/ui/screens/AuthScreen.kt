package es.josevaldes.filmatch.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.josevaldes.filmatch.navigation.Screen
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.viewmodels.AuthViewModel


@Composable
fun AuthScreen(navController: NavController) {
    val viewModel: AuthViewModel = hiltViewModel()
    val context = LocalContext.current
    Scaffold { padding ->
        // Register form
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .fillMaxHeight()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Button(
                onClick = {
                    viewModel.signInWithGoogle(context) {
                        navController.navigate(Screen.SlideMovieScreen.route)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
            ) {
                Text("Enter with google")
            }

            Button(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
            ) {
                Text("Register with username and password")
            }
            Text("Already have an account? Log in", Modifier.clickable {
                navController.navigate(Screen.LoginScreen.route)
            })
        }
    }
}


@Preview
@Composable
fun RegisterScreenPreview() {
    FilmatchTheme(darkTheme = true) {
        AuthScreen(rememberNavController())
    }
}