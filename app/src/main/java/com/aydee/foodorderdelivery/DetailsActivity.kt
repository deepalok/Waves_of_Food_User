package com.aydee.foodorderdelivery

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aydee.foodorderdelivery.databinding.ActivityDetailsBinding
import com.aydee.foodorderdelivery.model.CartItems
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private lateinit var foodIngredients: String
    private lateinit var foodImage: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize firebase auth
        auth = FirebaseAuth.getInstance()

        foodName = intent.getStringExtra("MenuItemName")!!
        foodPrice = intent.getStringExtra("MenuItemPrice")!!
        foodDescription = intent.getStringExtra("MenuItemDescription")!!
        foodIngredients = intent.getStringExtra("MenuItemIngredients")!!
        foodImage = intent.getStringExtra("MenuItemImage")!!

        binding.apply {
            txtDetailFoodName.text = foodName
            txtDetailFoodDescription.text = foodDescription
            txtDetailFoodIngredients.text = foodIngredients
            Glide.with(this@DetailsActivity).load(Uri.parse(foodImage)).into(imgDetailFoodImage)
        }

        binding.btnBackButton.setOnClickListener {
            finish()
        }

        binding.btnAddToCart.setOnClickListener {
            addItemToCart()
        }
    }

    private fun addItemToCart() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid ?: ""
        // Create cart details
        val cartItem = CartItems(foodName, foodPrice, foodIngredients, foodDescription, foodImage, 1)
        // add cart details to database
        database.child("user").child(userId).child("CartItems").push().setValue(cartItem)
            .addOnCompleteListener {
                Toast.makeText(this, "Item added to Cart", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Item doesn't added to Cart", Toast.LENGTH_SHORT).show()
            }
    }
}