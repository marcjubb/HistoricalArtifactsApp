package com.example.historicalartifactsapp


import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.FirebaseFirestore


class AdminRequestActivity : AppCompatActivity() {
    private lateinit var noReview: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_requests_page)
        noReview = findViewById(R.id.noReview)
        recyclerView = findViewById(R.id.review_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        retrieveReviews { reviews ->
            if (reviews.isEmpty()) {
                noReview.visibility = View.VISIBLE
                Log.w(ContentValues.TAG, "No reviews")
            } else {
                val recyclerView = findViewById<RecyclerView>(R.id.review_recycler_view)
                recyclerView.adapter = ReviewAdapter(reviews)
            }
        }
    }
}
private fun retrieveReviews(callback: (List<Review>) -> Unit) {
    val reviews: MutableList<Review> = ArrayList()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    firestore.collection("Reviews")
        .whereEqualTo("status", "pending")
        .get()
        .addOnSuccessListener { querySnapshot ->
            for (documentSnapshot in querySnapshot) {
                val review = documentSnapshot.toObject(
                    Review::class.java
                )
                reviews.add(review)
            }
            callback(reviews)
        }
        .addOnFailureListener {
            OnFailureListener { e -> Log.w(ContentValues.TAG, "Error loading artifacts", e) }
        }


}