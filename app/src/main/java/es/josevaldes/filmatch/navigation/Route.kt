package es.josevaldes.filmatch.navigation

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.navigation.NavType
import es.josevaldes.data.model.DetailsItemData
import es.josevaldes.data.model.DetailsMovieData
import es.josevaldes.data.model.DetailsTvData
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


@Serializable
sealed class Route {
    @Serializable
    data object OnBoardingRoute : Route()

    @Serializable
    data object WelcomeRoute : Route()

    @Serializable
    data object SlideMovieRoute : Route()

    @Serializable
    data class MovieDetailsRoute(val movie: DetailsItemData) : Route()
}


val detailsItemSerializersModule = SerializersModule {
    polymorphic(DetailsItemData::class) {
        subclass(DetailsMovieData::class, DetailsMovieData.serializer())
        subclass(DetailsTvData::class, DetailsTvData.serializer())
    }
}


val json = Json {
    serializersModule = detailsItemSerializersModule
    ignoreUnknownKeys = true
}


val DetailsItemDataParameterType = object : NavType<DetailsItemData>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): DetailsItemData {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, DetailsItemData::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable(key)!!
        }
    }

    override fun parseValue(value: String): DetailsItemData {
        return json.decodeFromString(PolymorphicSerializer(DetailsItemData::class), value)
    }

    override fun put(bundle: Bundle, key: String, value: DetailsItemData) {
        bundle.putParcelable(key, value)
    }

    override fun serializeAsValue(value: DetailsItemData): String {
        return Uri.encode(json.encodeToString(PolymorphicSerializer(DetailsItemData::class), value))
    }
}