package com.example.historicalartifactsapp

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Cabinet(var name: String, var description: String, var imageName: String? = null, var artifacts: List<Artifact> = listOf()) {

    constructor(): this("", "")

    fun storeCabinet() {
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val cabinetMap: MutableMap<String, Any?> = HashMap()
        cabinetMap["name"] = name
        cabinetMap["description"] = description
        cabinetMap["imageName"] = imageName
        firestore.collection("Cabinets").add(cabinetMap)
            .addOnSuccessListener { cabinetDocRef ->
                Log.d(ContentValues.TAG, "Cabinet added with ID: ${cabinetDocRef.id}")
                // Store the artifact IDs in the cabinet
                val artifactCollectionRef = cabinetDocRef.collection("Artifacts")
                artifacts.forEach { artifact ->
                    val artifactMap = HashMap<String, Any>()
                    artifactCollectionRef.add(artifactMap.apply {
                        artifact.getID{ id ->
                            if (id != null) {
                                put("artifactId",   id )
                            }
                        }
                    })
                        .addOnSuccessListener { artifactDocRef ->
                            Log.d(ContentValues.TAG, "Artifact added with ID: ${artifactDocRef.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error adding artifact", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding cabinet", e)
            }
    }

    fun getCabinetID(callback: (String?) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Artifacts")
            .whereEqualTo("name", name)
            .whereEqualTo("description", description)
            .whereEqualTo("type", "Cabinet")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    callback(document.id)
                    break
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting cabinet ID", exception)
                callback(null)
            }
    }

    fun deleteCabinet() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Cabinets")
            .whereEqualTo("name", name)
            .whereEqualTo("description", description)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val cabinetId = document.id
                    // Delete all artifacts in the cabinet
                    artifacts.forEach { artifact ->
                        artifact.deleteArtifact()
                    }
                    // Delete the cabinet image from Firebase Storage
                    if (imageName != null) {
                        val storage = FirebaseStorage.getInstance()
                        val storageRef = storage.reference.child("images/$imageName")
                        storageRef.delete()
                            .addOnSuccessListener {
                                Log.d(ContentValues.TAG, "Cabinet image deleted with name: $imageName")
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error deleting cabinet image with name: $imageName", e)
                            }
                    }
                    // Delete the cabinet itself
                    firestore.collection("Cabinets").document(cabinetId)
                        .delete()
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "Cabinet deleted with ID: $cabinetId")
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error deleting cabinet with ID: $cabinetId", e)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting cabinet ID", exception)
            }
    }
}
