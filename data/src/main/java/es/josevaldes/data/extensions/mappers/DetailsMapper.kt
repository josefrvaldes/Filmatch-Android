package es.josevaldes.data.extensions.mappers

import es.josevaldes.data.model.CastMemberData
import es.josevaldes.data.model.CollectionData
import es.josevaldes.data.model.CreditsData
import es.josevaldes.data.model.CrewMemberData
import es.josevaldes.data.model.DetailsItemData
import es.josevaldes.data.model.DetailsMovieData
import es.josevaldes.data.model.DetailsTvData
import es.josevaldes.data.model.EpisodeData
import es.josevaldes.data.model.GenreData
import es.josevaldes.data.model.NetworkData
import es.josevaldes.data.model.ProductionCompanyData
import es.josevaldes.data.model.ProductionCountryData
import es.josevaldes.data.model.SeasonData
import es.josevaldes.data.model.SpokenLanguageData
import es.josevaldes.data.model.VideoResultData
import es.josevaldes.data.model.VideosData
import es.josevaldes.data.responses.CastMemberResponse
import es.josevaldes.data.responses.CollectionResponse
import es.josevaldes.data.responses.CreditsResponse
import es.josevaldes.data.responses.CrewMemberResponse
import es.josevaldes.data.responses.DetailsItemResponse
import es.josevaldes.data.responses.DetailsMovieResponse
import es.josevaldes.data.responses.DetailsTvResponse
import es.josevaldes.data.responses.EpisodeResponse
import es.josevaldes.data.responses.GenreResponse
import es.josevaldes.data.responses.NetworkResponse
import es.josevaldes.data.responses.ProductionCompanyResponse
import es.josevaldes.data.responses.ProductionCountryResponse
import es.josevaldes.data.responses.SeasonResponse
import es.josevaldes.data.responses.SpokenLanguageResponse
import es.josevaldes.data.responses.VideoResultResponse
import es.josevaldes.data.responses.VideosResponse

fun DetailsItemResponse.toAppModel(): DetailsItemData {
    return when (this) {
        is DetailsMovieResponse -> DetailsMovieData(
            baseId = id,
            baseAdult = adult,
            baseBackdropPath = backdropPath,
            budget = budget,
            baseGenres = genres.map { it.toAppModel() },
            baseHomepage = homepage,
            baseOriginalLanguage = originalLanguage,
            originalTitle = originalTitle,
            baseOverview = overview,
            basePopularity = popularity,
            basePosterPath = posterPath,
            baseProductionCompanies = productionCompanies.map { it.toAppModel() },
            baseProductionCountries = productionCountries.map { it.toAppModel() },
            releaseDate = releaseDate,
            revenue = revenue,
            runtime = runtime,
            baseSpokenLanguages = spokenLanguages.map { it.toAppModel() },
            baseStatus = status,
            baseTagline = tagline,
            title = title,
            video = video,
            baseVoteAverage = voteAverage,
            baseVoteCount = voteCount,
            baseCredits = credits?.toAppModel(),
            baseVideos = videos?.toAppModel(),
            belongsToCollection = belongsToCollection?.toAppModel()
        )

        is DetailsTvResponse -> DetailsTvData(
            baseId = id,
            baseAdult = adult,
            baseBackdropPath = backdropPath,
            baseGenres = genres.map { it.toAppModel() },
            baseHomepage = homepage,
            baseOriginalLanguage = originalLanguage,
            baseOverview = overview,
            basePopularity = popularity,
            basePosterPath = posterPath,
            baseProductionCompanies = productionCompanies.map { it.toAppModel() },
            baseProductionCountries = productionCountries.map { it.toAppModel() },
            baseStatus = status,
            baseTagline = tagline,
            baseVoteAverage = voteAverage,
            baseVoteCount = voteCount,
            baseCredits = credits?.toAppModel(),
            baseVideos = videos?.toAppModel(),
            originalName = originalName,
            name = name,
            firstAirDate = firstAirDate,
            lastAirDate = lastAirDate,
            episodeRunTime = episodeRunTime,
            inProduction = inProduction,
            numberOfEpisodes = numberOfEpisodes,
            numberOfSeasons = numberOfSeasons,
            originCountry = originCountry,
            lastEpisodeToAir = lastEpisodeToAir?.toAppModel(),
            nextEpisodeToAir = nextEpisodeToAir?.toAppModel(),
            seasons = seasons.map { it.toAppModel() },
            networks = networks.map { it.toAppModel() }
        )

        else -> throw IllegalArgumentException("Unknown type")
    }

}

fun SeasonResponse.toAppModel(): SeasonData {
    return SeasonData(
        id = id,
        name = name,
        overview = overview,
        airDate = airDate,
        episodeCount = episodeCount,
        posterPath = posterPath,
        seasonNumber = seasonNumber,
        voteAverage = voteAverage
    )
}

fun NetworkResponse.toAppModel(): NetworkData {
    return NetworkData(
        id = id,
        name = name,
        logoPath = logoPath,
        originCountry = originCountry
    )
}

fun EpisodeResponse.toAppModel(): EpisodeData {
    return EpisodeData(
        id = id,
        name = name,
        overview = overview,
        voteAverage = voteAverage,
        voteCount = voteCount,
        airDate = airDate,
        episodeNumber = episodeNumber,
        episodeType = episodeType,
        productionCode = productionCode,
        runtime = runtime,
        seasonNumber = seasonNumber,
        stillPath = stillPath
    )
}

fun GenreResponse.toAppModel(): GenreData {
    return GenreData(
        id = id,
        name = name
    )
}

fun CollectionResponse.toAppModel(): CollectionData {
    return CollectionData(
        id = id,
        name = name,
        posterPath = posterPath,
        backdropPath = backdropPath
    )
}

fun ProductionCompanyResponse.toAppModel(): ProductionCompanyData {
    return ProductionCompanyData(
        id = id,
        logoPath = logoPath,
        name = name,
        originCountry = originCountry
    )
}

fun ProductionCountryResponse.toAppModel(): ProductionCountryData {
    return ProductionCountryData(
        isoCode = isoCode,
        name = name
    )
}

fun SpokenLanguageResponse.toAppModel(): SpokenLanguageData {
    return SpokenLanguageData(
        englishName = englishName,
        isoCode = isoCode,
        name = name
    )
}

fun CreditsResponse.toAppModel(): CreditsData {
    return CreditsData(
        cast = cast.map { it.toAppModel() },
        crew = crew.map { it.toAppModel() }
    )
}

fun CastMemberResponse.toAppModel(): CastMemberData {
    return CastMemberData(
        adult = adult,
        gender = gender,
        id = id,
        knownForDepartment = knownForDepartment,
        name = name,
        originalName = originalName,
        popularity = popularity,
        profilePath = profilePath,
        castId = castId,
        character = character,
        creditId = creditId,
        order = order
    )
}

fun CrewMemberResponse.toAppModel(): CrewMemberData {
    return CrewMemberData(
        adult = adult,
        gender = gender,
        id = id,
        knownForDepartment = knownForDepartment,
        name = name,
        originalName = originalName,
        popularity = popularity,
        profilePath = profilePath,
        department = department,
        job = job
    )
}

fun VideosResponse.toAppModel(): VideosData {
    return VideosData(
        results = results.map { it.toAppModel() }
    )
}


fun VideoResultResponse.toAppModel(): VideoResultData {
    return VideoResultData(
        iso6391 = iso6391,
        iso31661 = iso31661,
        name = name,
        key = key,
        site = site,
        size = size,
        type = type,
        official = official,
        publishedAt = publishedAt,
        id = id
    )
}