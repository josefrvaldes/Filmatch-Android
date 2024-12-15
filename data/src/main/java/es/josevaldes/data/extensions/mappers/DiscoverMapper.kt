package es.josevaldes.data.extensions.mappers

import es.josevaldes.data.model.DiscoverItemData
import es.josevaldes.data.model.DiscoverMovieData
import es.josevaldes.data.model.DiscoverMoviesData
import es.josevaldes.data.model.DiscoverTvData
import es.josevaldes.data.responses.DiscoverItem
import es.josevaldes.data.responses.DiscoverMovie
import es.josevaldes.data.responses.DiscoverResponse
import es.josevaldes.data.responses.DiscoverTV

fun DiscoverResponse.toAppModel(): DiscoverMoviesData {
    return DiscoverMoviesData(
        results = results.map { it.toAppModel() },
        page = page,
        totalResults = totalResults,
        totalPages = totalPages
    )
}

fun DiscoverItem.toAppModel(): DiscoverItemData {
    return when (this) {
        is DiscoverMovie -> DiscoverMovieData(
            originalTitle = originalTitle,
            title = title,
            releaseDate = releaseDate,
            video = video,
            // Campos del padre
            id = this.id,
            adult = this.adult,
            backdropPath = this.backdropPath,
            genreIds = this.genreIds,
            originalLanguage = this.originalLanguage,
            posterPath = this.posterPath,
            overview = this.overview,
            voteAverage = this.voteAverage,
            voteCount = this.voteCount,
            popularity = this.popularity
        )

        is DiscoverTV -> DiscoverTvData(
            originalName = originalName,
            name = name,
            firstAirDate = firstAirDate,
            originCountry = originCountry,
            // Campos del padre
            id = this.id,
            adult = this.adult,
            backdropPath = this.backdropPath,
            genreIds = this.genreIds,
            originalLanguage = this.originalLanguage,
            posterPath = this.posterPath,
            overview = this.overview,
            voteAverage = this.voteAverage,
            voteCount = this.voteCount,
            popularity = this.popularity
        )

        else -> throw IllegalArgumentException("Unknown type")
    }
}