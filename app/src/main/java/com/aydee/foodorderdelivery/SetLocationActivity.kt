package com.aydee.foodorderdelivery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.aydee.foodorderdelivery.databinding.ActivitySetLocationBinding

class SetLocationActivity : AppCompatActivity() {

    private val binding : ActivitySetLocationBinding by lazy {
        ActivitySetLocationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val locations = arrayOf<String>("Patna", "Danapur", "Gaya", "Arrah", "Bhagalpur")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locations)
        val autoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)

        binding.listOfLocation.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}