package com.example.historicalartifactsapp

import android.app.Notification
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationActivity : AppCompatActivity() {
    private lateinit var noNotification: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var currentUserUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notifications_page)

        noNotification = findViewById(R.id.noNotifications)
        recyclerView = findViewById(R.id.notification_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Get the UID of the current user
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        retrieveNotifications { notifications ->
            if (notifications.isEmpty()) {
                noNotification.visibility = View.VISIBLE
                Log.w(ContentValues.TAG, "No notifications")
            } else {
                adapter = NotificationAdapter(notifications)
                recyclerView.adapter = adapter
            }
        }
    }

    private fun retrieveNotifications(callback: (List<com.example.historicalartifactsapp.Notification>) -> Unit) {
        val notifications: MutableList<com.example.historicalartifactsapp.Notification> = ArrayList()
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        firestore.collection("Notifications")
            .whereEqualTo("userId", currentUserUid)
            .orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot) {
                    val notification = documentSnapshot.toObject(com.example.historicalartifactsapp.Notification::class.java)
                    notifications.add(notification)
                }
                callback(notifications)
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error loading notifications", e)
            }
    }
}
