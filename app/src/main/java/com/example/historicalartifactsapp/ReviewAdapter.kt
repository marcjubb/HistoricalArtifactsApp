package com.example.historicalartifactsapp


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView


class ReviewAdapter(private val reviewList: List<Review>) :

    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.review_recyclerview_row, parent, false)
        return ReviewViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviewList[position]
        holder.artifact_name.text = review.artifactName
        holder.status.text = review.status


        holder.openReview.setOnClickListener(View.OnClickListener {
            review.getID { reviewID ->
                if (reviewID != null) {
                    val intent = Intent(holder.itemView.context, ReviewDetailActivity::class.java)
                    intent.putExtra("review_id", reviewID)
                    holder.itemView.context.startActivity(intent)
                }
            }
        })

    }



    override fun getItemCount(): Int {
        return reviewList.size
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val openReview: Button
        var status: TextView
        var artifact_name: TextView

        init {
            openReview = itemView.findViewById(R.id.view_review_button)
            status = itemView.findViewById(R.id.status)
            artifact_name = itemView.findViewById(R.id.artifact_name)

        }
    }
}
