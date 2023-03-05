package com.example.historicalartifactsapp


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


class ArtifactAdapter(private val artifactsList: List<Artifact>) :

    RecyclerView.Adapter<ArtifactAdapter.ArtifactViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtifactViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_row, parent, false)
        return ArtifactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArtifactViewHolder, position: Int) {
        val artifact = artifactsList[position]
        holder.bind(artifact)


    }





    override fun getItemCount(): Int {
        return artifactsList.size
    }
    inner class ArtifactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val storageRef = FirebaseStorage.getInstance().reference

        private val artifactImage: ImageView = itemView.findViewById(R.id.image)

        fun bind(artifact: Artifact) {
            val viewArtifactButton: Button = itemView.findViewById(R.id.view_artifact_button)
            val artifactName: TextView = itemView.findViewById(R.id.name)
            val artifactDescription: TextView = itemView.findViewById(R.id.description)
            artifactName.text = artifact.name
            artifactDescription.text = artifact.description
            // Load image from Firebase Storage using FirebaseUI Storage library
            val storagePath = "images/${artifact.imageName }"
            val imageRef: StorageReference = storageRef.child(storagePath)
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(artifactImage)
            }
            viewArtifactButton.setOnClickListener(View.OnClickListener {
                artifact.getID { artifactId ->
                    if (artifactId != null) {
                        val intent = Intent(itemView.context, ArtifactDetailActivity::class.java)
                        intent.putExtra("artifact_id", artifactId)
                        itemView.context.startActivity(intent)
                    }
                }
            })
        }
    }


}
