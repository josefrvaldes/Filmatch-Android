package es.josevaldes.filmatch


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import es.josevaldes.data.results.AuthError
import es.josevaldes.data.results.AuthResult
import es.josevaldes.data.services.FirebaseAuthService
import es.josevaldes.filmatch.viewmodels.AuthViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantTakExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private val mockedAuthService: FirebaseAuthService = mockk()
    private val authViewModel = AuthViewModel(mockedAuthService)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `callForgotPassword should set forgotPasswordResult to Success`() = runTest {
        coEvery { mockedAuthService.callForgotPassword(any()) } returns AuthResult.Success(Unit)
        authViewModel.callForgotPassword("user@example.com")
        advanceUntilIdle()
        val result = authViewModel.forgotPasswordResult.first()
        assertEquals(AuthResult.Success(Unit), result)
    }

    @Test
    fun `callForgotPassword should set forgotPasswordResult to Error Invalid email`() = runTest {
        coEvery { mockedAuthService.callForgotPassword(any()) } returns AuthResult.Error(AuthError.EmailIsNotValid)
        authViewModel.callForgotPassword("user@example.com")
        advanceUntilIdle()
        val result = authViewModel.forgotPasswordResult.first()
        assertEquals(AuthError.EmailIsNotValid, (result as AuthResult.Error).authError)
    }

    @Test
    fun `callForgotPassword should set forgotPasswordResult to Error Unknown`() = runTest {
        coEvery { mockedAuthService.callForgotPassword(any()) } returns AuthResult.Error(AuthError.Unknown)
        authViewModel.callForgotPassword("user@example.com")
        advanceUntilIdle()
        val result = authViewModel.forgotPasswordResult.first()
        assertEquals(AuthError.Unknown, (result as AuthResult.Error).authError)
    }

    @Test
    fun `callForgotPassword should set forgotPasswordResult to Error UserNotFound`() = runTest {
        coEvery { mockedAuthService.callForgotPassword(any()) } returns AuthResult.Error(AuthError.UserNotFound)
        authViewModel.callForgotPassword("user@example.com")
        advanceUntilIdle()
        val result = authViewModel.forgotPasswordResult.first()
        assertEquals(AuthError.UserNotFound, (result as AuthResult.Error).authError)
    }

}