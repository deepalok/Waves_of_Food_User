package com.aydee.foodorderdelivery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aydee.foodorderdelivery.databinding.NotificationItemBinding

class NotificationAdapter(
    private val notificationText: ArrayList<String>,
    private val notificationImage: ArrayList<Int>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun getItemCount(): Int = notificationText.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notificationText[position], notificationImage[position])
    }

    class NotificationViewHolder(private val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String, image: Int) {
            binding.apply {
                notificationText.text = text
                notificationImage.setImageResource(image)
            }
        }

    }
}