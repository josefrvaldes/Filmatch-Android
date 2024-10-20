package es.josevaldes.filmatch.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.josevaldes.filmatch.R
import es.josevaldes.filmatch.ui.theme.FilmatchTheme
import es.josevaldes.filmatch.utils.SimplePreferencesManager
import kotlinx.coroutines.launch

@Composable
fun OnBoardingScreen1(screenNumber: Int) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.onboarding_image_1),
            contentDescription = stringResource(R.string.onboarding_screen_1_title),
        )
        Text(stringResource(R.string.onboarding_screen_1_title) + " $screenNumber")
    }
}


@Composable
fun OnBoardingScreen(onNavigateToWelcomeScreen: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
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
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> OnBoardingScreen1(page + 1)
                    1 -> OnBoardingScreen1(page + 1)
                    2 -> OnBoardingScreen1(page + 1)
                }
            }
            Row(
                Modifier
                    .padding(top = 20.dp)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x6F252525))
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.White else Color.Gray
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }
            Row(
                Modifier
                    .padding(20.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, Color.White, RoundedCornerShape(22.dp))
                    .clickable {
                        if (pagerState.currentPage == pagerState.pageCount - 1) {
                            onNavigateToWelcomeScreen()
                            dismissOnBoardingScreenForever(
                                context
                            )
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.pageCount - 1)
                            }
                        }
                    }
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp, bottom = 10.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (pagerState.currentPage == pagerState.pageCount - 1) stringResource(R.string.start) else stringResource(
                        R.string.skip
                    ),
                    color = Color.White,
                    fontSize = 12.sp
                )
                AnimatedVisibility(pagerState.currentPage == pagerState.pageCount - 1) {
                    Image(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(16.dp),
                        painter = painterResource(R.drawable.ic_arrow_forward),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}


private fun dismissOnBoardingScreenForever(
    context: Context
) {
    SimplePreferencesManager(context).setOnboardingFinished()
}


@Preview
@Composable
fun OnBoardingScreenPreview() {
    FilmatchTheme(darkTheme = true) {
        OnBoardingScreen {}
    }
}