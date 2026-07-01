package com.example.skillforge.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val meta: Meta,
    val categories: List<Category>
)

@Serializable
data class Meta(
    val app: String,
    val version: String,
    val generatedAt: String
)
