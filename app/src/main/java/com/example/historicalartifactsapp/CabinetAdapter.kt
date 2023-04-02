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


class CabinetAdapter(private val cabinetList: List<Cabinet>) :

    RecyclerView.Adapter<CabinetAdapter.CabinetViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CabinetViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_row_cabinet, parent, false)
        return CabinetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CabinetViewHolder, position: Int) {
        val cabinet = cabinetList[position]
        holder.bind(cabinet)


    }


    override fun getItemCount(): Int {
        return cabinetList.size
    }

    inner class CabinetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val storageRef = FirebaseStorage.getInstance().reference

        private val artifactImage: ImageView = itemView.findViewById(R.id.image)

        fun bind(cabinet: Cabinet) {
            val viewCabinetBtn: Button = itemView.findViewById(R.id.view_cabinet_button)
            val artifactName: TextView = itemView.findViewById(R.id.name)
            val artifactDescription: TextView = itemView.findViewById(R.id.description)
            artifactName.text = cabinet.name
            artifactDescription.text = cabinet.description
            // Load image from Firebase Storage using FirebaseUI Storage library
            val storagePath = "images/${cabinet.imageName}"
            val imageRef: StorageReference = storageRef.child(storagePath)
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(artifactImage)
            }
            viewCabinetBtn.setOnClickListener(View.OnClickListener {
                cabinet.getCabinetID { cabinetID ->
                    if (cabinetID != null) {
                        val intent = Intent(itemView.context, CabinetDetailActivity::class.java)
                        intent.putExtra("cabinet_id", cabinetID)
                        itemView.context.startActivity(intent)
                    }
                }
            })
        }

    }
}











