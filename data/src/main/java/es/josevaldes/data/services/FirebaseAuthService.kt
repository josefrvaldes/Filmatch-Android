package es.josevaldes.data.services

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import es.josevaldes.core.utils.generateNonce
import es.josevaldes.data.BuildConfig
import es.josevaldes.data.extensions.toUser
import es.josevaldes.data.model.User

class FirebaseAuthService : AuthService {

    override suspend fun signInWithGoogle(
        context: Context,
        onSuccess: (user: User) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = getGoogleToken(context)
        if (token != null) {
            firebaseAuthWithGoogle(token, onSuccess, onError)
        } else {
            onError("Error")
        }
    }

    private fun firebaseAuthWithGoogle(
        idToken: String,
        onSuccess: (user: User) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                result.user?.let { user ->
                    onSuccess(user.toUser())
                } ?: run {
                    onError("No user received")
                }
            }
            .addOnFailureListener {
                onError("Error ${it.message}")
            }
    }

    private suspend fun getGoogleToken(context: Context): String? {
        val credentialManager = CredentialManager.create(context)

        val signInRequest = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(BuildConfig.GOOGLE_AUTH_CLIENT_ID)
            .setAutoSelectEnabled(false)
            .setNonce(generateNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInRequest)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            if (result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(result.credential.data)
                googleIdTokenCredential.idToken
            } else {
                null
            }
        } catch (e: GetCredentialException) {
            null
        }
    }

    override fun register(
        email: String,
        pass: String,
        onSuccess: (user: User) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                val currentUser = auth.currentUser
                currentUser?.let {
                    it.sendEmailVerification()
                    onSuccess(it.toUser())
                    auth.signOut()
                } ?: run {
                    onError("Error, no user received")
                }
            }
            .addOnFailureListener {
                onError(it.message ?: "Error")
            }
    }

    override fun login(
        email: String,
        pass: String,
        onSuccess: (user: User) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                val currentUser = auth.currentUser
                currentUser?.let {
                    if (it.isEmailVerified) {
                        onSuccess(it.toUser())
                    } else {
                        auth.signOut()
                        onError("Email not verified")
                    }
                } ?: run {
                    onError("Error, user not received")
                }
            }
            .addOnFailureListener {
                onError(it.message ?: "Error")
            }
    }

    override fun callForgotPassword(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Error")
            }
    }

    override fun isLoggedIn(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return user != null && user.isEmailVerified
    }
}