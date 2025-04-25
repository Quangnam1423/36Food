package com.example.a36food.presentation.screens.introduce

data class IntroduceState(
    val isLoading: Boolean = false,
    val shouldNavigateToLogin: Boolean = false,
    val shouldNavigateToHome: Boolean = false,
    val error: String? = null
)