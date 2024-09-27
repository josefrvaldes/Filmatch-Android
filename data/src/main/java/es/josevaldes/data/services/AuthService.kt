package es.josevaldes.data.services

import android.content.Context
import es.josevaldes.data.model.User

interface AuthService {

    suspend fun signInWithGoogle(
        context: Context,
        onSuccess: (user: User) -> Unit,
        onError: (String) -> Unit
    )

    fun register(
        email: String,
        pass: String,
        onSuccess: (user: User) -> Unit,
        onError: (String) -> Unit
    )

    fun login(
        email: String,
        pass: String,
        onSuccess: (user: User) -> Unit,
        onError: (String) -> Unit
    )

    fun callForgotPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit)

    fun isLoggedIn(): Boolean
}

