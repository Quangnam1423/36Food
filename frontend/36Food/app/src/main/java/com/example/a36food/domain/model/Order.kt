package com.example.a36food.domain.model


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
    val userId: String,
    val restaurantId: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val totalPrice: Double,
    val address: String,
    val phoneNumber: String,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)