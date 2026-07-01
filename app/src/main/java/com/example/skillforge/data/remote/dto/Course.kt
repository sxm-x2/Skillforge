package com.example.skillforge.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val id: String,
    val title: String,
    val subtitle: String,
    val thumbnailUrl: String,
    val level: String,
    val durationHours: Double,
    val rating: Double,
    val studentsEnrolled: Int,
    val language: String,
    val lastUpdated: String,
    val tags: List<String>,
    val instructor: Instructor,
    val description: String,
    val lessons: List<Lesson>
)

@Serializable
data class Instructor(
    val id: String,
    val name: String,
    val title: String,
    val avatarUrl: String,
    val bio: String
)
