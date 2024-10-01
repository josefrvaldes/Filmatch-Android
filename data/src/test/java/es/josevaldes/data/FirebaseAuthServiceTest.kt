package es.josevaldes.data

import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import es.josevaldes.data.model.User
import es.josevaldes.data.results.AuthError
import es.josevaldes.data.results.AuthResult
import es.josevaldes.data.services.FirebaseAuthService
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.google.firebase.auth.AuthResult as FirebaseAuthResult

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FirebaseAuthServiceTest {

    private val mockFirebaseAuth: FirebaseAuth = mockk()


    private val mockCredential: AuthCredential = mockk()
    private val mockAuthResult: FirebaseAuthResult = mockk()
    private val mockFirebaseUser: FirebaseUser = mockk()

    private lateinit var authService: FirebaseAuthService

    @Before
    fun setUp() {
        authService = FirebaseAuthService(mockFirebaseAuth)
        mockkStatic(GoogleAuthProvider::class)
        every { GoogleAuthProvider.getCredential(any(), any()) } returns mockCredential
    }


    @Test
    fun `login success should return AuthResult Success`() = runBlocking {
        val mockedUser = mockk<FirebaseUser> {
            every { email } returns "user@example.com"
            every { isEmailVerified } returns true
            every { uid } returns "123"
            every { photoUrl } returns Uri.EMPTY
        }
        val mockedSuccessfulAuthResult = mockk<FirebaseAuthResult> {
            every { user } returns mockedUser
        }
        val mockedSuccessfulTask =
            mockk<Task<FirebaseAuthResult>> {
                every { isSuccessful } returns true
                every { result } returns mockedSuccessfulAuthResult
                every { isComplete } returns true
                every { exception } returns null
                every { isCanceled } returns false
            }
        coEvery {
            mockFirebaseAuth.signInWithEmailAndPassword(any(), any())
        } returns mockedSuccessfulTask

        val result = authService.login("user@example.com", "password")

        assertTrue(result is AuthResult.Success)
        assertEquals("user@example.com", (result as AuthResult.Success).data.email)
    }


    @Test
    fun `user with no verified email should return EmailNotVerified error`() = runBlocking {
        val mockedUser = mockk<FirebaseUser> {
            every { email } returns "user@example.com"
            every { isEmailVerified } returns false
            every { uid } returns "123"
            every { photoUrl } returns Uri.EMPTY
        }
        val mockedSuccessfulAuthResult = mockk<FirebaseAuthResult> {
            every { user } returns mockedUser
        }
        val mockedSuccessfulTask =
            mockk<Task<FirebaseAuthResult>> {
                every { isSuccessful } returns true
                every { result } returns mockedSuccessfulAuthResult
                every { isComplete } returns true
                every { exception } returns null
                every { isCanceled } returns false
            }
        coEvery {
            mockFirebaseAuth.signInWithEmailAndPassword(any(), any())
        } returns mockedSuccessfulTask

        coEvery {
            mockFirebaseAuth.signOut()
        } returns Unit

        val result = authService.login("user@example.com", "password")

        assertTrue(result is AuthResult.Error)
        assertEquals((result as AuthResult.Error).authError, AuthError.EmailNotVerified)
    }


    @Test
    fun `no user on login should return UserNotFound Error`() = runBlocking {
        val mockedErrorAuthResult = mockk<FirebaseAuthResult> {
            every { user } returns null
        }
        val mockedErrorTask =
            mockk<Task<FirebaseAuthResult>> {
                every { isSuccessful } returns false
                every { isComplete } returns true
                every { exception } returns null
                every { isCanceled } returns false
                every { result } returns mockedErrorAuthResult
            }
        coEvery {
            mockFirebaseAuth.signInWithEmailAndPassword(any(), any())
        } returns mockedErrorTask

        val result = authService.login("", "")
        assertTrue(result is AuthResult.Error)
        assertTrue((result as AuthResult.Error).authError is AuthError.UserNotFound)
    }

    @Test
    fun `wrong credentials on login should return InvalidCredentials Error`() = runBlocking {
        val mockedException = mockk<FirebaseAuthInvalidCredentialsException> {
            every { errorCode } returns "12"
            every { message } returns "12"
        }
        coEvery {
            mockFirebaseAuth.signInWithEmailAndPassword(any(), any())
        } throws mockedException

        val result = authService.login("", "")
        assertTrue(result is AuthResult.Error)
        assertTrue((result as AuthResult.Error).authError is AuthError.InvalidCredentials)
    }

    @Test
    fun `Invalid user on login should return InvalidUser Error`() = runBlocking {
        val mockedException = mockk<FirebaseAuthInvalidUserException> {
            every { errorCode } returns "12"
            every { message } returns "12"
        }
        coEvery {
            mockFirebaseAuth.signInWithEmailAndPassword(any(), any())
        } throws mockedException

        val result = authService.login("", "")
        assertTrue(result is AuthResult.Error)
        assertTrue((result as AuthResult.Error).authError is AuthError.UserNotFound)
    }

    @Test
    fun `Exception on login should return Unknown Error`() = runBlocking {
        coEvery {
            mockFirebaseAuth.signInWithEmailAndPassword(any(), any())
        } throws Exception()

        val result = authService.login("", "")
        assertTrue(result is AuthResult.Error)
        assertTrue((result as AuthResult.Error).authError is AuthError.Unknown)
    }


    @Test
    fun `signInWithGoogle when getGoogleToken returns Error should return AuthResult Error`() =
        runBlocking {
            val context = mockk<Context>()

            // Espiamos authService para poder mockear métodos privados
            val spyAuthService = spyk(authService)

            // Mockeamos el método privado getGoogleToken
            coEvery { spyAuthService invoke "getGoogleToken" withArguments listOf(context) } returns AuthResult.Error(
                AuthError.InvalidCredentials
            )

            val result = spyAuthService.signInWithGoogle(context)

            assertTrue(result is AuthResult.Error)
            assertEquals(AuthError.InvalidCredentials, (result as AuthResult.Error).authError)
        }

    @Test
    fun `signInWithGoogle when getGoogleToken returns Success should return a user`() =
        runBlocking {
            val context = mockk<Context>()
            val idToken = "valid_token"
            val mockUser = User("123", "username", "email", "")

            // Espiamos authService para poder mockear métodos privados
            val spyAuthService = spyk(authService)

            // Mockeamos el método privado getGoogleToken
            coEvery { spyAuthService invoke "getGoogleToken" withArguments listOf(context) } returns AuthResult.Success(
                idToken
            )

            // Mockeamos el método firebaseAuthWithGoogle para que devuelva éxito
            coEvery { spyAuthService invoke "firebaseAuthWithGoogle" withArguments listOf(idToken) } returns AuthResult.Success(
                mockUser
            )

            val result = spyAuthService.signInWithGoogle(context)

            assertTrue(result is AuthResult.Success)
            assertEquals(mockUser, (result as AuthResult.Success).data)
        }

    @Test
    fun `signInWithGoogle when firebaseAuthWithGoogle returns Error should return AuthResult Error`() =
        runBlocking {
            val context = mockk<Context>()
            val idToken = "valid_token"

            // Espiamos authService para poder mockear métodos privados
            val spyAuthService = spyk(authService)

            // Mockeamos el método privado getGoogleToken
            coEvery { spyAuthService invoke "getGoogleToken" withArguments listOf(context) } returns AuthResult.Success(
                idToken
            )

            // Mockeamos el método firebaseAuthWithGoogle para que devuelva un error
            coEvery { spyAuthService invoke "firebaseAuthWithGoogle" withArguments listOf(idToken) } returns AuthResult.Error(
                AuthError.UserNotFound
            )

            val result = spyAuthService.signInWithGoogle(context)

            assertTrue(result is AuthResult.Error)
            assertEquals(AuthError.UserNotFound, (result as AuthResult.Error).authError)
        }


    @Test
    fun `register when user is created successfully should return AuthResult Success`() =
        runBlocking {
            val mockedUser = mockk<FirebaseUser> {
                every { uid } returns "123"
                every { displayName } returns "whatever on displayName"
                every { email } returns "user@example.com"
                every { photoUrl } returns Uri.EMPTY
            }
            val mockedAuthResult = mockk<FirebaseAuthResult> {
                every { user } returns mockedUser
            }
            val mockedSuccessfulTask = mockk<Task<FirebaseAuthResult>> {
                every { isSuccessful } returns true
                every { result } returns mockedAuthResult
                every { isComplete } returns true
                every { exception } returns null
                every { isCanceled } returns false
            }

            // Simulamos el task de sendEmailVerification()
            val mockedVerificationTask = mockk<Task<Void>> {
                every { isSuccessful } returns true
                every { isComplete } returns true
                every { exception } returns null
            }

            coEvery { mockedUser.sendEmailVerification() } returns mockedVerificationTask
            coEvery {
                mockFirebaseAuth.createUserWithEmailAndPassword(
                    any(),
                    any()
                )
            } returns mockedSuccessfulTask
            coEvery { mockFirebaseAuth.signOut() } just Runs

            val expectedUser = User("123", "", "user@example.com", "")

            val result = authService.register("user@example.com", "password")

            assertTrue(result is AuthResult.Success)
            assertEquals(expectedUser, (result as AuthResult.Success).data)
        }

    @Test
    fun `register when user is null should return AuthResult Error UserNotFound`() = runBlocking {
        val mockedAuthResult = mockk<FirebaseAuthResult> {
            every { user } returns null
        }
        val mockedSuccessfulTask = mockk<Task<FirebaseAuthResult>> {
            every { isSuccessful } returns true
            every { result } returns mockedAuthResult
            every { isComplete } returns true
            every { exception } returns null
            every { isCanceled } returns false
        }

        coEvery {
            mockFirebaseAuth.createUserWithEmailAndPassword(any(), any())
        } returns mockedSuccessfulTask

        val result = authService.register("user@example.com", "password")

        assertTrue(result is AuthResult.Error)
        assertEquals(AuthError.UserNotFound, (result as AuthResult.Error).authError)
    }

    @Test
    fun `register when FirebaseAuthWeakPasswordException is thrown should return AuthResult Error WeakPassword`() =
        runBlocking {
            val mockedException = mockk<FirebaseAuthWeakPasswordException> {
                every { errorCode } returns "12"
                every { message } returns "12"
            }
            coEvery {
                mockFirebaseAuth.createUserWithEmailAndPassword(any(), any())
            } throws mockedException

            val result = authService.register("user@example.com", "weakpassword")

            assertTrue(result is AuthResult.Error)
            assertEquals(AuthError.WeakPassword, (result as AuthResult.Error).authError)
        }

    @Test
    fun `register when FirebaseAuthInvalidCredentialsException is thrown should return AuthResult Error InvalidCredentials`() =
        runBlocking {
            val mockedException = mockk<FirebaseAuthInvalidCredentialsException> {
                every { errorCode } returns "12"
                every { message } returns "12"
            }

            coEvery {
                mockFirebaseAuth.createUserWithEmailAndPassword(any(), any())
            } throws mockedException

            val result = authService.register("user@example.com", "password")

            assertTrue(result is AuthResult.Error)
            assertEquals(AuthError.InvalidCredentials, (result as AuthResult.Error).authError)
        }

    @Test
    fun `register when FirebaseAuthUserCollisionException is thrown should return AuthResult Error UserExists`() =
        runBlocking {
            val mockedException = mockk<FirebaseAuthUserCollisionException> {
                every { errorCode } returns "12"
                every { message } returns "12"
            }

            coEvery {
                mockFirebaseAuth.createUserWithEmailAndPassword(any(), any())
            } throws mockedException

            val result = authService.register("user@example.com", "password")

            assertTrue(result is AuthResult.Error)
            assertEquals(AuthError.UserExists, (result as AuthResult.Error).authError)
        }

    @Test
    fun `register when generic Exception is thrown should return AuthResult Error Unknown`() =
        runBlocking {
            val mockedFailureTask = mockk<Task<FirebaseAuthResult>> {
                every { isSuccessful } returns false
                every { exception } returns Exception("Generic error")
            }

            coEvery {
                mockFirebaseAuth.createUserWithEmailAndPassword(any(), any())
            } returns mockedFailureTask

            val result = authService.register("user@example.com", "password")

            assertTrue(result is AuthResult.Error)
            assertEquals(AuthError.Unknown, (result as AuthResult.Error).authError)
        }


    @Test
    fun `callForgotPassword when successful should return AuthResult Success`() = runBlocking {
        val mockedSuccessfulTask = mockk<Task<Void>> {
            every { isSuccessful } returns true
            every { isComplete } returns true
            every { exception } returns null
            every { isCanceled } returns false
            every { result } returns null
        }

        coEvery {
            mockFirebaseAuth.sendPasswordResetEmail(any())
        } returns mockedSuccessfulTask

        val result = authService.callForgotPassword("user@example.com")

        assertTrue(result is AuthResult.Success)
        assertEquals(Unit, (result as AuthResult.Success).data)
    }

    @Test
    fun `callForgotPassword when FirebaseAuthInvalidUserException is thrown should return AuthResult Error UserNotFound`() = runBlocking {
        val mockedException = mockk<FirebaseAuthInvalidUserException> {
            every { errorCode } returns "ERROR_USER_NOT_FOUND"
            every { message } returns "User not found"
        }

        coEvery {
            mockFirebaseAuth.sendPasswordResetEmail(any())
        } throws mockedException

        val result = authService.callForgotPassword("user@example.com")

        assertTrue(result is AuthResult.Error)
        assertEquals(AuthError.UserNotFound, (result as AuthResult.Error).authError)
    }

    @Test
    fun `callForgotPassword when generic Exception is thrown should return AuthResult Error Unknown`() = runBlocking {
        val mockedException = mockk<Exception> {
            every { message } returns "Generic error"
        }

        coEvery {
            mockFirebaseAuth.sendPasswordResetEmail(any())
        } throws mockedException

        val result = authService.callForgotPassword("user@example.com")

        assertTrue(result is AuthResult.Error)
        assertEquals(AuthError.Unknown, (result as AuthResult.Error).authError)
    }


    @Test
    fun `isLoggedIn should return true when user is logged in and email is verified`() {
        val mockedUser = mockk<FirebaseUser> {
            every { isEmailVerified } returns true
        }

        every { mockFirebaseAuth.currentUser } returns mockedUser

        val result = authService.isLoggedIn()

        assertTrue(result)
    }

    @Test
    fun `isLoggedIn should return false when user is logged in but email is not verified`() {
        val mockedUser = mockk<FirebaseUser> {
            every { isEmailVerified } returns false
        }

        every { mockFirebaseAuth.currentUser } returns mockedUser

        val result = authService.isLoggedIn()

        assertFalse(result)
    }

    @Test
    fun `isLoggedIn should return false when no user is logged in`() {
        every { mockFirebaseAuth.currentUser } returns null

        val result = authService.isLoggedIn()

        assertFalse(result)
    }

}