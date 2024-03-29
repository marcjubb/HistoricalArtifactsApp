package com.example.historicalartifactsapp

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class Artifact(var name: String, var description: String, var imageName: String? = null) {

    constructor(): this("","", "")

    fun storeArtifact() {
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val artifactMap: MutableMap<String, Any?> = HashMap()
        artifactMap["name"] = name
        artifactMap["description"] = description
        artifactMap["imageName"] = imageName
        firestore.collection("Artifacts").add(artifactMap)
            .addOnSuccessListener(OnSuccessListener<DocumentReference> { documentReference ->
                Log.d(
                    ContentValues.TAG,
                    "Artifact added with ID: " + documentReference.id
                )
            })
            .addOnFailureListener(OnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding artifact", e) })
    }

    fun getID(callback: (String?) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Artifacts")
            .whereEqualTo("name", name)
            .whereEqualTo("description", description)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    callback(document.id)
                    break
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting artifact ID", exception)
                callback(null)
            }
    }
    fun deleteArtifact() {
        val firestore = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance()
        getID { id ->
            if (id != null) {
                // Delete the artifact data from Firestore
                firestore.collection("Artifacts").document(id)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Artifact deleted with ID: $id")
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error deleting artifact with ID: $id", e)
                    }
                // Delete the artifact image from Firebase Storage
                val storageRef = storage.reference.child("images/$imageName")
                storageRef.delete()
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Artifact image deleted with name: $imageName")
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error deleting artifact image with name: $imageName", e)
                    }
            }
        }
    }
}




