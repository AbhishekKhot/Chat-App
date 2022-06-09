package com.example.chatapp

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(val context: Activity, val messageList:MutableList<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    inner class MessageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}