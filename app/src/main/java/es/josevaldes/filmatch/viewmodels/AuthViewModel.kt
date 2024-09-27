package es.josevaldes.filmatch.viewmodels

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.core.utils.generateNonce
import es.josevaldes.filmatch.BuildConfig
import es.josevaldes.filmatch.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {


    fun signInWithGoogle(
        context: Context,
        onSuccess: (user: FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val token = getGoogleToken(context)
                if (token != null) {
                    firebaseAuthWithGoogle(context, token, onSuccess, onError)
                } else {
                    onError(context.getString(R.string.error_when_authenticating_with_google))
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(
        context: Context,
        idToken: String,
        onSuccess: (user: FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                result.user?.let { user ->
                    onSuccess(user)
                } ?: run {
                    onError(
                        context.getString(R.string.error_when_authenticating_with_google)
                    )
                }
            }
            .addOnFailureListener {
                onError(
                    context.getString(R.string.error_when_authenticating_with_google)
                )
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

    fun register(
        email: String,
        pass: String,
        onSuccess: (user: FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    currentUser?.let {
                        it.sendEmailVerification()
                        onSuccess(it)
                        return@addOnCompleteListener
                    } ?: run {
                        onError("Error")
                    }
                }
            }
            .addOnFailureListener {
                onError(it.message ?: "Error")
            }
    }
}