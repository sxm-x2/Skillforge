package com.example.skillforge

import com.example.skillforge.data.remote.dto.ApiResponse
import com.example.skillforge.data.remote.dto.Meta
import com.example.skillforge.data.repository.CourseRepository
import com.example.skillforge.presentation.home.HomeViewModel
import com.example.skillforge.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getCourses_success_updatesUiStateToSuccess() = runTest {
        val fakeApiResponse = ApiResponse(
            meta = Meta(app = "", version = "", generatedAt = ""),
            categories = emptyList()
        )
        val fakeRepository = object : CourseRepository {
            override suspend fun getCourses(): ApiResponse = fakeApiResponse
        }
        
        val viewModel = HomeViewModel(fakeRepository)
        
        // HomeViewModel calls getCourses in init. 
        // With StandardTestDispatcher, we need to advance the scheduler.
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value is UiState.Success)
    }
}
