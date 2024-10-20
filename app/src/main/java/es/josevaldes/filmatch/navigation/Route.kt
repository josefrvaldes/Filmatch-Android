package es.josevaldes.filmatch.navigation

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.navigation.NavType
import es.josevaldes.data.model.Movie
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
sealed class Route {
    @Serializable
    data object OnBoardingRoute : Route()

    @Serializable
    data object WelcomeRoute : Route()

    @Serializable
    data object SlideMovieRoute : Route()

    @Serializable
    data class MovieDetailsRoute(val movie: Movie) : Route()
}

val MovieParameterType = object : NavType<Movie>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Movie {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, Movie::class.java) as Movie
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable(key)!! // we use this because we are marking "isNullableAllowed = false"
        }
    }

    override fun parseValue(value: String): Movie {
        return Json.decodeFromString<Movie>(value)
    }

    override fun put(bundle: Bundle, key: String, value: Movie) {
        bundle.putParcelable(key, value)
    }

    override fun serializeAsValue(value: Movie): String {
        return Uri.encode(Json.encodeToString(Movie.serializer(), value))
    }
}