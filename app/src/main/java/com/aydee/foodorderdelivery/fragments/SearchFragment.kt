package com.aydee.foodorderdelivery.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aydee.foodorderdelivery.adapter.MenuAdapter
import com.aydee.foodorderdelivery.databinding.FragmentSearchBinding
import com.aydee.foodorderdelivery.model.MenuItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var database: FirebaseDatabase

    private lateinit var originalMenuItems: MutableList<MenuItems>
    //private val filteredMenuItems = mutableListOf<MenuItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        // retrieve all menu items
        retrieveOriginalMenuItems()
        // setup search view
        setUpSearchView()
        // show all menu
        showAllMenu()

        return binding.root
    }

    private fun retrieveOriginalMenuItems() {
        database = FirebaseDatabase.getInstance()
        val foodRef = database.getReference("menu")
        originalMenuItems = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(MenuItems::class.java)
                    menuItem?.let {
                        originalMenuItems.add(it)
                    }
                }
                setAdapter(originalMenuItems)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Menu items loading failed", Toast.LENGTH_SHORT)
                    .show()
                Log.d("DetailsActivity", error.message)
            }
        })
    }

    private fun showAllMenu() {
        val filteredMenuItems: ArrayList<MenuItems> = ArrayList(originalMenuItems)
        setAdapter(filteredMenuItems)
    }

    private fun setUpSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterMenuItems(query)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                filterMenuItems(p0)
                return true
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterMenuItems(query: String?) {
        val filteredMenuItems = originalMenuItems.filter {
            query?.let { q -> it.foodName?.contains(q, ignoreCase = true) } == true
        }
        setAdapter(ArrayList(filteredMenuItems))
    }

    private fun setAdapter(filteredMenuItems: MutableList<MenuItems>) {
        adapter = MenuAdapter(filteredMenuItems, requireContext())
        binding.menuRV.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRV.adapter = adapter
    }
}