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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    fun signInWithGoogle(context: Context, onSuccess: (user: FirebaseUser) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val token = getGoogleToken(context)
                if (token != null) {
                    firebaseAuthWithGoogle(context, token, onSuccess)
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(
        context: Context,
        idToken: String,
        onSuccess: (user: FirebaseUser) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let { user ->
                        onSuccess(user)
                    } ?: run {
                        _errorMessage.value =
                            context.getString(R.string.error_when_authenticating_with_google)
                    }
                } else {
                    _errorMessage.value =
                        context.getString(R.string.error_when_authenticating_with_google)
                }
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
            val result = credentialManager.getCredential(
                context, request
            )
            if (result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(result.credential.data)
                googleIdTokenCredential.idToken
            } else {
                _errorMessage.value = context.getString(R.string.error_getting_google_credentials)
                null
            }
        } catch (e: GetCredentialException) {
            _errorMessage.value = e.message ?: "Error"
            null
        }
    }
}