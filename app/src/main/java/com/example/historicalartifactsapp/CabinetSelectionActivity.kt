package com.example.historicalartifactsapp
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.FirebaseFirestore


class CabinetSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cabinet_selection_page)
        retrieveCabinets { cabinets ->
            val recyclerView = findViewById<RecyclerView>(R.id.cabinet_recycler_view)
            recyclerView.adapter = CabinetAdapter(cabinets)
        }

    }
    private fun retrieveCabinets(callback: (List<Cabinet>) -> Unit) {
        val cabinets: MutableList<Cabinet> = ArrayList()
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

        firestore.collection("Cabinets").get()
            .addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot) {
                    val cabinet = documentSnapshot.toObject(
                        Cabinet::class.java
                    )
                    cabinets.add(cabinet)
                }
                callback(cabinets)
            }
            .addOnFailureListener {
                OnFailureListener { e -> Log.w(ContentValues.TAG, "Error loading cabinets", e) }
            }


    }

}


