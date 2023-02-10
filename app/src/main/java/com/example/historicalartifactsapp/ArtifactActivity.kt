

package com.example.historicalartifactsapp


import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ArtifactActivity : AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.artifacts_homepage)

        val adminReqButton :Button = findViewById(R.id.admin_req_button)
        val adminButton :Button = findViewById(R.id.admin_button)
        adminButton.setOnClickListener {
            switchActivitiesCreateArtifact()
        }

        adminReqButton.setOnClickListener {
            switchActivitiesRequestView()
        }



        retrieveArtifacts { artifacts ->
            val recyclerView = findViewById<RecyclerView>(R.id.artifact_recycler_view)
            recyclerView.adapter = ArtifactAdapter(artifacts)
        }

        FirebaseAuth.getInstance().currentUser?.let { user ->
            isUserCurator(user.uid) { isCurator ->
                if (isCurator) run {
                    adminButton.visibility = View.VISIBLE
                    adminReqButton.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun switchActivitiesRequestView() {
        val switchActivityIntent = Intent(this, AdminRequestActivity::class.java)
        startActivity(switchActivityIntent)
    }

    private fun switchActivitiesCreateArtifact() {
        val switchActivityIntent = Intent(this, CreateArtifactActivity::class.java)
        startActivity(switchActivityIntent)
    }
}




private fun retrieveArtifacts(callback: (List<Artifact>) -> Unit) {
    val artifacts: MutableList<Artifact> = ArrayList()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    firestore.collection("Artifacts").get()
        .addOnSuccessListener { querySnapshot ->
            for (documentSnapshot in querySnapshot) {
                val artifact = documentSnapshot.toObject(
                    Artifact::class.java
                )
                artifacts.add(artifact)
            }
            callback(artifacts)
        }
        .addOnFailureListener {
            OnFailureListener { e -> Log.w(TAG, "Error loading artifacts", e) }
        }


}

fun isUserCurator(uid: String, callback: (Boolean) -> Unit) {
    val fStore = FirebaseFirestore.getInstance()
    val df = fStore.collection("Users").document(uid)
    df.get().addOnSuccessListener { documentSnapshot ->
        Log.d("TAG", "onSuccess: ${documentSnapshot.data}")

        if (documentSnapshot.getString("isCurator") == "1") {
            callback(true)
        } else {
            callback(false)
        }
    }
}

