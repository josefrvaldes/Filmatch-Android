package es.josevaldes.filmatch.errors

import android.content.Context
import es.josevaldes.data.responses.ApiErrorResponse
import es.josevaldes.data.results.AuthError

class ErrorMessageWrapper(val context: Context) {

    fun getErrorMessage(errorResponse: ApiErrorResponse): String {
        return when (errorResponse.code) {
            400 -> "Bad request"
            401 -> "Unauthorized"
            403 -> "Forbidden"
            404 -> "Not found"
            500 -> "Internal server error"
            else -> "Unknown error"
        }
    }

    fun getErrorMessage(result: AuthError): String {
        return when (result) {
            AuthError.UserNotFound -> "User not found"
            AuthError.InvalidCredentials -> "Invalid credentials"
            AuthError.WeakPassword -> "Weak password"
            AuthError.EmailNotVerified -> "Email not verified"
            AuthError.UserExists -> "User exists"
            AuthError.CouldNotFetchToken -> "Couldn't fetch token"
            AuthError.CancelledByUser -> "Cancelled by user"
            AuthError.Unknown -> "Unknown error"
        }
    }

}