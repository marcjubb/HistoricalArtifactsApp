package com.example.historicalartifactsapp;

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


class CreateArtifactActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var btnUpload: Button
    private lateinit var imageTemp: ImageView

    @SuppressLint("DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_artifact_activity)

        btnUpload = findViewById(R.id.upload_picture_button);
        nameEditText = findViewById(R.id.etName)
        descriptionEditText = findViewById(R.id.etDescription)
        imageTemp = findViewById(R.id.tempImage)

        val createArtifactButton : Button =   findViewById(R.id.btnCreateArtifact)




        createArtifactButton.setOnClickListener {
            val artifact = Artifact(
                nameEditText.text.toString(),
                descriptionEditText.text.toString()

            )
            val imageName = "nutty"
            val resId = resources.getIdentifier(imageName, "drawable", packageName)
            uploadPictureToFirebase("$imageName.png", resId)
            artifact.storeArtifact()

            loadImageFromFirebase("$imageName.png",imageTemp)
        }
    }


    private fun uploadPictureToFirebase(fileName: String, resourceId: Int) {
        val storageRef = FirebaseStorage.getInstance().reference
        val drawableRef = storageRef.child("images/$fileName")

        val bitmap = BitmapFactory.decodeResource(resources, resourceId)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = drawableRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            // File uploaded successfully
        }.addOnFailureListener {
            // Handle error
        }
    }

    private fun loadImageFromFirebase(imageName: String, imageView: ImageView) {
        // First, get a reference to the Firebase Storage instance
        val storage = FirebaseStorage.getInstance()

        // Then, get a reference to the image file in Firebase Storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/$imageName")

        // Finally, download the image into a Bitmap and display it in an ImageView
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            val imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imageView.setImageBitmap(imageBitmap)
            imageView.visibility = ImageView.VISIBLE
        }.addOnFailureListener {
            // Handle any errors here
        }
    }






}
