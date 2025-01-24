package es.josevaldes.data.model

enum class InterestStatus {
    INTERESTED,
    SUPER_INTERESTED,
    NOT_INTERESTED,
    WATCHED,
    NONE;

    companion object {
        fun fromInt(value: Int): InterestStatus {
            return when (value) {
                0 -> INTERESTED
                1 -> SUPER_INTERESTED
                2 -> NOT_INTERESTED
                3 -> WATCHED
                else -> NONE
            }
        }

        fun toInt(interestStatus: InterestStatus): Int {
            return when (interestStatus) {
                INTERESTED -> 0
                SUPER_INTERESTED -> 1
                NOT_INTERESTED -> 2
                WATCHED -> 3
                NONE -> -1
            }
        }
    }
}