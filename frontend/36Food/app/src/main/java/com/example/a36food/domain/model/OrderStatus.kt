package com.example.a36food.domain.model

enum class OrderStatus {
    PENDING,           // Đơn đang chờ xác nhận từ nhà hàng
    CONFIRMED,         // Nhà hàng đã xác nhận đơn
    PREPARING,         // Nhà hàng đang chuẩn bị món
    READY_TO_PICKUP,   // Sẵn sàng cho shipper đến lấy
    PICKED_UP,         // Shipper đã lấy đơn
    IN_PROGRESS,       // Đang giao hàng
    COMPLETED,         // Đã giao thành công
    CANCELLED,         // Đã hủy
    REFUNDED,          // Đã hoàn tiền (nếu thanh toán trước)
    FAILED             // Giao hàng thất bại (không liên lạc được, địa chỉ sai, etc.)
}