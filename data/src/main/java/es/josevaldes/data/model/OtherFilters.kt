package es.josevaldes.data.model


enum class ContentType(private val displayName: String) {
    MOVIES("Movies"),
    TV_SHOWS("TV Shows");

    override fun toString(): String {
        return displayName
    }
}

data class Score(
    val score: Float
) {
    override fun toString(): String {
        return score.toString()
    }
}

data class Duration(
    val duration: Int
) {
    override fun toString(): String {
        return duration.toString()
    }
}


class OtherFilters {
    companion object {
        private val _timeFilters = listOf(
            Filter(Duration(95), false),
            Filter(Duration(120), false)
        )
        val timeFilters: List<Filter<Duration>>
            get() = _timeFilters

        private val _scoreFilters = listOf(
            Filter(Score(5f), false),
            Filter(Score(7.5f), false),
        )
        val scoreFilters: List<Filter<Score>>
            get() = _scoreFilters

        private val _contentTypeFilters = listOf(
            Filter(ContentType.MOVIES, true),
            Filter(ContentType.TV_SHOWS, false)
        )
        val contentTypeFilters: List<Filter<ContentType>>
            get() = _contentTypeFilters
    }
}