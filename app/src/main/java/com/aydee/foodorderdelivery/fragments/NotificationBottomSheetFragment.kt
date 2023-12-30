package com.aydee.foodorderdelivery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.aydee.foodorderdelivery.R
import com.aydee.foodorderdelivery.adapter.NotificationAdapter
import com.aydee.foodorderdelivery.databinding.FragmentNotificationBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NotificationBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNotificationBottomSheetBinding

    private val notificationText = listOf<String>(
        "Your order has been Canceled Successfully",
        "Order has been taken by the driver",
        "Congrats Your Order Placed"
    )

    private val notificationImage = listOf<Int>(
        R.drawable.sademoji,
        R.drawable.truck,
        R.drawable.congrats
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBottomSheetBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            dismiss()
        }

        val adapter = NotificationAdapter(ArrayList(notificationText), ArrayList(notificationImage))
        binding.notificationRV.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRV.adapter = adapter

        return binding.root
    }

    companion object {
    }
}