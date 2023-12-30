package com.aydee.foodorderdelivery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.aydee.foodorderdelivery.databinding.ActivitySignUpBinding
import com.aydee.foodorderdelivery.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // initialize firebase
        auth = Firebase.auth
        database = Firebase.database.reference

        // Google sign in option
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("851004822751-214r8v5r44d0oilmrk88stco6mjuscu3.apps.googleusercontent.com")
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // onClick google button
        binding.btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

        // onClick create account button
        binding.btnCreateAccount.setOnClickListener {
            // get text from edit text
            name = binding.etName.text.toString().trim()
            email = binding.etEmail.text.toString().trim()
            password = binding.etPassword.text.toString().trim()

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                showToast("Fill all the Details")
            } else {
                createAccount()
            }
        }

        // if already have account then intent to login activity
        binding.txtAlreadyHaveAccount.setOnClickListener {
            updateUi()
        }
    }

    // Launcher for google sign in
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                auth.signInWithCredential(credential).addOnCompleteListener {authTask->
                    if(authTask.isSuccessful){
                        showToast("Google sign is successful")
                        val intent = Intent(this, SetLocationActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        showToast("Google sign failed")
                    }
                }
                    .addOnFailureListener {
                        showToast("Google sign in failed")
                        Log.d("Account", "Google sign in: credential error", task.exception)
                    }
            }
            else{
                showToast("Google sign in failed")
                Log.d("Account", "Google sign in: task failure", task.exception)
            }
        }
        else {
            showToast("Google sign in failed")
            Log.d("Account", "Google sign in: Error in result")
        }
    }

    // Create Account
    private fun createAccount() {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("Sign Up is Successful")
                saveUserData()
                updateUi()
            } else {
                showToast("Sign Up Failed")
                Log.d("Account", "SignUpActivity: failed", task.exception)
            }
        }
    }

    // save data to database
    private fun saveUserData() {
        val user = UserModel(name, email, password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        userId.let {
            database.child("user").child(userId).setValue(user)
        }
    }

    // show toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Intent to login activity
    private fun updateUi() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}