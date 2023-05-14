

package com.example.historicalartifactsapp




import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ArtifactActivity : AppCompatActivity() {
    private val search_edit_text :EditText = findViewById(R.id.search_edit_text)



    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            search_edit_text.setText(savedInstanceState.getString("userInput"))
        }

        setContentView(R.layout.artifacts_homepage)
        val cabinetSelectionButton :Button = findViewById(R.id.cabinet_select_btn)
        val adminReqButton :Button = findViewById(R.id.admin_req_button)
        val adminButton :Button = findViewById(R.id.admin_button)
        val searchButton  :Button = findViewById(R.id.search_button)
        val notificationBtn :Button = findViewById(R.id.view_notifications_button)

        searchButton.setOnClickListener {
            val searchEditText = findViewById<EditText>(R.id.search_edit_text)
            val searchText = searchEditText.text.toString().trim { it <= ' ' }

            retrieveArtifactsSearch(searchText) { artifacts ->
                val recyclerView = findViewById<RecyclerView>(R.id.artifact_recycler_view)
                recyclerView.adapter = ArtifactAdapter(artifacts)
            }
        }
        notificationBtn.setOnClickListener {
            switchActivitiesNotificationsView()
        }
        adminButton.setOnClickListener {
            switchActivitiesCreateArtifact()
        }

        adminReqButton.setOnClickListener {
            switchActivitiesRequestView()
        }

        cabinetSelectionButton.setOnClickListener {
            switchActivitiesCabinetSelection()
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
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("userInput", search_edit_text.text.toString())
    }

    private fun switchActivitiesNotificationsView() {
        val switchActivityIntent = Intent(this, NotificationActivity::class.java)
        startActivity(switchActivityIntent)
    }
    private fun switchActivitiesRequestView() {
        val switchActivityIntent = Intent(this, AdminRequestActivity::class.java)
        startActivity(switchActivityIntent)
    }

    private fun switchActivitiesCreateArtifact() {
        val switchActivityIntent = Intent(this, CreateArtifactActivity::class.java)
        startActivity(switchActivityIntent)
    }
    private fun switchActivitiesCabinetSelection() {
        val switchActivityIntent = Intent(this, CabinetSelectionActivity::class.java)
        startActivity(switchActivityIntent)
    }
}





private fun retrieveArtifactsSearch(searchText: String, callback: (List<Artifact>) -> Unit) {
    val artifacts: MutableList<Artifact> = ArrayList()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    firestore.collection("Artifacts")
        .whereEqualTo("name", searchText) // Filter artifacts by name that matches search text
        .get()
        .addOnSuccessListener { querySnapshot ->
            for (documentSnapshot in querySnapshot) {
                val artifact = documentSnapshot.toObject(Artifact::class.java)
                artifacts.add(artifact)
            }
            // If no artifacts matched search text by name, try searching by body
            if (artifacts.isEmpty()) {
                firestore.collection("Artifacts")
                    .whereEqualTo("body", searchText) // Filter artifacts by body that matches search text
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
            } else {
                callback(artifacts)
            }
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error loading artifacts", e)
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

