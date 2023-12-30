package com.aydee.foodorderdelivery.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aydee.foodorderdelivery.PlaceMyOrderActivity
import com.aydee.foodorderdelivery.adapter.CartAdapter
import com.aydee.foodorderdelivery.databinding.FragmentCartBinding
import com.aydee.foodorderdelivery.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartItems: MutableList<CartItems>
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String

    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDescriptions: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var foodImagesUri: MutableList<String>
    private lateinit var quantity: MutableList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        //initialize firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // get data from database and display
        retrieveData()
        // proceed to payment
        binding.btnProceed.setOnClickListener {
            // get order details
            getOrderItemsDetail()
        }

        return binding.root
    }

    private fun getOrderItemsDetail() {
        val orderIdReference: DatabaseReference = database.reference.child("user").child(userId).child("CartItems")

        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodIngredient = mutableListOf<String>()
        val foodDescription = mutableListOf<String>()
        val foodImage = mutableListOf<String>()

        // get items quantities
        val foodQuantities: MutableList<Int> = cartAdapter.getUpdatedItemsQuantities()

        orderIdReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){
                    // get the cart items to respective list
                    val orderItems = foodSnapshot.getValue(CartItems::class.java)
                    // add items details in to list
                    orderItems?.foodName?.let { foodName.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                    orderItems?.foodIngredient?.let { foodIngredient.add(it) }
                    orderItems?.foodDescription?.let { foodDescription.add(it) }
                    orderItems?.foodQuantity?.let { foodQuantities.add(it) }
                    orderItems?.foodImage?.let { foodImage.add(it) }
                }
                orderNow(foodName, foodPrice, foodIngredient, foodDescription, foodImage, foodQuantities)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Order Failed. Please try again", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun orderNow(foodName: MutableList<String>,
                         foodPrice: MutableList<String>,
                         foodIngredient: MutableList<String>,
                         foodDescription: MutableList<String>,
                         foodImage: MutableList<String>,
                         foodQuantities: MutableList<Int>) {
        if(isAdded && context != null){
            val intent = Intent(requireContext(), PlaceMyOrderActivity::class.java)
            intent.putExtra("FoodItemName", foodName as ArrayList<String>)
            intent.putExtra("FoodItemPrice", foodPrice as ArrayList<String>)
            intent.putExtra("FoodItemDescription", foodDescription as ArrayList<String>)
            intent.putExtra("FoodItemImage", foodImage as ArrayList<String>)
            intent.putExtra("FoodItemIngredient", foodIngredient as ArrayList<String>)
            intent.putExtra("FoodItemQuantities", foodQuantities as ArrayList<Int>)
            startActivity(intent)
        }
    }

    private fun retrieveData() {
        userId = auth.currentUser?.uid!!
        val foodRef = database.getReference("user/${userId}/CartItems")
        cartItems = mutableListOf()

        foodNames = mutableListOf()
        foodPrices = mutableListOf()
        foodDescriptions = mutableListOf()
        foodIngredients = mutableListOf()
        foodImagesUri = mutableListOf()
        quantity = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItems.clear()
                for (foodSnapshot in snapshot.children) {
                    val cartItem = foodSnapshot.getValue(CartItems::class.java)
                    // add cart item details to the list
                    cartItem?.let {
                        cartItems.add(it)
                    }

                    cartItem?.foodName?.let { foodNames.add(it) }
                    cartItem?.foodPrice?.let { foodPrices.add(it) }
                    cartItem?.foodDescription?.let { foodIngredients.add(it) }
                    cartItem?.foodIngredient?.let { foodDescriptions.add(it) }
                    cartItem?.foodImage?.let { foodImagesUri.add(it) }
                    cartItem?.foodQuantity?.let { quantity.add(it) }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Cart", error.message)
            }
        })
    }

    private fun setAdapter() {
        cartAdapter = CartAdapter(
            requireContext(),
            cartItems,
            foodNames,
            foodPrices,
            foodIngredients,
            foodDescriptions,
            foodImagesUri,
            quantity)
        binding.cartRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.cartRV.adapter = cartAdapter
    }
}