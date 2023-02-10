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


        setContentView(R.layout.artifact_detailed_page)
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
            println("ACCEPT")
        })
        reviewDeclineButton.setOnClickListener(View.OnClickListener {
            println("DECLINE")
        })

    }

    private fun switchActivitiesRequest() {

        val switchActivityIntent = Intent(this, UserRequestActivity::class.java)
        startActivity(switchActivityIntent)
    }

}