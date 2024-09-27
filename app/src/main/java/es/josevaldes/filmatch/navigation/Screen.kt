package es.josevaldes.filmatch.navigation

sealed class Screen(val route: String) {
    data object LoginScreen : Screen("login")
    data object AuthScreen : Screen("auth")
    data object SlideMovieScreen : Screen("slideMovie")
    data object RegisterScreen : Screen("register")
}