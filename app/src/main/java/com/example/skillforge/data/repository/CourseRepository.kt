package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.api.ApiService
import com.example.skillforge.data.remote.dto.ApiResponse

class CourseRepository(private val apiService: ApiService) {
    suspend fun getCourses(): ApiResponse {
        return apiService.getCourses()
    }
}
