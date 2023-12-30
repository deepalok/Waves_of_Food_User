package com.aydee.foodorderdelivery.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentHostCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.aydee.foodorderdelivery.R
import com.aydee.foodorderdelivery.adapter.MenuAdapter
import com.aydee.foodorderdelivery.adapter.PopularAdapter
import com.aydee.foodorderdelivery.databinding.FragmentHomeBinding
import com.aydee.foodorderdelivery.model.MenuItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItems>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate( inflater, container, false)

        // onClick "View Menu" to view menu
        binding.btnViewMenu.setOnClickListener {
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }

        // retrieve and display popular items
        retrieveAndDisplayPopularItems()

        return binding.root
    }

    private fun retrieveAndDisplayPopularItems() {
        // initialize firebase database
        database = FirebaseDatabase.getInstance()
        val foodRef = database.reference.child("menu")
        menuItems = mutableListOf()
        foodRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItems::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                // set random popular items
                randomPopularItems()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Popular", error.message)
            }
        })
    }

    private fun randomPopularItems() {
        val index = menuItems.indices.toList().shuffled()
        val numToShow = 6
        val popularItems = index.take(numToShow).map { menuItems[it] }
        // set Adapter
        setAdapter(popularItems)
    }

    private fun setAdapter(popularItems: List<MenuItems>) {
        val adapter = MenuAdapter(ArrayList(popularItems), requireContext())
        binding.popularRV.layoutManager = LinearLayoutManager(requireContext())
        binding.popularRV.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val foodName = listOf<String>("Burger", "Sandwich", "Ice cream", "Maggie", "Pasta")
//        val foodImage = listOf<Int>(R.drawable.menu1, R.drawable.menu2, R.drawable.menu3, R.drawable.menu4, R.drawable.menu5)
//        val foodPrice = listOf<String>("$7", "$8", "$6", "$10", "$4")
    }
}