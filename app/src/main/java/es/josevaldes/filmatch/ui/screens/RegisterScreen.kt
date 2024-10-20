package es.josevaldes.filmatch.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import es.josevaldes.core.utils.validateEmail
import es.josevaldes.core.utils.validatePassword
import es.josevaldes.data.results.AuthResult
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.errors.ErrorMessageWrapper
import es.josevaldes.filmatch.ui.components.EmailTextField
import es.josevaldes.filmatch.ui.components.PasswordTextField
import es.josevaldes.filmatch.ui.dialogs.ErrorDialog
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.ui.theme.getDefaultAccentButtonColors
import es.josevaldes.filmatch.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(onNavigateToSlideMovieScreen: () -> Unit, onGoToLoginClicked: () -> Unit) {
    val authViewModel: AuthViewModel = hiltViewModel()

    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val pass1 = remember { mutableStateOf("") }
    val pass2 = remember { mutableStateOf("") }
    val signInWithGoogle = remember { mutableStateOf(false) }
    val tcAccepted = remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val registerResult = authViewModel.authResult.collectAsState(null)
    val isLoadingStatus = authViewModel.isLoading.collectAsState(false)
    val shouldDisplayErrors = remember { mutableStateOf(false) }


    fun isValidForm(): Boolean {
        return validateEmail(email.value) && validatePassword(pass1.value) && pass1.value == pass2.value && tcAccepted.value
    }


    RegisterScreenContent(
        email,
        isLoadingStatus,
        shouldDisplayErrors,
        pass1,
        pass2,
        tcAccepted,
        isValidForm(),
        {
            signInWithGoogle.value = false
            authViewModel.register(email.value, pass1.value)
        },
        {
            signInWithGoogle.value = true
            authViewModel.signInWithGoogle(context)
        },
        onGoToLoginClicked
    )


    when (val result = registerResult.value) {
        is AuthResult.Success -> {
            if (signInWithGoogle.value) {
                onNavigateToSlideMovieScreen()
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

@Composable
fun RegisterScreenContent(
    email: MutableState<String>,
    isLoadingStatus: State<Boolean>,
    shouldDisplayErrors: MutableState<Boolean>,
    pass1: MutableState<String>,
    pass2: MutableState<String>,
    tcAccepted: MutableState<Boolean>,
    isValidForm: Boolean,
    onRegisterClicked: () -> Unit,
    onLoginWithGoogleClicked: () -> Unit,
    onGoToLoginClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            stringResource(R.string.get_started),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        EmailTextField(
            email,
            isEnabled = !isLoadingStatus.value,
            shouldDisplayErrors = shouldDisplayErrors.value
        )
        PasswordTextField(
            pass1,
            isEnabled = !isLoadingStatus.value,
            shouldDisplayErrors = shouldDisplayErrors.value
        )
        PasswordTextField(
            pass2,
            label = stringResource(R.string.repeat_password),
            imeAction = ImeAction.Done,
            isError = pass1.value != pass2.value,
            supportingText = stringResource(R.string.passwords_don_t_match_error_message),
            isEnabled = !isLoadingStatus.value,
            shouldDisplayErrors = shouldDisplayErrors.value
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    if (!isLoadingStatus.value) tcAccepted.value = !tcAccepted.value
                }
                .padding(top = 5.dp)
        ) {

            ShakingCheckBox(
                modifier = Modifier.padding(end = 10.dp),
                tcAccepted = tcAccepted,
                isLoadingStatus = isLoadingStatus,
                shouldDisplayErrors = shouldDisplayErrors
            )

            TermsAndConditionsText {
                if (!isLoadingStatus.value) tcAccepted.value = !tcAccepted.value
            }
        }

        Button(
            enabled = !isLoadingStatus.value,
            onClick = {
                shouldDisplayErrors.value = true
                if (isValidForm) {
                    onRegisterClicked()
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            colors = getDefaultAccentButtonColors()
        ) {
            Text(stringResource(R.string.register))
            if (isLoadingStatus.value) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .size(20.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
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
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
            )
            Text(
                "Register with",
                modifier = Modifier.padding(horizontal = 10.dp),
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
            Box(
                Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
            )
        }


        Image(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = stringResource(R.string.sign_in_with_google),
            modifier = Modifier
                .clickable {
                    if (!isLoadingStatus.value) {
                        onLoginWithGoogleClicked()
                    }
                }
                .padding(20.dp)
        )

        Row(modifier = Modifier.padding(bottom = 20.dp)) {
            Text(
                text = stringResource(R.string.already_have_an_account_log_in),
            )
            Text(
                text = stringResource(R.string.log_in),
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        if (!isLoadingStatus.value) onGoToLoginClicked()
                    }
                    .padding(start = 10.dp)
            )
        }
    }
}

@Composable
fun ShakingCheckBox(
    isLoadingStatus: State<Boolean>,
    tcAccepted: MutableState<Boolean>,
    modifier: Modifier,
    shouldDisplayErrors: MutableState<Boolean>
) {
    val shakeOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(tcAccepted.value, shouldDisplayErrors.value) {
        if (!tcAccepted.value && shouldDisplayErrors.value) {
            coroutineScope.launch {
                shakeOffset.animateTo(
                    targetValue = 4f,
                    animationSpec = repeatable(
                        iterations = 4,
                        animation = tween(durationMillis = 50, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                shakeOffset.snapTo(0f)
            }
        }
    }

    val shouldWeDisplayColorRed = !tcAccepted.value && shouldDisplayErrors.value
    val colorToDisplay =
        if (shouldWeDisplayColorRed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondary

    Box(
        modifier = modifier
            .size(20.dp)
            .offset {
                IntOffset(
                    x = if (!tcAccepted.value && shouldDisplayErrors.value) shakeOffset.value.dp.roundToPx() else 0,
                    y = 0
                )
            }
    ) {
        Checkbox(
            enabled = !isLoadingStatus.value,
            checked = tcAccepted.value,
            onCheckedChange = { tcAccepted.value = it },
            colors = CheckboxDefaults.colors(
                checkmarkColor = MaterialTheme.colorScheme.onSurface,
                checkedColor = colorToDisplay,
                uncheckedColor = colorToDisplay
            )
        )
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
                color = MaterialTheme.colorScheme.inverseOnSurface
            ),
            start = 0,
            end = length
        )

        addStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.secondary,
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
        containerColor = MaterialTheme.colorScheme.onSurface,
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
    val dummyText = remember { mutableStateOf("hola") }
    val dummyBoolean = remember { mutableStateOf(true) }
    FilmatchTheme(darkTheme = true) {
        RegisterScreenContent(
            onRegisterClicked = {},
            onLoginWithGoogleClicked = {},
            onGoToLoginClicked = {},
            email = dummyText,
            isLoadingStatus = dummyBoolean,
            shouldDisplayErrors = dummyBoolean,
            pass1 = dummyText,
            pass2 = dummyText,
            tcAccepted = dummyBoolean,
            isValidForm = true
        )
    }
}