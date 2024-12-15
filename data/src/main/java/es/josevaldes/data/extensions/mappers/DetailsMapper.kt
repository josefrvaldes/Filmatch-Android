package es.josevaldes.data.extensions.mappers

import es.josevaldes.data.model.CastMember
import es.josevaldes.data.model.Collection
import es.josevaldes.data.model.Credits
import es.josevaldes.data.model.CrewMember
import es.josevaldes.data.model.Genre
import es.josevaldes.data.model.GenresList
import es.josevaldes.data.model.Movie
import es.josevaldes.data.model.ProductionCompany
import es.josevaldes.data.model.ProductionCountry
import es.josevaldes.data.model.SpokenLanguage
import es.josevaldes.data.model.VideoResult
import es.josevaldes.data.model.Videos
import es.josevaldes.data.responses.CastMemberResponse
import es.josevaldes.data.responses.CollectionResponse
import es.josevaldes.data.responses.CreditsResponse
import es.josevaldes.data.responses.CrewMemberResponse
import es.josevaldes.data.responses.DetailMovieResponse
import es.josevaldes.data.responses.GenreResponse
import es.josevaldes.data.responses.GenresListResponse
import es.josevaldes.data.responses.ProductionCompanyResponse
import es.josevaldes.data.responses.ProductionCountryResponse
import es.josevaldes.data.responses.SpokenLanguageResponse
import es.josevaldes.data.responses.VideoResultResponse
import es.josevaldes.data.responses.VideosResponse

fun DetailMovieResponse.toAppModel(): Movie {
    return Movie(
        id = id,
        adult = adult,
        backdropPath = backdropPath,
        collection = belongsToCollection?.toAppModel(),
        budget = budget,
        genres = genres.map { it.toAppModel() },
        homepage = homepage,
        imdbId = imdbId,
        originCountry = originCountry ?: emptyList(),
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        productionCompanies = productionCompanies?.map { it.toAppModel() } ?: emptyList(),
        productionCountries = productionCountries?.map { it.toAppModel() } ?: emptyList(),
        releaseDate = releaseDate,
        revenue = revenue,
        runtime = runtime,
        spokenLanguages = spokenLanguages?.map { it.toAppModel() } ?: emptyList(),
        status = status,
        tagline = tagline,
        title = title,
        video = video,
        voteAverage = voteAverage,
        voteCount = voteCount,
        credits = credits?.toAppModel(),
        videos = videos?.toAppModel()
    )
}

fun GenreResponse.toAppModel(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

fun CollectionResponse.toAppModel(): Collection {
    return Collection(
        id = id,
        name = name,
        posterPath = posterPath,
        backdropPath = backdropPath
    )
}

fun ProductionCompanyResponse.toAppModel(): ProductionCompany {
    return ProductionCompany(
        id = id,
        logoPath = logoPath,
        name = name,
        originCountry = originCountry
    )
}

fun ProductionCountryResponse.toAppModel(): ProductionCountry {
    return ProductionCountry(
        isoCode = isoCode,
        name = name
    )
}

fun SpokenLanguageResponse.toAppModel(): SpokenLanguage {
    return SpokenLanguage(
        englishName = englishName,
        isoCode = isoCode,
        name = name
    )
}

fun CreditsResponse.toAppModel(): Credits {
    return Credits(
        cast = cast.map { it.toAppModel() },
        crew = crew.map { it.toAppModel() }
    )
}

fun CastMemberResponse.toAppModel(): CastMember {
    return CastMember(
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

fun CrewMemberResponse.toAppModel(): CrewMember {
    return CrewMember(
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

fun VideosResponse.toAppModel(): Videos {
    return Videos(
        results = results.map { it.toAppModel() }
    )
}

// En el módulo data

fun GenresListResponse.toAppModel(): GenresList {
    return GenresList(
        genres = genres.map { it.toAppModel() }
    )
}

fun VideoResultResponse.toAppModel(): VideoResult {
    return VideoResult(
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