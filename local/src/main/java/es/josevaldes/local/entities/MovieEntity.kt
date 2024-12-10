package es.josevaldes.local.entities

import androidx.room.Entity

@Entity(tableName = "movies", primaryKeys = ["id"])
data class Movie(
    val id: Int,
    val adult: Boolean? = null,
    val backdropPath: String? = null,
    val collection: Collection? = null,
    val budget: Int? = null,
    val genres: List<Genre> = emptyList(),
    val homepage: String? = null,
    val imdbId: String? = null,
    val originCountry: List<String> = emptyList(),
    val originalLanguage: String? = null,
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Double? = null,
    val posterPath: String? = null,
    val productionCompanies: List<ProductionCompany> = emptyList(),
    val productionCountries: List<ProductionCountry> = emptyList(),
    val releaseDate: String? = null,
    val revenue: Long? = null,
    val runtime: Int? = null,
    val spokenLanguages: List<SpokenLanguage> = emptyList(),
    val status: String? = null,
    val tagline: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
    val voteAverage: Double? = null,
    val voteCount: Int? = null,
    val credits: Credits? = null,
    val videos: List<Video>? = null
)

@Entity(tableName = "genres", primaryKeys = ["id"])
data class Genre(
    val id: Int,
    val name: String
)


@Entity(tableName = "collections", primaryKeys = ["id"])
data class Collection(
    val id: Int,
    val name: String? = null,
    val posterPath: String? = null,
    val backdropPath: String? = null
)


@Entity(tableName = "production_companies", primaryKeys = ["id"])
data class ProductionCompany(
    val id: Int,
    val logoPath: String? = null,
    val name: String,
    val originCountry: String
)

@Entity(tableName = "production_countries", primaryKeys = ["isoCode"])
data class ProductionCountry(
    val isoCode: String,
    val name: String
)

@Entity(tableName = "spoken_languages", primaryKeys = ["isoCode"])
data class SpokenLanguage(
    val englishName: String,
    val isoCode: String,
    val name: String
)

@Entity(tableName = "credits", primaryKeys = ["id"])
data class Credits(
    val cast: List<CastMember> = emptyList(),
    val crew: List<CrewMember> = emptyList()
)

@Entity(tableName = "cast_members", primaryKeys = ["id"])
data class CastMember(
    val adult: Boolean,
    val gender: Int?,
    val id: Int,
    val knownForDepartment: String?,
    val name: String,
    val originalName: String?,
    val popularity: Float?,
    val profilePath: String?,
    val castId: Int?,
    val character: String?,
    val creditId: String?,
    val order: Int
)

@Entity(tableName = "crew_members", primaryKeys = ["id"])
data class CrewMember(
    val adult: Boolean? = null,
    val gender: Int? = null,
    val id: Int,
    val knownForDepartment: String? = null,
    val name: String? = null,
    val originalName: String? = null,
    val popularity: Float? = null,
    val profilePath: String? = null,
    val department: String? = null,
    val job: String? = null
)


@Entity(tableName = "video_results", primaryKeys = ["id"])
data class Video(
    val iso6391: String? = null,
    val iso31661: String? = null,
    val name: String? = null,
    val key: String? = null,
    val site: String? = null,
    val size: Int? = null,
    private val type: String? = null,
    val official: Boolean? = null,
    val publishedAt: String? = null,
    val id: String? = null
)