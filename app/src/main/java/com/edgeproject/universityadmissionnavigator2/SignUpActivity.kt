package com.edgeproject.universityadmissionnavigator2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edgeproject.universityadmissionnavigator2.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.tvlogin.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.signUpButton.setOnClickListener {
            val email = binding.tvemail.text.toString()
            val password = binding.tvpassword.text.toString()
            val sscgpaString = binding.tvsscgpa.text.toString()
            val hscgpaString = binding.tvhscgpa.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && sscgpaString.isNotEmpty() && hscgpaString.isNotEmpty()) {
                val sscgpa = sscgpaString.toDoubleOrNull()
                val hscgpa = hscgpaString.toDoubleOrNull()

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid ?: return@addOnCompleteListener
                        val user = hashMapOf(
                            "user_id" to userId,
                            "email" to email,
                            "password" to password,
                            "sscgpa" to sscgpa,
                            "hscgpa" to hscgpa
                        )
                        db.collection("users").document(userId).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Sign Up Failed for database!", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Sign Up Failed for user creation!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}