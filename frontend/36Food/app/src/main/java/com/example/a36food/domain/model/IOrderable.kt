package com.example.a36food.domain.model

interface IOrderable {
    val id: String
    val name: String
    val price: Double
    val quantity: Int
    val imageUrl: String
    val note: String
}