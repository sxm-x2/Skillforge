package com.example.skillforge.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data class CourseDetail(val courseId: String) : Screen

    @Serializable
    data class Lesson(val courseId: String, val lessonId: String) : Screen
}
