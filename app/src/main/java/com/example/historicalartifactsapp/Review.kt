package com.example.historicalartifactsapp


import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class Review(
    var artifactID: String?, var artifactName: String, var userID: String, var reviewDate:
String, var artifactDescription:
String, var status: String) {

    constructor(): this("","","","","","")

    fun storeReview() {
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val artifactMap: MutableMap<String, Any?> = HashMap()
        artifactMap["artifactID"] = artifactID
        artifactMap["artifactName"] = artifactName
        artifactMap["artifactDescription"] = artifactDescription
        artifactMap["userID"] = userID
        artifactMap["reviewDate"] = reviewDate
        artifactMap["status"] = status
        firestore.collection("Reviews").add(artifactMap)
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
        firestore.collection("Reviews")
            .whereEqualTo("userID", userID)
            .whereEqualTo("artifactName", artifactName)
            .whereEqualTo("artifactDescription", artifactDescription)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    callback(document.id)
                    break
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting review ID", exception)
                callback(null)
            }
    }
}


