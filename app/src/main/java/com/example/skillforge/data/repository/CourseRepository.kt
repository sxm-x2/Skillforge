package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.api.ApiService
import com.example.skillforge.data.remote.dto.ApiResponse

interface CourseRepository {
    suspend fun getCourses(): ApiResponse
}

class CourseRepositoryImpl(private val apiService: ApiService) : CourseRepository {
    override suspend fun getCourses(): ApiResponse {
        return apiService.getCourses()
    }
}
