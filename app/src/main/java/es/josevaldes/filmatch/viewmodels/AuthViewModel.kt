package es.josevaldes.filmatch.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.model.User
import es.josevaldes.data.services.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {


    fun signInWithGoogle(
        context: Context,
        onSuccess: (user: User) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                authService.signInWithGoogle(context, onSuccess, onError)
            }
        }
    }

    fun register(
        email: String,
        pass: String,
        onSuccess: (user: User) -> Unit,
        onError: (String) -> Unit
    ) {
        authService.register(email, pass, onSuccess, onError)
    }

    fun login(
        email: String,
        pass: String,
        onSuccess: (user: User) -> Unit,
        onError: (String) -> Unit
    ) {
        authService.login(email, pass, onSuccess, onError)
    }

    fun callForgotPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        authService.callForgotPassword(email, onSuccess, onError)
    }
}