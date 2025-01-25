package es.josevaldes.data.extensions.mappers

import es.josevaldes.data.model.DetailsItemData
import es.josevaldes.data.model.DetailsMovieData
import es.josevaldes.data.model.DetailsTvData
import es.josevaldes.data.model.DiscoverItemData
import es.josevaldes.data.model.DiscoverMovieData
import es.josevaldes.data.model.DiscoverMoviesData
import es.josevaldes.data.model.DiscoverTvData
import es.josevaldes.data.responses.DiscoverItem
import es.josevaldes.data.responses.DiscoverMovie
import es.josevaldes.data.responses.DiscoverResponse
import es.josevaldes.data.responses.DiscoverTV
import es.josevaldes.local.entities.InterestStatus
import es.josevaldes.local.entities.MediaEntityType
import es.josevaldes.local.entities.MediaItemEntity
import es.josevaldes.local.entities.VisitedMediaItemEntity
import es.josevaldes.local.entities.VisitedMediaWithItem

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

fun DiscoverItemData.toVisitedMediaWithItem(interestStatus: es.josevaldes.data.model.InterestStatus): VisitedMediaWithItem {
    val type = when (this is DiscoverMovieData) {
        true -> MediaEntityType.MOVIE
        false -> MediaEntityType.TV
    }
    val mediaEntity = when (type) {
        MediaEntityType.MOVIE -> (this as DiscoverMovieData).toEntity()
        MediaEntityType.TV -> (this as DiscoverTvData).toEntity()
    }

    val visitedMediaItemEntity = VisitedMediaItemEntity(
        mediaId = mediaEntity.id,
        type = type,
        interestStatus = interestStatus.toLocalModel()
    )

    val visitedMediaWithItem = VisitedMediaWithItem(
        visitedMedia = visitedMediaItemEntity,
        mediaItem = mediaEntity
    )
    return visitedMediaWithItem
}

fun DiscoverItemData.toDetailsItemData(): DetailsItemData {
    return when (this) {
        is DiscoverMovieData -> DetailsMovieData(
            baseId = id,
            baseAdult = adult,
            baseBackdropPath = backdropPath,
            baseGenres = emptyList(),
            baseOriginalLanguage = originalLanguage,
            originalTitle = originalTitle,
            baseOverview = overview,
            basePopularity = popularity,
            basePosterPath = posterPath,
            releaseDate = releaseDate,
            title = title,
            video = video,
            baseVoteAverage = voteAverage,
            baseVoteCount = voteCount,
        )

        is DiscoverTvData -> DetailsTvData(
            originalName = originalName,
            baseOverview = overview,
            basePopularity = popularity,
            basePosterPath = posterPath,
            firstAirDate = firstAirDate,
            baseId = id,
            name = name,
            originCountry = originCountry ?: emptyList(),
            baseVoteAverage = voteAverage,
            baseVoteCount = voteCount,
            baseAdult = adult,
            baseBackdropPath = backdropPath,
            baseGenres = emptyList(),
            baseOriginalLanguage = originalLanguage,
        )

        else -> throw IllegalArgumentException("Unknown type")
    }
}


fun DiscoverMovieData.toEntity(): MediaItemEntity {
    return MediaItemEntity(
        id = id,
        type = MediaEntityType.MOVIE,
        originalTitle = originalTitle,
        title = title,
        releaseDate = releaseDate,
        video = video,
        originalName = null,
        name = null,
        firstAirDate = null,
        originCountry = null,
        adult = adult,
        backdropPath = backdropPath,
        genreIds = genreIds.joinToString(","),
        originalLanguage = originalLanguage,
        posterPath = posterPath,
        overview = overview,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity
    )
}

fun es.josevaldes.data.model.InterestStatus.toLocalModel(): InterestStatus {
    return when (this) {
        es.josevaldes.data.model.InterestStatus.INTERESTED -> InterestStatus.INTERESTED
        es.josevaldes.data.model.InterestStatus.SUPER_INTERESTED -> InterestStatus.SUPER_INTERESTED
        es.josevaldes.data.model.InterestStatus.NOT_INTERESTED -> InterestStatus.NOT_INTERESTED
        es.josevaldes.data.model.InterestStatus.WATCHED -> InterestStatus.WATCHED
        es.josevaldes.data.model.InterestStatus.NONE -> InterestStatus.NONE
    }
}

fun InterestStatus.toDataModel(): es.josevaldes.data.model.InterestStatus {
    return when (this) {
        InterestStatus.INTERESTED -> es.josevaldes.data.model.InterestStatus.INTERESTED
        InterestStatus.SUPER_INTERESTED -> es.josevaldes.data.model.InterestStatus.SUPER_INTERESTED
        InterestStatus.NOT_INTERESTED -> es.josevaldes.data.model.InterestStatus.NOT_INTERESTED
        InterestStatus.WATCHED -> es.josevaldes.data.model.InterestStatus.WATCHED
        InterestStatus.NONE -> es.josevaldes.data.model.InterestStatus.NONE
    }
}

fun MediaItemEntity.toDiscoverMovieData(): DiscoverMovieData {
    return DiscoverMovieData(
        id = id,
        originalTitle = originalTitle,
        title = title,
        releaseDate = releaseDate,
        video = video,
        adult = adult,
        backdropPath = backdropPath,
        genreIds = genreIds?.split(",")?.map { it.toInt() } ?: emptyList(),
        originalLanguage = originalLanguage,
        posterPath = posterPath,
        overview = overview,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity
    )
}

fun DiscoverTvData.toEntity(): MediaItemEntity {
    return MediaItemEntity(
        id = id,
        type = MediaEntityType.TV,
        originalTitle = null,
        title = null,
        releaseDate = null,
        video = null,
        originalName = originalName,
        name = name,
        firstAirDate = firstAirDate,
        originCountry = originCountry?.joinToString(","),
        adult = adult,
        backdropPath = backdropPath,
        genreIds = genreIds.joinToString(","),
        originalLanguage = originalLanguage,
        posterPath = posterPath,
        overview = overview,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity
    )
}

fun MediaItemEntity.toDiscoverTvData(): DiscoverTvData {
    return DiscoverTvData(
        id = id,
        originalName = originalName,
        name = name,
        firstAirDate = firstAirDate,
        originCountry = originCountry?.split(",") ?: emptyList(),
        adult = adult,
        backdropPath = backdropPath,
        genreIds = genreIds?.split(",")?.map { it.toInt() } ?: emptyList(),
        originalLanguage = originalLanguage,
        posterPath = posterPath,
        overview = overview,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity
    )
}