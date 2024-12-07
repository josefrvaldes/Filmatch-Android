package es.josevaldes.data.model

data class Filter<T>(
    val item: T,
    var isSelected: Boolean,
    val imageUrl: String? = null
)