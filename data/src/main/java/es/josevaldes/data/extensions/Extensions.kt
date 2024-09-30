package es.josevaldes.data.extensions

import com.google.firebase.auth.FirebaseUser
import es.josevaldes.data.model.User

fun FirebaseUser.toUser(): User {
    return User(
        id = this.uid,
        email = this.email ?: "",
        photoUrl = this.photoUrl.toString(),
        username = ""
    )
}