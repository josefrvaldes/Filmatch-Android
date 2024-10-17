package es.josevaldes.filmatch.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.ui.theme.FilmatchTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(navController: NavHostController) {
    val registerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val loginSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showRegisterBottomSheet = remember { mutableStateOf(false) }
    val showLoginBottomSheet = remember { mutableStateOf(false) }
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF7871FF),
                            Color(0xFF4B43E9),
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_filmatch),
                contentDescription = stringResource(R.string.filmatch_logo)
            )
            Text(
                stringResource(R.string.welcome_screen_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                stringResource(R.string.welcome_screen_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            ButtonRooms()

            Box(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 20.dp)
                    .fillMaxWidth()
                    .height(114.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        showRegisterBottomSheet.value = true
                    }
                    .background(color = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.welcome_screen_login_button_title),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        stringResource(R.string.welcome_screen_login_button_description),
                        modifier = Modifier.padding(top = 10.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                Icon(
                    modifier = Modifier
                        .rotate(330f)
                        .align(alignment = Alignment.BottomEnd)
                        .offset(y = 15.dp),
                    painter = painterResource(id = R.drawable.logo_filmatch_monotone),
                    contentDescription = stringResource(R.string.filmatch_logo),
                    tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_heart),
                    contentDescription = null,
                    tint = Color.Red.copy(alpha = 0.4f)
                )

                Icon(
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .offset(x = (-60).dp),
                    painter = painterResource(id = R.drawable.ic_bookmark),
                    contentDescription = null,
                    tint = Color(0xFFEECE00).copy(alpha = 0.3f)
                )
            }
        }
    }


    if (showRegisterBottomSheet.value) {
        RegisterBottomSheetDialog(
            showLoginBottomSheet = showLoginBottomSheet,
            showRegisterBottomSheet = showRegisterBottomSheet,
            navController = navController,
            sheetState = registerSheetState
        )
    } else if (showLoginBottomSheet.value) {
        LoginBottomSheetDialog(
            showLoginBottomSheet = showLoginBottomSheet,
            showRegisterBottomSheet = showRegisterBottomSheet,
            navController = navController,
            sheetState = loginSheetState
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginBottomSheetDialog(
    showRegisterBottomSheet: MutableState<Boolean>,
    showLoginBottomSheet: MutableState<Boolean>,
    navController: NavHostController,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = { showLoginBottomSheet.value = false },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.onSurface
    ) {
        LoginScreen(navController) {
            showLoginBottomSheet.value = false
            showRegisterBottomSheet.value = true
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterBottomSheetDialog(
    showRegisterBottomSheet: MutableState<Boolean>,
    showLoginBottomSheet: MutableState<Boolean>,
    navController: NavHostController,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = { showRegisterBottomSheet.value = false },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.onSurface
    ) {
        RegisterScreen(navController) {
            showRegisterBottomSheet.value = false
            showLoginBottomSheet.value = true
        }
    }
}

@Composable
private fun ButtonRooms() {
    Row(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, top = 30.dp)
            .clip(
                RoundedCornerShape(20.dp)
            )
            .background(Color(0x99000000))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(0.8f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.welcome_screen_btn_room_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                stringResource(R.string.welcome_screen_btn_room_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                stringResource(R.string.welcome_screen_btn_room_description),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            modifier = Modifier.weight(0.2f),
            painter = painterResource(id = R.drawable.logo_filmatch_monotone),
            contentDescription = stringResource(R.string.filmatch_logo),
            tint = Color.White
        )
    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    FilmatchTheme(darkTheme = true) {
        WelcomeScreen(rememberNavController())
    }
}