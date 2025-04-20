package com.example.a36food.domain.model

import com.example.a36food.domain.OrderStatus



enum class ServiceType {
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
    val orderId: String,
    val restaurantName: String,
    val restaurantImage: String,
    val orderItems: List<OrderItem>,
    val totalPrice: Double,
    val orderDate: Long,
    val status: OrderStatus,
    val deliveryAddress: String,
    val paymentMethod: String,
    val serviceType: ServiceType,
    val isCompleted: Boolean
)