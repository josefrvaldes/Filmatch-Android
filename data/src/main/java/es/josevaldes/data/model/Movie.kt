package es.josevaldes.data.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class Movie(
    @JsonProperty("id") val id: Int,
    @JsonProperty("adult") val adult: Boolean? = null,
    @JsonProperty("backdrop_path") val backdropPath: String? = null,
    @JsonProperty("belongs_to_collection") val belongsToCollection: Collection? = null,
    @JsonProperty("budget") val budget: Int? = null,
    @JsonProperty("genres") val genres: List<Genre> = emptyList(),
    @JsonProperty("homepage") val homepage: String? = null,
    @JsonProperty("imdb_id") val imdbId: String? = null,
    @JsonProperty("origin_country") val originCountry: List<String>? = emptyList(),
    @JsonProperty("original_language") val originalLanguage: String? = null,
    @JsonProperty("original_title") val originalTitle: String? = null,
    @JsonProperty("overview") val overview: String? = null,
    @JsonProperty("popularity") val popularity: Double? = null,
    @JsonProperty("poster_path") val posterPath: String? = null,
    @JsonProperty("production_companies") val productionCompanies: List<ProductionCompany>? = emptyList(),
    @JsonProperty("production_countries") val productionCountries: List<ProductionCountry>? = emptyList(),
    @JsonProperty("release_date") val releaseDate: String? = null,
    @JsonProperty("revenue") val revenue: Long? = null,
    @JsonProperty("runtime") val runtime: Int? = null,
    @JsonProperty("spoken_languages") val spokenLanguages: List<SpokenLanguage>? = emptyList(),
    @JsonProperty("status") val status: String? = null,
    @JsonProperty("tagline") val tagline: String? = null,
    @JsonProperty("title") val title: String? = null,
    @JsonProperty("video") val video: Boolean? = null,
    @JsonProperty("vote_average") val voteAverage: Double? = null,
    @JsonProperty("vote_count") val voteCount: Int? = null
) : Parcelable {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w500${posterPath}"

    fun getGenresString(andSeparator: String = "and"): String {
        var categoriesString = ""
        for (i in genres.size - 1 downTo 0) {
            when (i) {
                genres.size - 1 -> {
                    categoriesString += genres[i].name
                }

                genres.size - 2 -> {
                    categoriesString = "${genres[i].name} $andSeparator $categoriesString"
                }

                else -> {
                    categoriesString = "${genres[i].name}, $categoriesString"
                }
            }
        }
        return categoriesString
    }

    fun getDurationString(): String {
        val hours = runtime?.div(60) ?: 0
        val minutes = runtime?.rem(60) ?: 0
        return "${hours}h ${minutes}m"
    }

    fun getReleaseYear(): String? {
        val regex = "\\b(\\d{4})\\b".toRegex()
        val matchResult = regex.find(releaseDate ?: "")
        return matchResult?.value
    }
}


@Serializable
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class Collection(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("poster_path") val posterPath: String? = null,
    @JsonProperty("backdrop_path") val backdropPath: String? = null
) : Parcelable

@Serializable
@Parcelize
data class Genre(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String
) : Parcelable

@Serializable
@Parcelize
data class ProductionCompany(
    @JsonProperty("id") val id: Int,
    @JsonProperty("logo_path") val logoPath: String? = null,
    @JsonProperty("name") val name: String,
    @JsonProperty("origin_country") val originCountry: String
) : Parcelable

@Serializable
@Parcelize
data class ProductionCountry(
    @JsonProperty("iso_3166_1") val isoCode: String,
    @JsonProperty("name") val name: String
) : Parcelable

@Serializable
@Parcelize
data class SpokenLanguage(
    @JsonProperty("english_name") val englishName: String,
    @JsonProperty("iso_639_1") val isoCode: String,
    @JsonProperty("name") val name: String
) : Parcelable