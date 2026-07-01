package com.example.skillforge.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillforge.data.repository.CourseRepository
import com.example.skillforge.presentation.course.CourseViewModel
import com.example.skillforge.presentation.home.HomeViewModel
import com.example.skillforge.presentation.lesson.LessonViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: CourseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository) as T
            modelClass.isAssignableFrom(CourseViewModel::class.java) -> CourseViewModel(repository) as T
            modelClass.isAssignableFrom(LessonViewModel::class.java) -> LessonViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
