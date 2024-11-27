package es.josevaldes.filmatch.model


data class Score(
    val score: Int
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
        val timeFilters = listOf(
            Filter(Duration(95), false),
            Filter(Duration(120), false)
        )

        val scoreFilters = listOf(
            Filter(Score(50), false),
            Filter(Score(75), false),
        )
    }
}