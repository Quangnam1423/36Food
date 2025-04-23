package com.example.a36food.domain.model

data class CartItem(
    override val id: String,
    override val name: String,
    override val price: Double,
    override val quantity: Int,
    override val imageUrl: String,
    override val note: String = ""
) : IOrderable