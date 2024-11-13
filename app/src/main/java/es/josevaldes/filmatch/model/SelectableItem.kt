package es.josevaldes.filmatch.model

data class SelectableItem<T>(
    val item: T,
    var isSelected: Boolean
)