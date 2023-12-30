package com.aydee.foodorderdelivery.model

data class CartItems(
    val foodName: String? = null,
    val foodPrice: String? = null,
    val foodIngredient: String? = null,
    val foodDescription: String? = null,
    val foodImage: String? = null,
    val foodQuantity: Int? = null
)
