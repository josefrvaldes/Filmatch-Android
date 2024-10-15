package es.josevaldes.filmatch.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.josevaldes.data.model.User
import es.josevaldes.data.results.AuthResult
import es.josevaldes.data.services.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private val _authResult = MutableSharedFlow<AuthResult<User>?>()
    val authResult = _authResult.asSharedFlow()

    private val _forgotPasswordResult = MutableSharedFlow<AuthResult<Unit>?>()
    val forgotPasswordResult = _forgotPasswordResult.asSharedFlow()

    private val _isLoading = MutableSharedFlow<Boolean>()
    val isLoading = _isLoading.asSharedFlow()

    fun signInWithGoogle(
        context: Context
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.emit(true)
                val result = authService.signInWithGoogle(context)
                _isLoading.emit(false)
                _authResult.emit(result)
            }
        }
    }

    fun register(
        email: String,
        pass: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.emit(true)
                val result = authService.register(email, pass)
                _isLoading.emit(false)
                _authResult.emit(result)
            }
        }
    }

    fun login(
        email: String,
        pass: String
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.emit(true)
                val result = authService.login(email, pass)
                _isLoading.emit(false)
                _authResult.emit(result)
            }
        }
    }

    fun callForgotPassword(email: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.emit(true)
                val result = authService.callForgotPassword(email)
                _isLoading.emit(false)
                _forgotPasswordResult.emit(result)
            }
        }
    }

    fun clearError() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _authResult.emit(null)
                _forgotPasswordResult.emit(null)
            }
        }
    }
}