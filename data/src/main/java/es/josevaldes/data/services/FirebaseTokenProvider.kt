package es.josevaldes.data.services

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseTokenProvider(private val firebaseAuth: FirebaseAuth) : TokenProvider {
    override suspend fun getToken(): String {
        return firebaseAuth.currentUser?.getIdToken(true)?.await()?.token ?: ""
    }
}