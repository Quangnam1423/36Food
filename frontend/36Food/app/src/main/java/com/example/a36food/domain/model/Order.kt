package com.example.a36food.domain.model


import java.util.Date

enum class ServiceType {

   DRINK,
    ALL,
    FOOD,
    GROCERY,
    ALCOHOL,
    FLOWER,
    SUPERMARKET,
    MEDICINE,
    PET;

    fun toDisplayString(): String {
        return when (this) {
            ALL -> "Tất cả"
            DRINK -> "Đồ uống"
            FOOD -> "Đồ ăn"
            GROCERY -> "Thực phẩm"
            ALCOHOL -> "Rượu bia"
            FLOWER -> "Hoa"
            SUPERMARKET -> "Siêu thị"
            MEDICINE -> "Thuốc"
            PET -> "Thú cưng"
        }
    }
}




data class Order(
    val id: String,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantImageUrl: String,
    val status: String,
    val orderDate: Date?,
    val totalAmount: Double,
    val items: List<OrderItem> = emptyList(),
    val serviceType: String = "Đồ uống", // Default value, should be set from API
    val isReviewed: Boolean = false,
    val deliveryAddress: String = "",
    val note: String? = null
)

data class OrderItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String?,
    val note: String? = null
)