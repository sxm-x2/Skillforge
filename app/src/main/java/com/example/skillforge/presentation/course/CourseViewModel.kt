package com.example.skillforge.presentation.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.dto.Course
import com.example.skillforge.data.repository.CourseRepository
import com.example.skillforge.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CourseDetailState(
    val course: Course,
    val categoryColor: String
)

class CourseViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<CourseDetailState>>(UiState.Loading)
    val uiState: StateFlow<UiState<CourseDetailState>> = _uiState.asStateFlow()

    fun getCourseById(courseId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.getCourses()
                var foundCourse: Course? = null
                var foundColor: String? = null
                
                for (category in response.categories) {
                    val course = category.courses.find { it.id == courseId }
                    if (course != null) {
                        foundCourse = course
                        foundColor = category.iconColor
                        break
                    }
                }

                if (foundCourse != null && foundColor != null) {
                    _uiState.value = UiState.Success(CourseDetailState(foundCourse, foundColor))
                } else {
                    _uiState.value = UiState.Error("Course not found")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
