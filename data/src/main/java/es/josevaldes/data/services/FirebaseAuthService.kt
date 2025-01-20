package es.josevaldes.data.services

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import es.josevaldes.core.utils.generateNonce
import es.josevaldes.data.BuildConfig
import es.josevaldes.data.extensions.toUser
import es.josevaldes.data.model.User
import es.josevaldes.data.repositories.AuthRepository
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.results.AuthError
import es.josevaldes.data.results.AuthResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseAuthService(
    private val auth: FirebaseAuth,
    private val authRepository: AuthRepository
) : AuthService {


    private suspend fun handleFirebaseAndApi(
        firebaseAction: suspend () -> AuthResult<User>
    ): AuthResult<User> {
        return when (val firebaseResult = firebaseAction()) {
            is AuthResult.Success -> {
                when (authRepository.auth().first()) {
                    is ApiResult.Success -> firebaseResult
                    is ApiResult.Error -> AuthResult.Error(AuthError.InvalidCredentials)
                }
            }

            is AuthResult.Error -> firebaseResult
        }
    }

    override suspend fun signInWithGoogle(
        context: Context
    ): AuthResult<User> {
        return handleFirebaseAndApi {
            when (val result = getGoogleToken(context)) {
                is AuthResult.Error -> result
                is AuthResult.Success -> firebaseAuthWithGoogle(result.data)
            }
        }
    }

    private suspend fun firebaseAuthWithGoogle(
        idToken: String
    ): AuthResult<User> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            val result = auth.signInWithCredential(credential).await()
            result.user?.let {
                AuthResult.Success(it.toUser())
            } ?: run {
                AuthResult.Error(AuthError.UserNotFound)
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            AuthResult.Error(AuthError.UserNotFound)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            AuthResult.Error(AuthError.InvalidCredentials)
        } catch (e: FirebaseAuthUserCollisionException) {
            AuthResult.Error(AuthError.UserExists)
        } catch (e: Exception) {
            AuthResult.Error(AuthError.Unknown)
        }
    }

    private suspend fun getGoogleToken(context: Context): AuthResult<String> {
        val credentialManager = CredentialManager.create(context)

        val signInRequest = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
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
                AuthResult.Success(googleIdTokenCredential.idToken)
            } else {
                AuthResult.Error(AuthError.CouldNotFetchToken)
            }
        } catch (e: GetCredentialException) {
            return when (e.type) {
                android.credentials.GetCredentialException.TYPE_USER_CANCELED -> AuthResult.Error(
                    AuthError.CancelledByUser
                )

                android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL -> AuthResult.Error(
                    AuthError.NoCredentialsAvailable
                )

                android.credentials.GetCredentialException.TYPE_INTERRUPTED -> AuthResult.Error(
                    AuthError.Interrupted
                )

                else -> AuthResult.Error(AuthError.Unknown)
            }
        } catch (e: Exception) {
            AuthResult.Error(AuthError.Unknown)
        }
    }

    override suspend fun register(
        email: String,
        pass: String
    ): AuthResult<User> {
        return handleFirebaseAndApi {
            try {
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                result.user?.let {
                    it.sendEmailVerification()
                    auth.signOut()
                    AuthResult.Success(it.toUser())
                } ?: run {
                    AuthResult.Error(AuthError.UserNotFound)
                }
            } catch (e: FirebaseAuthWeakPasswordException) {
                AuthResult.Error(AuthError.WeakPassword)
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                AuthResult.Error(AuthError.InvalidCredentials)
            } catch (e: FirebaseAuthUserCollisionException) {
                AuthResult.Error(AuthError.UserExists)
            } catch (e: Exception) {
                Timber.tag("AuthService").d("register: ${e.message}")
                AuthResult.Error(AuthError.Unknown)
            }
        }
    }

    override suspend fun login(
        email: String,
        pass: String
    ): AuthResult<User> {
        return handleFirebaseAndApi {
            try {
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                result.user?.let { currentUser ->
                    if (currentUser.isEmailVerified) {
                        AuthResult.Success(currentUser.toUser())
                    } else {
                        auth.signOut()
                        AuthResult.Error(AuthError.EmailNotVerified)
                    }
                } ?: run {
                    AuthResult.Error(AuthError.UserNotFound)
                }
            } catch (e: FirebaseAuthInvalidUserException) {
                AuthResult.Error(AuthError.UserNotFound)
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                AuthResult.Error(AuthError.InvalidCredentials)
            } catch (e: Exception) {
                Timber.tag("AuthService").d("login: ${e.message}")
                AuthResult.Error(AuthError.Unknown)
            }
        }
    }

    override suspend fun callForgotPassword(
        email: String,
    ): AuthResult<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            AuthResult.Error(AuthError.UserNotFound)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            AuthResult.Error(AuthError.EmailIsNotValid)
        } catch (e: Exception) {
            Timber.tag("AuthService").d("callForgotPassword: ${e.message}")
            AuthResult.Error(AuthError.Unknown)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        val user = auth.currentUser

        val isLoggedInInFirebaseAuth = user != null && user.isEmailVerified
        if (!isLoggedInInFirebaseAuth) return false


        val tokenResult = user?.getIdToken(false)?.await()
        val token = tokenResult?.token
        val isLoggedIn = token?.isNotEmpty() == true

        val authResult = authRepository.auth().first()
        if (authResult is ApiResult.Error) return false

        Timber.tag("AuthService").d("auth token: $token")
        return isLoggedIn
    }
}