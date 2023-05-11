package com.example.historicalartifactsapp

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class EditArtifactActivity : AppCompatActivity() {

    private lateinit var artifactID: String
    private lateinit var artifactNameEditText: EditText
    private lateinit var artifactDescriptionEditText: EditText
    private lateinit var imageTemp: ImageView
    private lateinit var btnUpload: Button
    private lateinit var imageName: String
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_edit_artifact_activity)

        btnUpload = findViewById(R.id.upload_picture_button)
        artifactNameEditText = findViewById(R.id.etName)
        artifactDescriptionEditText = findViewById(R.id.etDescription)
        imageTemp = findViewById(R.id.tempImage)

        artifactID = intent.getStringExtra("artifactID") ?: ""
        loadArtifactData()

        val timeStamp = System.currentTimeMillis().toString()
        imageName = "-$timeStamp.png"

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imageUri = uri // Save the image URI
                uploadPictureToFirebase(imageName, uri){
                    loadImageFromFirebase(imageName, imageTemp)
                }

            }
        }

        btnUpload.setOnClickListener {
            pickImage.launch("image/*")

        }

        val saveUpdateButton = findViewById<Button>(R.id.update_artifact_btn)
        saveUpdateButton.setOnClickListener {
            saveChanges()
        }
    }

    private fun loadArtifactData() {
        // Get a reference to the artifact document in Firestore
        val firestore = FirebaseFirestore.getInstance()
        val artifactRef = firestore.collection("Artifacts").document(artifactID)

        // Get the artifact data from Firestore
        artifactRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Fill in the UI elements with the artifact data
                    val artifact = documentSnapshot.toObject(Artifact::class.java)
                    artifactNameEditText.setText(artifact?.name)
                    artifactDescriptionEditText.setText(artifact?.description)
                    // Load the artifact image into the image view using Picasso
                    val originalImageName = artifact?.imageName
                    if (originalImageName != null) {
                        loadImageFromFirebase(originalImageName, imageTemp)
                    }
                } else {
                    Toast.makeText(this, "Error loading artifact data", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error loading artifact data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }


    }

    private fun saveChanges() {
        val name = artifactNameEditText.text.toString().trim()
        val description = artifactDescriptionEditText.text.toString().trim()

        val imageNameToSave = if (imageUri != null) imageName else ""

        val firestore = FirebaseFirestore.getInstance()
        val artifactRef = firestore.collection("Artifacts").document(artifactID)

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "description" to description,
            "imageName" to imageNameToSave
        )

        artifactRef.update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()
                // Retrieve all users who have bookmarked the artifact
                firestore.collection("Bookmarks")
                    .whereEqualTo("artifactId", artifactID)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                val userId = document.getString("userId")
                                if (userId != null) {
                                    sendNotification(userId )
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.exception)
                        }
                    }

                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving changes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendNotification(userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val notification = hashMapOf(
            "userId" to userId,
            "artifactId" to artifactID,
            "time" to FieldValue.serverTimestamp()
        )

        firestore.collection("Notifications")
            .add(notification)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Notification sent to user $userId for artifact $artifactID with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error sending notification to user $userId for artifact $artifactID", e)
            }
    }

    private fun uploadPictureToFirebase(fileName: String, imageUri: Uri, completion: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val drawableRef = storageRef.child("images/$fileName")

        val inputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = drawableRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl.toString()
            completion(downloadUrl)
        }.addOnFailureListener {
            // Handle error
            completion(null.toString())
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

        }
    }

}
