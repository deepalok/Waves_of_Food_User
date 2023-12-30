package com.aydee.foodorderdelivery.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aydee.foodorderdelivery.DetailsActivity
import com.aydee.foodorderdelivery.databinding.MenuItemBinding
import com.aydee.foodorderdelivery.model.MenuItems
import com.bumptech.glide.Glide

class MenuAdapter(
    private val menuItems: MutableList<MenuItems>,
    private val context: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var pos = -1

        init {
            binding.root.setOnClickListener {
                openDetailsActivity(adapterPosition)
            }
        }

        // set data into recycler view items
        fun bind(position: Int) {
            pos = position
            val menuItem = menuItems[position]
            binding.apply {
                menuName.text = menuItem.foodName
                menuPrice.text = menuItem.foodPrice
                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(context).load(uri).into(menuImage)
            }
        }

        // transfers data to DetailsActivity
        private fun openDetailsActivity(position: Int) {
            val menuItem = menuItems[position]
            // item setOnClickListener to open details
            val intent = Intent(context, DetailsActivity::class.java).apply {
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemPrice", menuItem.foodPrice)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemIngredients", menuItem.foodIngredients)
                putExtra("MenuItemImage", menuItem.foodImage)
            }
            Log.d("Details", "Data received")
            context.startActivity(intent)
        }
    }

}