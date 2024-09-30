package es.josevaldes.data.results

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val apiError: ApiError) : ApiResult<Nothing>()
}

sealed class ApiError(val message: String) {
    data object InvalidService : ApiError("Invalid service: this service does not exist.")
    data object AuthenticationFailed :
        ApiError("Authentication failed: You do not have permissions to access the service.")

    data object InvalidFormat :
        ApiError("Invalid format: This service doesn't exist in that format.")

    data object InvalidParameters :
        ApiError("Invalid parameters: Your request parameters are incorrect.")

    data object InvalidIdPreRequisite :
        ApiError("Invalid id: The pre-requisite id is invalid or not found.")

    data object InvalidApiKey : ApiError("Invalid API key: You must be granted a valid key.")
    data object DuplicateEntry :
        ApiError("Duplicate entry: The data you tried to submit already exists.")

    data object ServiceOffline :
        ApiError("Service offline: This service is temporarily offline, try again later.")

    data object SuspendedApiKey :
        ApiError("Suspended API key: Access to your account has been suspended, contact TMDB.")

    data object InternalError : ApiError("Internal error: Something went wrong, contact TMDB.")
    data object UpdatedSuccessfully : ApiError("The item/record was updated successfully.")
    data object DeletedSuccessfully : ApiError("The item/record was deleted successfully.")
    data object AuthenticationFailedGeneric : ApiError("Authentication failed.")
    data object Failed : ApiError("Failed.")
    data object DeviceDenied : ApiError("Device denied.")
    data object SessionDenied : ApiError("Session denied.")
    data object ValidationFailed : ApiError("Validation failed.")
    data object InvalidAcceptHeader : ApiError("Invalid accept header.")
    data object InvalidDateRange :
        ApiError("Invalid date range: Should be a range no longer than 14 days.")

    data object EntryNotFound :
        ApiError("Entry not found: The item you are trying to edit cannot be found.")

    data object InvalidPage :
        ApiError("Invalid page: Pages start at 1 and max at 500. They are expected to be an integer.")

    data object InvalidDate : ApiError("Invalid date: Format needs to be YYYY-MM-DD.")
    data object Timeout : ApiError("Your request to the backend server timed out. Try again.")
    data object RequestLimitExceeded : ApiError("Your request count is over the allowed limit.")
    data object MissingCredentials : ApiError("You must provide a username and password.")
    data object TooManyResponseObjects :
        ApiError("Too many append to response objects: The maximum number of remote calls is 20.")

    data object InvalidTimezone :
        ApiError("Invalid timezone: Please consult the documentation for a valid timezone.")

    data object ConfirmationRequired :
        ApiError("You must confirm this action: Please provide a confirm=true parameter.")

    data object InvalidCredentials :
        ApiError("Invalid username and/or password: You did not provide a valid login.")

    data object AccountDisabled :
        ApiError("Account disabled: Your account is no longer active. Contact TMDB if this is an error.")

    data object EmailNotVerified : ApiError("Your email address has not been verified.")
    data object InvalidRequestToken :
        ApiError("Invalid request token: The request token is either expired or invalid.")

    data object ResourceNotFound : ApiError("The resource you requested could not be found.")
    data object InvalidToken : ApiError("Invalid token.")
    data object NoWritePermission :
        ApiError("This token hasn't been granted write permission by the user.")

    data object SessionNotFound : ApiError("The requested session could not be found.")
    data object NoPermissionToEdit : ApiError("You don't have permission to edit this resource.")
    data object ResourceIsPrivate : ApiError("This resource is private.")
    data object NothingToUpdate : ApiError("Nothing to update.")
    data object RequestTokenNotApproved :
        ApiError("This request token hasn't been approved by the user.")

    data object MethodNotSupported :
        ApiError("This request method is not supported for this resource.")

    data object BackendConnectionFailed : ApiError("Couldn't connect to the backend server.")
    data object InvalidId : ApiError("The ID is invalid.")
    data object UserSuspended : ApiError("This user has been suspended.")
    data object ApiMaintenance : ApiError("The API is undergoing maintenance. Try again later.")
    data object InvalidInput : ApiError("The input is not valid.")
    data object Unknown : ApiError("An unknown error occurred.")


}

fun mapHttpCodeToApiError(httpCode: Int): ApiError {
    return when (httpCode) {
        201 -> ApiError.UpdatedSuccessfully
        400 -> ApiError.ValidationFailed // Usado como el error genérico de validación en 400
        401 -> ApiError.AuthenticationFailedGeneric // Varias posibles razones para 401
        403 -> ApiError.DuplicateEntry // o SuspendedApiKey o "No permissions" en ciertos contextos
        404 -> ApiError.ResourceNotFound
        405 -> ApiError.MethodNotSupported
        406 -> ApiError.InvalidAcceptHeader
        422 -> ApiError.InvalidParameters // O InvalidDateRange
        429 -> ApiError.RequestLimitExceeded
        500 -> ApiError.InternalError
        502 -> ApiError.BackendConnectionFailed
        503 -> ApiError.ServiceOffline // O ApiMaintenance
        504 -> ApiError.Timeout
        else -> ApiError.Unknown
    }
}

fun mapErrorCodeToApiError(code: Int): ApiError {
    return when (code) {
        2 -> ApiError.InvalidService
        3 -> ApiError.AuthenticationFailed
        4 -> ApiError.InvalidFormat
        5 -> ApiError.InvalidParameters
        6 -> ApiError.InvalidId
        7 -> ApiError.InvalidApiKey
        8 -> ApiError.DuplicateEntry
        9 -> ApiError.ServiceOffline
        10 -> ApiError.SuspendedApiKey
        11 -> ApiError.InternalError
        12 -> ApiError.UpdatedSuccessfully
        13 -> ApiError.DeletedSuccessfully
        14 -> ApiError.AuthenticationFailedGeneric
        15 -> ApiError.Failed
        16 -> ApiError.DeviceDenied
        17 -> ApiError.SessionDenied
        18 -> ApiError.ValidationFailed
        19 -> ApiError.InvalidAcceptHeader
        20 -> ApiError.InvalidDateRange
        21 -> ApiError.EntryNotFound
        22 -> ApiError.InvalidPage
        23 -> ApiError.InvalidDate
        24 -> ApiError.Timeout
        25 -> ApiError.RequestLimitExceeded
        26 -> ApiError.MissingCredentials
        27 -> ApiError.TooManyResponseObjects
        28 -> ApiError.InvalidTimezone
        29 -> ApiError.ConfirmationRequired
        30 -> ApiError.InvalidCredentials
        31 -> ApiError.AccountDisabled
        32 -> ApiError.EmailNotVerified
        33 -> ApiError.InvalidRequestToken
        34 -> ApiError.ResourceNotFound
        35 -> ApiError.InvalidToken
        36 -> ApiError.NoWritePermission
        37 -> ApiError.SessionNotFound
        38 -> ApiError.NoPermissionToEdit
        39 -> ApiError.ResourceIsPrivate
        40 -> ApiError.NothingToUpdate
        41 -> ApiError.RequestTokenNotApproved
        42 -> ApiError.MethodNotSupported
        43 -> ApiError.BackendConnectionFailed
        44 -> ApiError.InvalidId
        45 -> ApiError.UserSuspended
        46 -> ApiError.ApiMaintenance
        47 -> ApiError.InvalidInput
        else -> ApiError.Unknown
    }
}

class ApiErrorException(val apiError: ApiError) : Exception(apiError.message)