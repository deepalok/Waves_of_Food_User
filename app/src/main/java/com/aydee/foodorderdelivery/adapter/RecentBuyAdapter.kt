package com.aydee.foodorderdelivery.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aydee.foodorderdelivery.databinding.RecentBuyItemBinding
import com.bumptech.glide.Glide

class RecentBuyAdapter(
    private val context: Context,
    private val foodNameList: ArrayList<String>,
    private val foodImageList: ArrayList<String>,
    private val foodPriceList: ArrayList<String>,
    private val foodQuantityList: ArrayList<Int>
) : RecyclerView.Adapter<RecentBuyAdapter.RecentBuyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentBuyViewHolder {
        val binding =
            RecentBuyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentBuyViewHolder(binding)
    }

    override fun getItemCount(): Int = foodNameList.size

    override fun onBindViewHolder(holder: RecentBuyViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class RecentBuyViewHolder(private val binding: RecentBuyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            with(binding) {
                recentBuyName.text = foodNameList[position]
                recentBuyPrice.text = (foodPriceList[position].toInt() * foodQuantityList[position]).toString()
                recentBuyQuantity.text = foodQuantityList[position].toString()
                val uri = Uri.parse(foodImageList[position])
                Glide.with(context).load(uri).into(recentBuyImage)
            }
        }

    }

}