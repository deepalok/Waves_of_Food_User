package com.aydee.foodorderdelivery.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aydee.foodorderdelivery.DetailsActivity
import com.aydee.foodorderdelivery.databinding.PopularItemBinding

class PopularAdapter(
    private val items: List<String>,
    private val images: List<Int>,
    private val prices: List<String>,
    private val context: Context
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    private lateinit var binding: PopularItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        binding = PopularItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PopularViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item = items[position]
        val image = images[position]
        val price = prices[position]
        holder.bind(item, image, price)

        binding.root.setOnClickListener {
            // item setOnClickListener to open details
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("AdapterName", "PopularAdapter")
            intent.putExtra("MenuItemName", item)
            intent.putExtra("MenuItemImage", image)
            context.startActivity(intent)
        }

    }

    class PopularViewHolder(private val binding: PopularItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, image: Int, price: String) {
            binding.popularMenuName.text = item
            binding.popularMenuImage.setImageResource(image)
            binding.popularMenuPrice.text = price
        }
    }


}