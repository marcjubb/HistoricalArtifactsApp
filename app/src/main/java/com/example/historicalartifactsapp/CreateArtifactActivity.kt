package com.example.historicalartifactsapp;

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.historicalartifactsapp.Artifact
import com.example.historicalartifactsapp.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class CreateArtifactActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_artifact_activity)

        nameEditText = findViewById(R.id.etName)
        descriptionEditText = findViewById(R.id.etDescription)

        val createArtifactButton : Button =   findViewById(R.id.btnCreateArtifact)

        createArtifactButton.setOnClickListener {
            val artifact = Artifact(
                nameEditText.text.toString(),
                descriptionEditText.text.toString()

            )
            artifact.storeArtifact()
        }
    }


    }
