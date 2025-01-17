package es.josevaldes.data.services

import android.content.Context
import es.josevaldes.data.model.User
import es.josevaldes.data.results.AuthResult

interface AuthService {

    suspend fun signInWithGoogle(
        context: Context
    ): AuthResult<User>

    suspend fun register(
        email: String,
        pass: String
    ): AuthResult<User>

    suspend fun login(
        email: String,
        pass: String
    ): AuthResult<User>

    suspend fun callForgotPassword(email: String): AuthResult<Unit>

    suspend fun isLoggedIn(): Boolean
}

