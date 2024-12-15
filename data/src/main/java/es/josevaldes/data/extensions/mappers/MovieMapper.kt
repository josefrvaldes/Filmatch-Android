package es.josevaldes.data.extensions.mappers

import es.josevaldes.data.model.DiscoverItemData
import es.josevaldes.data.model.Movie


fun DiscoverItemData.toMovie(): Movie {
    return Movie(
        id = id,
        title = displayTitle,
        originalTitle = displayOriginalTitle,
        releaseDate = displayReleaseDate,
        posterPath = posterPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        adult = adult ?: false,
        backdropPath = backdropPath,
        originalLanguage = originalLanguage,
        overview = overview
    )
}