package es.josevaldes.filmatch

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import es.josevaldes.data.model.User
import es.josevaldes.data.results.AuthError
import es.josevaldes.data.results.AuthResult
import es.josevaldes.data.services.FirebaseAuthService
import es.josevaldes.filmatch.viewmodels.AuthViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `call ForgotPassword should set forgotPasswordResult to Success`() = runTest {
        coEvery { mockedAuthService.callForgotPassword(any()) } returns AuthResult.Success(Unit)
        authViewModel.callForgotPassword("user@example.com")
        val result = authViewModel.forgotPasswordResult.first()
        assertEquals(AuthResult.Success(Unit), result)
    }

    @Test
    fun `call ForgotPassword should set forgotPasswordResult to Error Invalid email`() = runTest {
        coEvery { mockedAuthService.callForgotPassword(any()) } returns AuthResult.Error(AuthError.EmailIsNotValid)
        authViewModel.callForgotPassword("user@example.com")
        val result = authViewModel.forgotPasswordResult.first()
        assertEquals(AuthError.EmailIsNotValid, (result as AuthResult.Error).authError)
    }

    @Test
    fun `call ForgotPassword should set forgotPasswordResult to Error Unknown`() = runTest {
        coEvery { mockedAuthService.callForgotPassword(any()) } returns AuthResult.Error(AuthError.Unknown)
        authViewModel.callForgotPassword("user@example.com")
        val result = authViewModel.forgotPasswordResult.first()
        assertEquals(AuthError.Unknown, (result as AuthResult.Error).authError)
    }

    @Test
    fun `call ForgotPassword should set forgotPasswordResult to Error EmailIsNotValid`() = runTest {
        coEvery { mockedAuthService.callForgotPassword(any()) } returns AuthResult.Error(AuthError.EmailIsNotValid)
        authViewModel.callForgotPassword("user@example.com")
        val result = authViewModel.forgotPasswordResult.first()
        assertEquals(AuthError.EmailIsNotValid, (result as AuthResult.Error).authError)
    }

    @Test
    fun `call ForgotPassword should set forgotPasswordResult to Error UserNotFound`() = runTest {
        coEvery { mockedAuthService.callForgotPassword(any()) } returns AuthResult.Error(AuthError.UserNotFound)
        authViewModel.callForgotPassword("user@example.com")
        val result = authViewModel.forgotPasswordResult.first()
        assertEquals(AuthError.UserNotFound, (result as AuthResult.Error).authError)
    }

    @Test
    fun `call register should set authResult to Success and return a valid user`() = runTest {
        val user = User("123", "username", "email@gmail.com", "")
        coEvery { mockedAuthService.register(any(), any()) } returns AuthResult.Success(user)
        authViewModel.register(user.email, "password")
        val result = authViewModel.authResult.first()
        assertTrue(result is AuthResult.Success)
        assertEquals((result as AuthResult.Success).data, user)
    }


    @Test
    fun `register should set authResult to Error Unkown`() = runTest {
        coEvery {
            mockedAuthService.register(
                any(),
                any()
            )
        } returns AuthResult.Error(AuthError.Unknown)
        authViewModel.register("my@email.com", "password")
        val result = authViewModel.authResult.first()
        assertTrue(result is AuthResult.Error)
        assertEquals((result as AuthResult.Error).authError, AuthError.Unknown)
    }


    @Test
    fun `register should set authResult to Error UserExists`() = runTest {
        coEvery {
            mockedAuthService.register(
                any(),
                any()
            )
        } returns AuthResult.Error(AuthError.UserExists)
        authViewModel.register("my@email.com", "password")
        val result = authViewModel.authResult.first()
        assertTrue(result is AuthResult.Error)
        assertEquals((result as AuthResult.Error).authError, AuthError.UserExists)
    }


    @Test
    fun `register should set authResult to Error WeakPassword`() = runTest {
        coEvery {
            mockedAuthService.register(
                any(),
                any()
            )
        } returns AuthResult.Error(AuthError.WeakPassword)
        authViewModel.register("my@email.com", "password")
        val result = authViewModel.authResult.first()
        assertTrue(result is AuthResult.Error)
        assertEquals((result as AuthResult.Error).authError, AuthError.WeakPassword)
    }


    @Test
    fun `register should set authResult to Error InvalidCredentials`() = runTest {
        coEvery {
            mockedAuthService.register(
                any(),
                any()
            )
        } returns AuthResult.Error(AuthError.InvalidCredentials)
        authViewModel.register("my@email.com", "password")
        val result = authViewModel.authResult.first()
        assertTrue(result is AuthResult.Error)
        assertEquals((result as AuthResult.Error).authError, AuthError.InvalidCredentials)
    }

    @Test
    fun `call login should set authResult to Success and return a valid user`() = runTest {
        val user = User("123", "username", "email@gmail.com", "")
        coEvery { mockedAuthService.login(any(), any()) } returns AuthResult.Success(user)
        authViewModel.login(user.email, "password")
        val result = authViewModel.authResult.first()
        assertTrue(result is AuthResult.Success)
        assertEquals((result as AuthResult.Success).data, user)
    }

    @Test
    fun `call login should set authResult to Error and return a UserNotFound error`() = runTest {
        val user = User("123", "username", "email@gmail.com", "")
        coEvery {
            mockedAuthService.login(
                any(),
                any()
            )
        } returns AuthResult.Error(AuthError.UserNotFound)
        authViewModel.login(user.email, "password")
        val result = authViewModel.authResult.first()
        assertTrue(result is AuthResult.Error)
        assertEquals((result as AuthResult.Error).authError, AuthError.UserNotFound)
    }

    @Test
    fun `call login should set authResult to Error and return a InvalidCredentials error`() =
        runTest {
            val user = User("123", "username", "email@gmail.com", "")
            coEvery {
                mockedAuthService.login(
                    any(),
                    any()
                )
            } returns AuthResult.Error(AuthError.InvalidCredentials)
            authViewModel.login(user.email, "password")
            val result = authViewModel.authResult.first()
            assertTrue(result is AuthResult.Error)
            assertEquals((result as AuthResult.Error).authError, AuthError.InvalidCredentials)
        }

    @Test
    fun `call login should set authResult to Error and return a EmailIsNotValid error`() = runTest {
        val user = User("123", "username", "email@gmail.com", "")
        coEvery {
            mockedAuthService.login(
                any(),
                any()
            )
        } returns AuthResult.Error(AuthError.EmailIsNotValid)
        authViewModel.login(user.email, "password")
        val result = authViewModel.authResult.first()
        assertTrue(result is AuthResult.Error)
        assertEquals((result as AuthResult.Error).authError, AuthError.EmailIsNotValid)
    }

    @Test
    fun `call signInWithGoogle should set authResult to Success and return a valid user`() =
        runTest {
            val user = User("123", "username", "email@gmail.com", "")
            val context: Context = mockk()
            coEvery { mockedAuthService.signInWithGoogle(any()) } returns AuthResult.Success(user)
            authViewModel.signInWithGoogle(context)
            val result = authViewModel.authResult.first()
            assertTrue(result is AuthResult.Success)
            assertEquals((result as AuthResult.Success).data, user)
        }

    @Test
    fun `call signInWithGoogle should set authResult to Error and return a InvalidCredentials`() =
        runTest {
            val context: Context = mockk()
            coEvery { mockedAuthService.signInWithGoogle(any()) } returns AuthResult.Error(AuthError.InvalidCredentials)
            authViewModel.signInWithGoogle(context)
            val result = authViewModel.authResult.first()
            assertTrue(result is AuthResult.Error)
            assertEquals((result as AuthResult.Error).authError, AuthError.InvalidCredentials)
        }

    @Test
    fun `calling clearError should set authResult and forgotPasswordResult to null`() = runTest {
        authViewModel.clearError()
        val authResult = authViewModel.authResult.first()
        val forgotPasswordResult = authViewModel.forgotPasswordResult.first()
        assertEquals(null, authResult)
        assertEquals(null, forgotPasswordResult)
    }


}