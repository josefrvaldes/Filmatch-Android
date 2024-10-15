package es.josevaldes.filmatch.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import es.josevaldes.core.utils.validateEmail
import es.josevaldes.core.utils.validatePassword
import es.josevaldes.data.results.AuthResult
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.errors.ErrorMessageWrapper
import es.josevaldes.filmatch.navigation.Screen
import es.josevaldes.filmatch.ui.components.EmailTextField
import es.josevaldes.filmatch.ui.components.PasswordTextField
import es.josevaldes.filmatch.ui.dialogs.ErrorDialog
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.viewmodels.AuthViewModel

@Composable
fun RegisterScreen(navController: NavController, onGoToLoginClicked: () -> Unit) {
    val authViewModel: AuthViewModel = hiltViewModel()

    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val pass1 = remember { mutableStateOf("") }
    val pass2 = remember { mutableStateOf("") }
    var signInWithGoogle by remember { mutableStateOf(false) }
    val tcAccepted = remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val registerResult = authViewModel.authResult.collectAsState(null)


    fun isValidForm(): Boolean {
        return validateEmail(email.value) && validatePassword(pass1.value) && pass1.value == pass2.value
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top

    ) {
        Text(
            "Get Started",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 20.dp)
        )
        EmailTextField(email)
        PasswordTextField(pass1)
        PasswordTextField(
            pass2,
            label = stringResource(R.string.repeat_password),
            imeAction = ImeAction.Done,
            isError = pass1.value != pass2.value,
            supportingText = stringResource(R.string.passwords_don_t_match_error_message),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { tcAccepted.value = !tcAccepted.value }) {
            Checkbox(
                checked = tcAccepted.value,
                onCheckedChange = { tcAccepted.value = it },
            )

            TermsAndConditionsText {
                tcAccepted.value = !tcAccepted.value
            }
        }

        Button(
            onClick = {
                if (isValidForm()) {
                    signInWithGoogle = false
                    authViewModel.register(email.value, pass1.value)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Text(stringResource(R.string.register))
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color.Gray)
            )
            Text("Register with", modifier = Modifier.padding(horizontal = 10.dp))
            Box(
                Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color.Gray)
            )
        }


        Image(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = stringResource(R.string.sign_in_with_google),
            modifier = Modifier
                .clickable {
                    signInWithGoogle = true
                    authViewModel.signInWithGoogle(context)
                }
                .padding(20.dp)
        )

        Row(modifier = Modifier.padding(bottom = 20.dp)) {
            Text(
                text = stringResource(R.string.already_have_an_account_log_in),
            )
            Text(
                text = stringResource(R.string.log_in),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        onGoToLoginClicked()
                    }
                    .padding(start = 10.dp)
            )
        }
    }



    when (val result = registerResult.value) {
        is AuthResult.Success -> {
            if (signInWithGoogle) {
                navController.navigate(Screen.SlideMovieScreen.route) {
                    // this will clean the stack up to SlideMovieScreen except for SlideMovieScreen itself
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true // avoid multiple instances of SlideMovieScreen
                }
            } else {
                showSuccessDialog = true
            }
        }

        is AuthResult.Error -> {
            errorMessage =
                ErrorMessageWrapper(LocalContext.current).getErrorMessage(result.authError)
            authViewModel.clearError()
        }

        null -> {} // do nothing
    }


    if (showSuccessDialog) {
        SuccessDialog {
            showSuccessDialog = false
            onGoToLoginClicked()
        }
    } else if (errorMessage.isNotEmpty()) {
        ErrorDialog(errorMessage) { errorMessage = "" }
    }
}

@Suppress("DEPRECATION")
@Composable
fun TermsAndConditionsText(onTermsClick: () -> Unit) {
    val termsAndConditions = stringResource(id = R.string.agree_tc_clickable)
    val agreeTerms = stringResource(id = R.string.agree_tc_title, termsAndConditions)
    val uriHandler = LocalUriHandler.current

    val annotatedText = buildAnnotatedString {
        val termsStartIndex = agreeTerms.indexOf(termsAndConditions)
        append(agreeTerms)

        addStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.onSurface
            ),
            start = 0,
            end = length
        )

        addStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            ),
            start = termsStartIndex,
            end = termsStartIndex + termsAndConditions.length
        )

        addStringAnnotation(
            tag = "terms",
            annotation = "https://example.com/terms",
            start = termsStartIndex,
            end = termsStartIndex + termsAndConditions.length
        )
    }


    ClickableText(
        text = annotatedText,
        style = MaterialTheme.typography.labelLarge,
        onClick = { offset ->
            val annotation =
                annotatedText.getStringAnnotations("terms", offset, offset).firstOrNull()
            annotation?.let {
                uriHandler.openUri(it.item)
            } ?: run {
                onTermsClick()
            }
        }
    )
}


@Composable
private fun SuccessDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        title = { Text(text = stringResource(R.string.user_registered_successfully_dialog_title)) },
        text = { Text(text = stringResource(R.string.user_registered_successfully_dialog_body)) },
        confirmButton = {
            Button(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(
                    text = stringResource(R.string.dismiss)
                )
            }
        }
    )
}


@Preview
@Composable
fun RegisterScreenPreview() {
    FilmatchTheme(darkTheme = true) {
        RegisterScreen(rememberNavController()) {}
    }
}