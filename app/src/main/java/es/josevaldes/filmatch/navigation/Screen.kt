package es.josevaldes.filmatch.navigation

sealed class Screen(val route: String) {
    data object OnBoardingScren : Screen("onboarding")
    data object WelcomeScren : Screen("welcome")
    data object LoginScreen : Screen("login")
    data object SlideMovieScreen : Screen("slideMovie")
    data object RegisterScreen : Screen("register")
}