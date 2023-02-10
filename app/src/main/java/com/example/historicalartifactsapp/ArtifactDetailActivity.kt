package com.example.historicalartifactsapp

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
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.auth.User

class ArtifactDetailActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var artifactNameView: TextView
    private lateinit var artifactDescriptionView: TextView
    private lateinit var artifactRequestButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.artifact_detailed_page)
        artifactRequestButton = findViewById(R.id.request_edit_button)
        artifactNameView = findViewById(R.id.artifact_name)
        artifactDescriptionView = findViewById(R.id.artifact_description)

        firestore = FirebaseFirestore.getInstance()

        val artifactId = intent.getStringExtra("artifact_id")

        if (artifactId != null) {
            firestore.collection("Artifacts").document(artifactId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val artifact = documentSnapshot.toObject(Artifact::class.java)
                    if (artifact != null) {
                        artifactNameView.text = artifact.name
                        artifactDescriptionView.text = artifact.description
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting artifact details", exception)
                }
        }

        FirebaseAuth.getInstance().currentUser?.let { user ->
            isLoggedUser(user.uid) { isLoggedUser ->
                if (isLoggedUser) run {
                    artifactRequestButton.visibility = View.VISIBLE
                }
            }
        }

        artifactRequestButton.setOnClickListener(View.OnClickListener {
            switchActivitiesRequest()
        })

    }
    private fun switchActivitiesRequest() {

        val switchActivityIntent = Intent(this, UserRequestActivity::class.java)
        startActivity(switchActivityIntent)
    }

    fun isLoggedUser(uid: String, callback: (Boolean) -> Unit) {
        val fStore = FirebaseFirestore.getInstance()
        val df = fStore.collection("Users").document(uid)
        df.get().addOnSuccessListener { documentSnapshot ->
            Log.d("TAG", "onSuccess: ${documentSnapshot.data}")
            if (documentSnapshot.getString("isCurator") == "1"
                ||documentSnapshot.getString("isCurator") == "0") {
                callback(true)
            } else {
                callback(false)
            }

        }
    }

}