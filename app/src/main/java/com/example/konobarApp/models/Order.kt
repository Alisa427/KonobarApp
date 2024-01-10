package com.example.konobarApp.models

data class Order(var id: Number, var brStola: Number, var status: Boolean, //id je broj stola //True za za take away
                 var sankOrders: ArrayList<OrderItem>,
                 var readyMeals: ArrayList<OrderItem>,
                 var inPreparationMeals: ArrayList<OrderItem>)
