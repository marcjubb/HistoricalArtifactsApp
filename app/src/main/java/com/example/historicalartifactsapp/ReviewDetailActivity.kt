package com.example.historicalartifactsapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReviewDetailActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var artifactNameView: TextView
    private lateinit var artifactDescriptionView: TextView
    private lateinit var reviewDateView: TextView
    private lateinit var reviewStatusView: TextView

    private lateinit var reviewAcceptButton: Button
    private lateinit var reviewDeclineButton: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.review_detailed_page)
        reviewAcceptButton = findViewById(R.id.accept_request_button)
        reviewDeclineButton = findViewById(R.id.decline_request_button)
        artifactNameView = findViewById(R.id.artifact_name)
        artifactDescriptionView = findViewById(R.id.artifact_description)
        reviewDateView = findViewById(R.id.review_date)
        reviewStatusView = findViewById(R.id.status_tv)


        firestore = FirebaseFirestore.getInstance()

        val reviewID = intent.getStringExtra("review_id")

        if (reviewID != null) {
            firestore.collection("Reviews").document(reviewID)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val review = documentSnapshot.toObject(Review::class.java)
                    if (review != null) {
                        artifactNameView.text = review.artifactName
                        artifactDescriptionView.text = review.artifactDescription
                        reviewDateView.text = review.reviewDate
                        reviewStatusView.text = review.status
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting review details", exception)
                }
        }


        reviewAcceptButton.setOnClickListener(View.OnClickListener {
            acceptReview()
        })
        reviewDeclineButton.setOnClickListener(View.OnClickListener {
            declineReview()
        })

    }

    private fun declineReview() {
        firestore = FirebaseFirestore.getInstance()
        val reviewID = intent.getStringExtra("review_id")
        if (reviewID != null) {
            val reviewRef = firestore.collection("Reviews").document(reviewID)
            reviewRef.update("status", "denied")
                .addOnSuccessListener {
                    Log.d("Firestore", "Review denied successfully")
                }
                .addOnFailureListener {
                    Log.e("Firestore", "Error denied review: ${it.message}")
                }
        }

    }

    private fun acceptReview() {
        firestore = FirebaseFirestore.getInstance()
        val reviewID = intent.getStringExtra("review_id")
        if (reviewID != null) {
            val reviewRef = firestore.collection("Reviews").document(reviewID)
            reviewRef.update("status", "approved")
                .addOnSuccessListener {
                    Log.d("Firestore", "Review approved successfully")
                }
                .addOnFailureListener {
                    Log.e("Firestore", "Error approved review: ${it.message}")
                }

            firestore.collection("Reviews").document(reviewID)
                .get()
                .addOnSuccessListener {documentSnapshot ->
                    val review = documentSnapshot.toObject(Review::class.java)
                    if (review != null) {
                        val artifactID = review.artifactID
                        if (artifactID != null) {
                            firestore.collection("Artifacts")
                                .document(artifactID)
                                .update("description", review.artifactDescription,
                                    "name", review.artifactName)
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Artifact updated successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error updating artifact", e)
                                }
                        }
                    }

                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error retrieving reviews", e)
                }
        }
    }



}