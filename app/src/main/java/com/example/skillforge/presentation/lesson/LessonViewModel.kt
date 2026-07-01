package com.example.skillforge.presentation.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.dto.Course
import com.example.skillforge.data.remote.dto.Lesson
import com.example.skillforge.data.repository.CourseRepository
import com.example.skillforge.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LessonState(
    val course: Course,
    val currentLesson: Lesson
)

class LessonViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<LessonState>>(UiState.Loading)
    val uiState: StateFlow<UiState<LessonState>> = _uiState.asStateFlow()

    fun getLessonData(courseId: String, lessonId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.getCourses()
                val course = response.categories.flatMap { it.courses }.find { it.id == courseId }
                val lesson = course?.lessons?.find { it.id == lessonId }
                
                if (course != null && lesson != null) {
                    _uiState.value = UiState.Success(LessonState(course, lesson))
                } else {
                    _uiState.value = UiState.Error("Lesson not found")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
