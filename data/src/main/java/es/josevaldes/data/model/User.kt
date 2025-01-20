package es.josevaldes.data.model

data class User(
    val id: String,
    val username: String? = null,
    val email: String,
    val photoUrl: String? = null,
    val uid: String
)