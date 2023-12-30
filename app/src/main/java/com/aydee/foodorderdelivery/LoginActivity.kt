package com.aydee.foodorderdelivery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.aydee.foodorderdelivery.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // initialize firebase
        auth = Firebase.auth

        // initialize google sign in option
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("851004822751-214r8v5r44d0oilmrk88stco6mjuscu3.apps.googleusercontent.com")
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // onClick google button
        binding.btnGoogle.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

        // onClick Login button
        binding.btnLogin.setOnClickListener {
            // get text from edit text
            email = binding.etEmail.text.toString().trim()
            password = binding.etPassword.text.toString().trim()

            if(email.isBlank() || password.isBlank()){
                showToast("Email or Password is Blank")
            }
            else{
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
                    if(task.isSuccessful){
                        showToast("Login Successful")
                        val intent = Intent(this, SetLocationActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        showToast("Login Failed")
                        Log.d("Account", "LoginActivity: login failed")
                    }
                }
            }
        }

        // if don't have account then intent to sign up activity
        binding.txtDontHaveAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    // launcher for google sign in
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                auth.signInWithCredential(credential).addOnCompleteListener {authTask->
                    if(authTask.isSuccessful){
                        showToast("Google Sign In successful")
                        startActivity(Intent(this, SetLocationActivity::class.java))
                    }
                    else{
                        showToast("Google Sign In failed")
                    }
                }
            }
            else{
                showToast("Google Sign In failed")
            }
        }
        else{
            showToast("Google Sign In failed")
        }
    }

    // show toast message
    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // if user is already logged in then directly move to main activity
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}