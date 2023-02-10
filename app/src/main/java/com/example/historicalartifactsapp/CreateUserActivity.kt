package com.example.historicalartifactsapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class CreateUserActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var createButton: Button
    private lateinit var fStore: FirebaseFirestore

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.create_user_page)
        createButton = findViewById(R.id.btn_create_user)
            createButton.setOnClickListener {
            email = findViewById(R.id.et_email)
            password = findViewById(R.id.et_password)

            if (email.text.isEmpty() || password.text.isEmpty()) {
                Toast.makeText(this, "Username or password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

                createUser(email.text.toString(),password.text.toString());

        }
    }



    private lateinit var auth: FirebaseAuth

    private fun createUser(email: String, password: String) {
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Account Created.",
                        Toast.LENGTH_SHORT).show()
                    fStore= FirebaseFirestore.getInstance()
                    val df : DocumentReference = auth.currentUser?.let { fStore.collection("Users").document(it.uid) }!!
                    val userInfo : HashMap<String, Any> = HashMap()
                    userInfo["userEmail"] = email
                    userInfo["isCurator"] = "0"
                    df.set(userInfo)
                    updateUI()
                } else {
                    updateUI()
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun updateUI() {
            val switchActivityIntent = Intent(this, ArtifactActivity::class.java)
            startActivity(switchActivityIntent)
        println("NormalUser")
    }

}
