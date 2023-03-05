package com.example.historicalartifactsapp

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ArtifactDetailActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var artifactNameView: TextView
    private lateinit var artifactDescriptionView: TextView
    private lateinit var artifactRequestButton: Button
    private lateinit var artifactEditButton: Button
    private lateinit var artifactDeleteButton: Button
    private lateinit var artifactId: String
    private lateinit var artifactImageView: ImageView
    private val storageRef = FirebaseStorage.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.artifact_detailed_page)

        artifactRequestButton = findViewById(R.id.request_edit_button)
        artifactNameView = findViewById(R.id.artifact_name)
        artifactDescriptionView = findViewById(R.id.artifact_description)
        artifactEditButton = findViewById(R.id.edit_button)
        artifactDeleteButton = findViewById(R.id.delete_button)
        artifactImageView = findViewById(R.id.artifact_image)

        firestore = FirebaseFirestore.getInstance()

        artifactId = intent.getStringExtra("artifact_id")!!

        firestore.collection("Artifacts").document(artifactId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val artifact = documentSnapshot.toObject(Artifact::class.java)
                if (artifact != null) {
                    artifactNameView.text = artifact.name
                    artifactDescriptionView.text = artifact.description
                    val storagePath = "images/${artifact.imageName }"
                    val imageRef: StorageReference = storageRef.child(storagePath)
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        Picasso.get().load(uri).into(artifactImageView)
                    }
                }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting artifact details", exception)
            }

        FirebaseAuth.getInstance().currentUser?.let { user ->
            isLoggedUser(user.uid) { isLoggedUser ->
                if (isLoggedUser) run {
                    artifactRequestButton.visibility = View.VISIBLE
                }
            }
            isUserCurator(user.uid) { isCurator ->
                if (isCurator) run {
                    artifactRequestButton.visibility = View.GONE
                    artifactEditButton.visibility = View.VISIBLE
                    artifactDeleteButton.visibility = View.VISIBLE
                }
            }
        }

        artifactRequestButton.setOnClickListener(View.OnClickListener {

            val switchActivityIntent = Intent(this, UserRequestActivity::class.java)
            switchActivityIntent.putExtra("artifactID", artifactId)
            startActivity(switchActivityIntent)

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