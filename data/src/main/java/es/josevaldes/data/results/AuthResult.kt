package es.josevaldes.data.results

sealed class AuthResult<out T> {
    data class Success<out T>(val data: T) : AuthResult<T>()
    data class Error(val authError: AuthError) : AuthResult<Nothing>()
}

sealed class AuthError {
    data object UserNotFound : AuthError()
    data object InvalidCredentials : AuthError()
    data object WeakPassword : AuthError()
    data object EmailNotVerified : AuthError()
    data object EmailIsNotValid : AuthError()
    data object UserExists : AuthError()
    data object CouldNotFetchToken : AuthError()
    data object CancelledByUser : AuthError()
    data object NoCredentialsAvailable : AuthError()
    data object Interrupted : AuthError()
    data object Unknown : AuthError()
}