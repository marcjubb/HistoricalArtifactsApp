package com.example.historicalartifactsapp

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(private val notificationList: List<com.example.historicalartifactsapp.Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.notification_recyclerview_row, parent, false)
        return NotificationViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]

        holder.notificationMessage.text = "Update on: " + notification.artifactId

    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clearnotification: Button
        var notificationMessage: TextView

        init {
            clearnotification = itemView.findViewById(R.id.clear_notification_btn)
            notificationMessage = itemView.findViewById(R.id.notification_text)
        }
    }
}
