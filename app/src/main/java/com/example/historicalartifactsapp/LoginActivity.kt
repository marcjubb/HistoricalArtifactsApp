package com.example.historicalartifactsapp


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var createBtn: Button
    private lateinit var guestBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)


        email = findViewById(R.id.et_email)
        password = findViewById(R.id.et_password)
        loginButton = findViewById(R.id.login_btn)
        createBtn = findViewById(R.id.create_btn)
        guestBtn = findViewById(R.id.guest_btn)



        loginButton.setOnClickListener {
            val email = email.text.toString()
            val pass = password.text.toString()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Username or password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            login(email,pass);
        }

        /*val adminLogin:Button = findViewById(R.id.admin_login)
        adminLogin.setOnClickListener {

            val email = "t@t.com"
            val pass ="test123123"
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Username or password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            login(email,pass);

        }*/


       /* val userLogin:Button = findViewById(R.id.user_login)
        userLogin.setOnClickListener {

            val email = "k@t.com"
            val pass ="test123123"
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Username or password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            login(email,pass);

        }*/

        createBtn.setOnClickListener {
            switchActivitiesCreate();
        }

        guestBtn.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            auth.signInAnonymously()
                .addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        // User is signed in anonymously
                        switchActivitiesHome()
                        Log.d(TAG, "Sign in anonymously: success")
                    } else {
                        // If sign in fails, display a message to the user
                        Log.w(TAG, "Sign in anonymously: failure", task.exception)
                        Toast.makeText(
                            this, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

    }


    private fun switchActivitiesCreate() {

        val switchActivityIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(switchActivityIntent)
    }
    private fun switchActivitiesHome() {
        val switchActivityIntent = Intent(this, ArtifactActivity::class.java)
        startActivity(switchActivityIntent)
    }



    private lateinit var auth: FirebaseAuth

    private fun login(email: String, password: String) {
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    switchActivitiesHome()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }







     private fun isUserCurator(uid: String, callback: (Boolean) -> Unit) {
        val fStore = FirebaseFirestore.getInstance()
        val df = fStore.collection("Users").document(uid)
        df.get().addOnSuccessListener { documentSnapshot ->
            Log.d("TAG", "onSuccess: ${documentSnapshot.data}")

            if (documentSnapshot.getString("isCurator") == "1") {
                callback(true)
            } else {
                callback(false)
            }
        }
    }



}