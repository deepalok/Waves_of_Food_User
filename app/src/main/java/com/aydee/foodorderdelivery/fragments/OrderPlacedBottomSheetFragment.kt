package com.aydee.foodorderdelivery.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aydee.foodorderdelivery.MainActivity
import com.aydee.foodorderdelivery.R
import com.aydee.foodorderdelivery.databinding.FragmentOrderPlacedBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OrderPlacedBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentOrderPlacedBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderPlacedBottomSheetBinding.inflate(inflater, container, false)

        binding.btnGoHome.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }


    companion object {
    }
}