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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.navigation.Screen
import es.josevaldes.filmatch.ui.dialogs.ErrorDialog
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.viewmodels.AuthViewModel


@Composable
fun AuthScreen(navController: NavController) {
    val viewModel: AuthViewModel = hiltViewModel()
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf("") }

    Scaffold { padding ->
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
                    viewModel.signInWithGoogle(context, { user ->
                        navController.navigate(Screen.SlideMovieScreen.route)
                    }, { error ->
                        errorMessage = error
                    })
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
            ) {
                Text(stringResource(R.string.sign_in_with_google))
            }

            Button(
                onClick = { navController.navigate(Screen.RegisterScreen.route) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
            ) {
                Text(stringResource(R.string.register_with_email_and_password))
            }
            Text(
                text = stringResource(R.string.already_have_an_account_log_in),
                modifier = Modifier.clickable {
                    navController.navigate(Screen.LoginScreen.route)
                })
        }
    }

    if (errorMessage.isNotEmpty()) {
        ErrorDialog(errorMessage) { errorMessage = "" }
    }
}


@Preview
@Composable
fun AuthScreenPreview() {
    FilmatchTheme(darkTheme = true) {
        AuthScreen(rememberNavController())
    }
}