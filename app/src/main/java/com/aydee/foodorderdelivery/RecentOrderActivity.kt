package com.aydee.foodorderdelivery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aydee.foodorderdelivery.adapter.RecentBuyAdapter
import com.aydee.foodorderdelivery.databinding.ActivityRecentOrderBinding
import com.aydee.foodorderdelivery.model.OrderDetails

class RecentOrderActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRecentOrderBinding
    private lateinit var allFoodNames: ArrayList<String>
    private lateinit var allFoodPrices: ArrayList<String>
    private lateinit var allFoodImages: ArrayList<String>
    private lateinit var allFoodQuantities: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recentBuyItems = intent.getSerializableExtra("RecentBuyOrderItem") as ArrayList<OrderDetails>
        recentBuyItems.let { orderDetails ->
            if (orderDetails.isNotEmpty()) {
                val recentOrderItem = orderDetails[0]
                allFoodNames = recentOrderItem.foodNames as ArrayList<String>
                allFoodPrices = recentOrderItem.foodPrices as ArrayList<String>
                allFoodImages = recentOrderItem.foodImages as ArrayList<String>
                allFoodQuantities = recentOrderItem.foodQuantities as ArrayList<Int>
            }
        }
        setAdapter()

        binding.btnBackArrow.setOnClickListener {
            finish()
        }
    }

    private fun setAdapter() {
        val rv = binding.recentBuyRV
        val adapter = RecentBuyAdapter(this, allFoodNames, allFoodImages, allFoodPrices, allFoodQuantities)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }
}