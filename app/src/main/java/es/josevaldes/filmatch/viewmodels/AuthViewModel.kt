package es.josevaldes.filmatch.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.model.User
import es.josevaldes.data.results.AuthResult
import es.josevaldes.data.services.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private val _authResult = MutableStateFlow<AuthResult<User>?>(null)
    val authResult = _authResult.asStateFlow()

    private val _forgotPasswordResult = MutableStateFlow<AuthResult<Unit>?>(null)
    val forgotPasswordResult = _forgotPasswordResult.asStateFlow()

    fun signInWithGoogle(
        context: Context
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = authService.signInWithGoogle(context)
                _authResult.value = result
            }
        }
    }

    fun register(
        email: String,
        pass: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = authService.register(email, pass)
                _authResult.value = result
            }
        }
    }

    fun login(
        email: String,
        pass: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = authService.login(email, pass)
                _authResult.value = result
            }
        }
    }

    fun callForgotPassword(email: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = authService.callForgotPassword(email)
                _forgotPasswordResult.value = result
            }
        }
    }

    fun clearError() {
        _authResult.value = null
        _forgotPasswordResult.value = null
    }
}