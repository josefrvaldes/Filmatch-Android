package es.josevaldes.filmatch.errors

import android.content.Context
import es.josevaldes.data.results.ApiError
import es.josevaldes.data.results.ApiResult
import es.josevaldes.data.results.AuthError
import es.josevaldes.filmatch.R

class ErrorMessageWrapper(val context: Context) {

    fun getErrorMessage(errorResponse: ApiResult.Error): String {
        return when (errorResponse.apiError) {
            ApiError.AccountDisabled -> context.getString(R.string.error_account_disabled)
            ApiError.ApiMaintenance -> context.getString(R.string.error_server_is_under_maintenance)
            ApiError.AuthenticationFailed,
            ApiError.AuthenticationFailedGeneric -> context.getString(R.string.error_authentication_failed)

            ApiError.BackendConnectionFailed -> context.getString(R.string.error_backend_connection_failed)
            ApiError.ConfirmationRequired -> context.getString(R.string.error_confirmation_required)
            ApiError.DeletedSuccessfully -> context.getString(R.string.error_deleted_successfully)
            ApiError.DeviceDenied -> context.getString(R.string.error_device_denied)
            ApiError.DuplicateEntry -> context.getString(R.string.error_duplicate_entry)
            ApiError.EmailNotVerified -> context.getString(R.string.error_email_not_verified)
            ApiError.EntryNotFound -> context.getString(R.string.error_entry_not_found)
            ApiError.Failed -> context.getString(R.string.error_failed)
            ApiError.InternalError -> context.getString(R.string.error_internal_error)
            ApiError.InvalidAcceptHeader -> context.getString(R.string.error_invalid_accept_header)
            ApiError.InvalidApiKey -> context.getString(R.string.error_invalid_api_key)
            ApiError.InvalidCredentials -> context.getString(R.string.error_invalid_credentials)
            ApiError.InvalidDate,
            ApiError.InvalidDateRange -> context.getString(R.string.error_invalid_date)

            ApiError.InvalidFormat -> context.getString(R.string.error_invalid_format)
            ApiError.InvalidId -> context.getString(R.string.error_invalid_id)
            ApiError.InvalidIdPreRequisite -> context.getString(R.string.error_invalid_id_pre_requisite)
            ApiError.InvalidInput -> context.getString(R.string.error_invalid_input)
            ApiError.InvalidPage -> context.getString(R.string.error_invalid_page)
            ApiError.InvalidParameters -> context.getString(R.string.error_invalid_parameters)
            ApiError.InvalidRequestToken -> context.getString(R.string.error_invalid_request_token)
            ApiError.InvalidService -> context.getString(R.string.error_invalid_service)
            ApiError.InvalidTimezone -> context.getString(R.string.error_invalid_timezone)
            ApiError.InvalidToken -> context.getString(R.string.error_invalid_token)
            ApiError.MethodNotSupported -> context.getString(R.string.error_method_not_supported)
            ApiError.MissingCredentials -> context.getString(R.string.error_missing_credentials)
            ApiError.NoPermissionToEdit -> context.getString(R.string.error_no_permission_to_edit)
            ApiError.NoWritePermission -> context.getString(R.string.error_no_write_permission)
            ApiError.NothingToUpdate -> context.getString(R.string.error_nothing_to_update)
            ApiError.RequestLimitExceeded -> context.getString(R.string.error_request_limit_exceeded)
            ApiError.RequestTokenNotApproved -> context.getString(R.string.error_request_token_not_approved)
            ApiError.ResourceIsPrivate -> context.getString(R.string.error_resource_is_private)
            ApiError.ResourceNotFound -> context.getString(R.string.error_resource_not_found)
            ApiError.ServiceOffline -> context.getString(R.string.error_service_offline)
            ApiError.SessionDenied -> context.getString(R.string.error_session_denied)
            ApiError.SessionNotFound -> context.getString(R.string.error_session_not_found)
            ApiError.SuspendedApiKey -> context.getString(R.string.error_suspended_api_key)
            ApiError.Timeout -> context.getString(R.string.error_timeout)
            ApiError.TooManyResponseObjects -> context.getString(R.string.error_too_many_response_objects)
            ApiError.Unknown -> context.getString(R.string.error_unknown_error)
            ApiError.UpdatedSuccessfully -> context.getString(R.string.error_updated_successfully)
            ApiError.UserSuspended -> context.getString(R.string.error_user_suspended)
            ApiError.ValidationFailed -> context.getString(R.string.error_validation_failed)
        }
    }

    fun getErrorMessage(result: AuthError): String {
        return when (result) {
            AuthError.UserNotFound -> context.getString(R.string.error_user_not_found)
            AuthError.InvalidCredentials -> context.getString(R.string.error_invalid_credentials)
            AuthError.WeakPassword -> context.getString(R.string.error_weak_password)
            AuthError.EmailNotVerified -> context.getString(R.string.error_email_not_verified)
            AuthError.UserExists -> context.getString(R.string.error_user_exists)
            AuthError.CouldNotFetchToken -> context.getString(R.string.error_couldn_t_fetch_token)
            AuthError.CancelledByUser -> context.getString(R.string.error_cancelled_by_user)
            AuthError.Unknown -> context.getString(R.string.error_unknown_error)
            AuthError.EmailIsNotValid -> context.getString(R.string.error_email_is_not_valid)
            AuthError.Interrupted -> context.getString(R.string.error_the_operation_was_interrupted)
            AuthError.NoCredentialsAvailable -> context.getString(R.string.error_no_credentials_available)
        }
    }

}