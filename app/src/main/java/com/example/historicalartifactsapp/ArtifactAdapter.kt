package com.example.historicalartifactsapp


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView


class ArtifactAdapter(private val artifactsList: List<Artifact>) :

    RecyclerView.Adapter<ArtifactAdapter.ArtifactViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtifactViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_row, parent, false)
        return ArtifactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArtifactViewHolder, position: Int) {
        val artifact = artifactsList[position]
        holder.artifactName.text = artifact.name
        holder.artifactDescription.text = artifact.description


        holder.viewArtifactButton.setOnClickListener(View.OnClickListener {
            artifact.getID { artifactId ->
                if (artifactId != null) {
                    val intent = Intent(holder.itemView.context, ArtifactDetailActivity::class.java)
                    intent.putExtra("artifact_id", artifactId)
                    holder.itemView.context.startActivity(intent)
                }
            }
        })

    }




    override fun getItemCount(): Int {
        return artifactsList.size
    }

    inner class ArtifactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewArtifactButton: Button
        var artifactName: TextView
        var artifactDescription: TextView

        init {
            viewArtifactButton = itemView.findViewById(R.id.view_artifact_button)
            artifactName = itemView.findViewById(R.id.name)
            artifactDescription = itemView.findViewById(R.id.description)

        }
    }
}
