package com.example.skillforge.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.dto.ApiResponse
import com.example.skillforge.data.repository.CourseRepository
import com.example.skillforge.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ApiResponse>>(UiState.Loading)
    val uiState: StateFlow<UiState<ApiResponse>> = _uiState.asStateFlow()

    init {
        getCourses()
    }

    fun getCourses() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.getCourses()
                _uiState.value = UiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
