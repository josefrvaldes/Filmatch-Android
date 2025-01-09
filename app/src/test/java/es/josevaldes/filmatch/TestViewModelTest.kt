package es.josevaldes.filmatch

import es.josevaldes.filmatch.viewmodels.TestViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test


class TestViewModelTest {

    private lateinit var testViewModel: TestViewModel
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        testViewModel = TestViewModel(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `mySuspendFun should return the right list`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        testViewModel.mySuspendFun()
        testViewModel.cleanedMovies.value.forEach {
            assert(it.id % 2 == 0)
        }
        Dispatchers.resetMain()
    }

}