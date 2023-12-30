package com.aydee.foodorderdelivery

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aydee.foodorderdelivery.databinding.ActivityPlaceMyOrderBinding
import com.aydee.foodorderdelivery.fragments.OrderPlacedBottomSheetFragment
import com.aydee.foodorderdelivery.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PlaceMyOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaceMyOrderBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String

    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String

    private lateinit var foodItemName: ArrayList<String>
    private lateinit var foodItemPrice: ArrayList<String>
    private lateinit var foodItemDescription: ArrayList<String>
    private lateinit var foodItemImage: ArrayList<String>
    private lateinit var foodItemIngredient: ArrayList<String>
    private lateinit var foodItemQuantities: ArrayList<Int>

    private lateinit var totalAmount: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceMyOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize firebase and user details
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        // get intent values
        val intent = intent
        foodItemName = intent.getStringArrayListExtra("FoodItemName") as ArrayList<String>
        foodItemPrice = intent.getStringArrayListExtra("FoodItemPrice") as ArrayList<String>
        foodItemDescription =
            intent.getStringArrayListExtra("FoodItemDescription") as ArrayList<String>
        foodItemIngredient =
            intent.getStringArrayListExtra("FoodItemIngredient") as ArrayList<String>
        foodItemImage = intent.getStringArrayListExtra("FoodItemImage") as ArrayList<String>
        foodItemQuantities = intent.getIntegerArrayListExtra("FoodItemQuantities") as ArrayList<Int>

        // set user data
        setUserData()
        totalAmount = "$ " + calculateTotalAmount().toString()
        binding.txtTotalAmount.text = totalAmount

        binding.btnPlaceMyOrder.setOnClickListener {
            // get data from text view
            name = binding.txtName.text.toString().trim()
            address = binding.txtAddress.text.toString().trim()
            phone = binding.txtPhone.text.toString().trim()

            if (name.isBlank() || address.isBlank() || phone.isBlank()) {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
            } else {
                placeOrder()
            }
        }

        binding.btnBackView.setOnClickListener {
            finish()
        }
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid!!
        val time: Long = System.currentTimeMillis()
        val itemPushKey: String? = databaseReference.child("OrderDetails").push().key
        val orderDetails = OrderDetails(
            userId,
            name,
            foodItemName,
            foodItemImage,
            foodItemPrice,
            foodItemQuantities,
            address,
            totalAmount,
            phone,
            false,
            false,
            itemPushKey,
            time
        )
        val orderReference = databaseReference.child("OrderDetails").child(itemPushKey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = OrderPlacedBottomSheetFragment()
            bottomSheetDialog.show(supportFragmentManager, "Order Placed")
            removeItemFromCart()
            addOrderToHistory(orderDetails)
        }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to order", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addOrderToHistory(orderDetails: OrderDetails) {
        databaseReference.child("user").child(userId).child("BuyHistory")
            .child(orderDetails.itemPushKey!!).setValue(orderDetails).addOnSuccessListener {
                Toast.makeText(this, "Order added in history", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add in history", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeItemFromCart() {
        val cartItemReference = databaseReference.child("user/${userId}/CartItems")
        cartItemReference.removeValue()
    }

    private fun setUserData() {
        val user = auth.currentUser
        if (user != null) {
            userId = user.uid
            val userReference = databaseReference.child("user").child(userId)
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        name = snapshot.child("name").getValue(String::class.java) ?: ""
                        address = snapshot.child("address").getValue(String::class.java) ?: ""
                        phone = snapshot.child("phone").getValue(String::class.java) ?: ""
                        binding.apply {
                            txtName.setText(name)
                            txtPhone.setText(phone)
                            txtAddress.setText(address)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0

        for (i in 0 until foodItemPrice.size) {
            val price: String = foodItemPrice[i]

//            val lastChar: Char = price.last()
//            val priceIntValue: Int = if(lastChar == '$'){
//                price.drop(1).toInt()
//            }
//            else{
//                price.toInt()
//            }

            val quantity: Int = foodItemQuantities[i]
            val amount = price.toInt() * quantity
            totalAmount += amount
        }

        return totalAmount
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finish()"))
    override fun onBackPressed() {
        finish()
    }
}