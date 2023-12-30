package com.aydee.foodorderdelivery.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.aydee.foodorderdelivery.LoginActivity
import com.aydee.foodorderdelivery.R
import com.aydee.foodorderdelivery.databinding.FragmentHistoryBinding
import com.aydee.foodorderdelivery.databinding.FragmentProfileBinding
import com.aydee.foodorderdelivery.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        setUserData()

        binding.apply {
            btnEdit.setOnClickListener {
                profileName.isEnabled = true
                profileAddress.isEnabled = true
                profilePhone.isEnabled = true
                profileEmail.isEnabled = true
                btnSaveInfo.visibility = View.VISIBLE
            }

            btnSaveInfo.setOnClickListener {
                val name = binding.profileName.text.toString()
                val email = binding.profileEmail.text.toString()
                val phone = binding.profilePhone.text.toString()
                val address = binding.profileAddress.text.toString()

                updateUserData(name, email, phone, address)

                profileName.isEnabled = false
                profileAddress.isEnabled = false
                profilePhone.isEnabled = false
                profileEmail.isEnabled = false
                btnSaveInfo.visibility = View.GONE
            }
        }

        binding.btnLogOut.setOnClickListener {
            signOut()
        }

        return binding.root
    }

    private fun updateUserData(name: String, email: String, phone: String, address: String) {
        val userId = auth.currentUser?.uid
        if(userId != null){
            val userReference = database.getReference("user").child(userId)
            val userData: HashMap<String, String> = hashMapOf(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "address" to address
            )
            userReference.setValue(userData).addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Profile updated failure", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if(userId != null){
            val userReference = database.getReference("user").child(userId)
            userReference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val userProfile = snapshot.getValue(UserModel::class.java)
                        binding.apply {
                            profileName.setText(userProfile?.name)
                            profileAddress.setText(userProfile?.address)
                            profilePhone.setText(userProfile?.phone)
                            profileEmail.setText(userProfile?.email)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("ProfileFragment", "set user data failed")
                }
            })
        }

    }

    // sign out from google sign in
    private fun signOut(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("851004822751-214r8v5r44d0oilmrk88stco6mjuscu3.apps.googleusercontent.com")
            .requestEmail().build()
        GoogleSignIn.getClient(requireContext(), gso).signOut()
        Firebase.auth.signOut()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
    }
}