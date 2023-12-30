package com.aydee.foodorderdelivery.fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aydee.foodorderdelivery.RecentOrderActivity
import com.aydee.foodorderdelivery.adapter.BuyAgainAdapter
import com.aydee.foodorderdelivery.databinding.FragmentHistoryBinding
import com.aydee.foodorderdelivery.model.OrderDetails
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String
    private var listOfOrderItem: MutableList<OrderDetails> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // retrieve and display user order history
        retrieveBuyHistory()

        binding.recentBuyItem.setOnClickListener {
            seeRecentBuyItems()
        }

        binding.btnReceived.setOnClickListener {
            val receivedItemPushKey = listOfOrderItem[0].itemPushKey
            val completedOrderReference = database.reference.child("CompletedOrders").child(receivedItemPushKey!!)
            completedOrderReference.child("paymentReceived").setValue(true)

            val userId = listOfOrderItem[0].userId
            val buyHistoryReference = database.reference.child("user").child(userId!!).child("BuyHistory").child(receivedItemPushKey)
            buyHistoryReference.child("paymentReceived").setValue(true)

            binding.btnReceived.visibility = View.GONE
        }

        return binding.root
    }

    private fun seeRecentBuyItems() {
        listOfOrderItem.firstOrNull()?.let {recentBuy ->
            val intent = Intent(requireContext(), RecentOrderActivity::class.java)
            intent.putExtra("RecentBuyOrderItem", listOfOrderItem as Serializable)
            startActivity(intent)
        }
    }

    private fun retrieveBuyHistory() {
        binding.recentBuyItem.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid ?: ""
        val buyItemReference: DatabaseReference =
            database.getReference("user").child(userId).child("BuyHistory")
        val shortingQuery: Query = buyItemReference.orderByChild("currentTime")
        shortingQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (buySnapshot in snapshot.children) {
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItem.add(it)
                    }
                }
                listOfOrderItem.reverse()
                if (listOfOrderItem.isNotEmpty()) {
                    setDataInRecentBuyItem()
                    setPreviousBuyItemsInRecyclerView()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setDataInRecentBuyItem() {
        binding.recentBuyItem.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItem.firstOrNull()
        recentOrderItem?.let {
            with(binding) {
                buyAgainName.text = it.foodNames?.firstOrNull() ?: ""
                buyAgainPrice.text = it.totalPrice
                val image: String = it.foodImages?.firstOrNull() ?: ""
                val uri = Uri.parse(image)
                Glide.with(requireContext()).load(uri).into(buyAgainImage)

                val isOrderAccepted = listOfOrderItem[0].orderAccepted
                val isPaymentReceived = listOfOrderItem[0].paymentReceived
                if(isOrderAccepted){
                    orderStatus.background.setTint(Color.GREEN)
                    if(!isPaymentReceived){
                        btnReceived.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setPreviousBuyItemsInRecyclerView() {
        val buyAgainFoodName = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImages = mutableListOf<String>()

        for (i in 1 until listOfOrderItem.size) {
            listOfOrderItem[i].foodNames?.firstOrNull()?.let { buyAgainFoodName.add(it) }
            listOfOrderItem[i].totalPrice?.replace("$ ", "")?.toIntOrNull()
                ?.let {
                    buyAgainFoodPrice.add("$" + it.toString())
                    }
            listOfOrderItem[i].foodImages?.firstOrNull()?.let { buyAgainFoodImages.add(it) }
        }
        // setUp recycler view adapter -->> BuyAgainAdapter
        val rv = binding.buyAgainRV
        val adapter = BuyAgainAdapter(
            requireContext(),
            buyAgainFoodName,
            buyAgainFoodPrice,
            buyAgainFoodImages
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }
}