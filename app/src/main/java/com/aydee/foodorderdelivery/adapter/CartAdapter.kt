package com.aydee.foodorderdelivery.adapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aydee.foodorderdelivery.databinding.CartItemBinding
import com.aydee.foodorderdelivery.model.CartItems
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<CartItems>,
    private val foodNames: MutableList<String>,
    private val foodPrices: MutableList<String>,
    private val foodIngredients: MutableList<String>,
    private val foodDescriptions: MutableList<String>,
    private val foodImagesUri: MutableList<String>,
    private val cartQuantity: MutableList<Int>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var itemQuantity = IntArray(cartItems.size) { 1 }
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var cartItemsReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid?:""
        cartItemsReference = database.getReference("user/${userId}/CartItems")

        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    fun getUpdatedItemsQuantities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
        return itemQuantity
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val cartItem = cartItems[position]
                cartFoodName.text = cartItem.foodName
                cartItemPrice.text = cartItem.foodPrice
                Glide.with(context).load(Uri.parse(cartItem.foodImage)).into(cartItemImage)
                cartItemQuantity.text = itemQuantity[position].toString()

                btnPlus.setOnClickListener {
                    increaseQuantity(position, binding)
                }

                btnMinus.setOnClickListener {
                    decreaseQuantity(position, binding)
                }

                btnDelete.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteItem(itemPosition)
                    }
                }
            }
        }
    }

    private fun increaseQuantity(position: Int, binding: CartItemBinding) {
        if (itemQuantity[position] < 10) {
            itemQuantity[position]++
            cartQuantity[position] = itemQuantity[position]
            binding.cartItemQuantity.text = itemQuantity[position].toString()
        }
    }

    private fun decreaseQuantity(position: Int, binding: CartItemBinding) {
        if (itemQuantity[position] > 1) {
            itemQuantity[position]--
            cartQuantity[position] = itemQuantity[position]
            binding.cartItemQuantity.text = itemQuantity[position].toString()
        }
    }

//    private fun deleteQuantity(position: Int) {
//        cartItems.removeAt(position)
//
//        foodNames.removeAt(position)
//        foodPrices.removeAt(position)
//        foodDescriptions.removeAt(position)
//        foodIngredients.removeAt(position)
//        foodImagesUri.removeAt(position)
//        cartQuantity.removeAt(position)
//
//        notifyItemRemoved(position)
//        notifyItemRangeChanged(position, cartItems.size)
//    }

    private fun deleteItem(position: Int) {
        val positionRetrieve = position
        getUniqueKeyAtPosition(positionRetrieve) { uniqueKey ->
            if (uniqueKey != null) {
                removeItem(position, uniqueKey)
            }
            else{
                Toast.makeText(context, "uniqueKey is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeItem(position: Int, uniqueKey: String) {
        cartItemsReference.child(uniqueKey).removeValue().addOnSuccessListener {
            cartItems.removeAt(position)
            foodNames.removeAt(position)
            foodPrices.removeAt(position)
            foodDescriptions.removeAt(position)
            foodIngredients.removeAt(position)
            foodImagesUri.removeAt(position)
            cartQuantity.removeAt(position)
            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()
            // update itemQuantities
            itemQuantity = itemQuantity.filterIndexed { index, i ->  index != position}.toIntArray()
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cartItems.size)
        }
        .addOnFailureListener {
            Toast.makeText(context, "Item Failed to Delete", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String?) -> Unit) {
        cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var uniqueKey: String? = null
                // loop for snapshot children
                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if(index == positionRetrieve){
                        Toast.makeText(context, "index is $index", Toast.LENGTH_SHORT).show()
                        uniqueKey = dataSnapshot.key
                        return@forEachIndexed
                    }
                }
                onComplete(uniqueKey)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Cart Adapter : uniqueKey failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

}