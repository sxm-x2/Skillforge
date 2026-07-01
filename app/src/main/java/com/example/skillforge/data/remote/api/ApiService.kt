package com.example.skillforge.data.remote.api

import com.example.skillforge.data.remote.dto.ApiResponse
import retrofit2.http.GET

interface ApiService {
    @GET("android-assesment/notes/refs/heads/main/data.json")
    suspend fun getCourses(): ApiResponse
}
