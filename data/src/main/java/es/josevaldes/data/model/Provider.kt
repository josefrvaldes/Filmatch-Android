package es.josevaldes.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Provider(
    val id: Int,
    val name: String,
    val logoPath: String?,
    val displayPriority: Int,
    val displayPriorities: Map<String, Int>
) : Parcelable {
    val logoUrl: String
        get() = logoPath?.let { "https://image.tmdb.org/t/p/original$it" } ?: ""

    override fun toString(): String {
        return name
    }
}

@Parcelize
data class ProvidersList(
    val results: List<Provider>
) : Parcelable