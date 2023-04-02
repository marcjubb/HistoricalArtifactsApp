package com.example.historicalartifactsapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


class CreateCabinetActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var btnUpload: Button
    private lateinit var imageTemp: ImageView
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_cabinet_activity)

        btnUpload = findViewById(R.id.upload_picture_button)
        nameEditText = findViewById(R.id.etName)
        descriptionEditText = findViewById(R.id.etDescription)


        val timeStamp = System.currentTimeMillis().toString()
        val imageName = "-$timeStamp.png"
        imageTemp = findViewById(R.id.tempImage)

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                uploadPictureToFirebase(imageName, uri)
            }
        }

        val createArtifactButton : Button =   findViewById(R.id.btnCreateCabinet)

        btnUpload.setOnClickListener {
            pickImage.launch("image/*")
            loadImageFromFirebase(imageName, imageTemp)
        }

        createArtifactButton.setOnClickListener {
            val cabinet = Cabinet(
                nameEditText.text.toString(),
                descriptionEditText.text.toString(),
                imageName,
            )
            imageUri?.let { uploadPictureToFirebase(imageName, it) }
            cabinet.storeCabinet()
        }
    }


    private fun uploadPictureToFirebase(fileName: String, imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val drawableRef = storageRef.child("images/$fileName")

        val inputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

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
