package com.example.kitchenApp.models

data class Order(var id: Number, var status: Boolean, var orderItems: ArrayList<OrderItem>) //True za accepted