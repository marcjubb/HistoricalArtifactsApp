package com.example.historicalartifactsapp

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class UserRequestActivity : AppCompatActivity() {
    private lateinit var artifactNameEditText: EditText
    private lateinit var artifactDescriptionEditText: EditText
    private lateinit var submitRequestButton: Button
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_edit_request_page)
        artifactNameEditText = findViewById<EditText>(R.id.artifactName)
        artifactDescriptionEditText = findViewById<EditText>(R.id.artifactDescription)
        submitRequestButton = findViewById<View>(R.id.submitButton) as Button
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("requests")

        firestore = FirebaseFirestore.getInstance()

        val artifactId = intent.getStringExtra("artifact_id")

        if (artifactId != null) {
            firestore.collection("Artifacts").document(artifactId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val artifact = documentSnapshot.toObject(Artifact::class.java)
                    if (artifact != null) {
                        artifactNameEditText.setText(artifact.name)
                        artifactDescriptionEditText.setText(artifact.description)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting artifact details", exception)
                }
        }






        submitRequestButton.setOnClickListener {
            val artifactName = artifactNameEditText.text.toString()
            val artifactDescription = artifactDescriptionEditText.text.toString()
            if (artifactName.isNotEmpty() && artifactDescription.isNotEmpty()) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val userid = currentUser.uid
                    val review = Review(
                        artifactName, userid,
                        "1.1", artifactDescription, "pending"
                    )
                    review.storeReview()
                }
                Toast.makeText(
                    this@UserRequestActivity,
                    "Request submitted successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@UserRequestActivity,
                    "Please enter both artifact name and description!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}