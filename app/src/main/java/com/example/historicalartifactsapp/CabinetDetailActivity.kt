package com.example.historicalartifactsapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.MotionEffect.TAG
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CabinetDetailActivity : AppCompatActivity() {
    private lateinit var cabinetId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cabinet_detail)

        // Retrieve the cabinet ID from the intent extras
        cabinetId = intent.getStringExtra("cabinetId") ?: ""

        retrieveArtifacts(cabinetId) { artifacts ->
            val recyclerView = findViewById<RecyclerView>(R.id.artifact_recycler_view)
            recyclerView.adapter = ArtifactAdapter(artifacts)
        }
    }

    private fun retrieveArtifacts(cabinetId: String, callback: (List<Artifact>) -> Unit) {
        val artifacts: MutableList<Artifact> = ArrayList()
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

        firestore.collection("Artifacts")
            .whereEqualTo("cabinetId", cabinetId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot) {
                    val artifact = documentSnapshot.toObject(Artifact::class.java)
                    artifacts.add(artifact)
                }
                callback(artifacts)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error loading artifacts", e)
            }
    }
}

