package com.example.konobarApp.models

data class Order(var id: Number, var status: Boolean,
                 var sankOrders: ArrayList<OrderItem>,
                 var readyMeals: ArrayList<OrderItem>,
                 var inPreparationMeals: ArrayList<OrderItem>) //True za za take away
