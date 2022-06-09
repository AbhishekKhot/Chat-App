package com.example.chatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.receiver_item.view.*
import kotlinx.android.synthetic.main.sender_item.view.*


class MessageAdapter(val messageList:MutableList<Message>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val SENDER_VIEW=1
    val RECEIVER_VIEW=2

    inner class SenderViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    inner class ReceiverViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==SENDER_VIEW){
            return SenderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sender_item,parent,false))
        }
        else{
            return ReceiverViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.receiver_item,parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.javaClass==SenderViewHolder::class.java){
            val sender_holder = holder as SenderViewHolder
            sender_holder.itemView.TextViewMessageSender.text=messageList[position].message
            sender_holder.itemView.TextViewMessageTimeSender.text=messageList[position].currentTime
        }
        else{
            val receiver_holder = holder as ReceiverViewHolder
            receiver_holder.itemView.TextViewMessageReciever.text=messageList[position].message
            receiver_holder.itemView.TextViewMessageTimeReciever.text=messageList[position].currentTime
        }
    }

    override fun getItemViewType(position: Int): Int {
       if(FirebaseAuth.getInstance().currentUser?.uid.equals(messageList[position].senderId)){
           return SENDER_VIEW
       }
       else{
           return RECEIVER_VIEW
       }
    }

    override fun getItemCount(): Int {
       return messageList.size
    }
}