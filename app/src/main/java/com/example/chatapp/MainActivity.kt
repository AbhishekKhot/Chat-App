package com.example.chatapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.chat_item.view.*

class MainActivity : AppCompatActivity() {
    private lateinit var userAdapter: FirestoreRecyclerAdapter<UserDetails, NoteViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()
        val fireStore = FirebaseFirestore.getInstance()

        val query = fireStore.collection("Users").whereNotEqualTo("uid", auth.uid)
        val allUsersName =
            FirestoreRecyclerOptions.Builder<UserDetails>().setQuery(query, UserDetails::class.java)
                .build()

        userAdapter = object : FirestoreRecyclerAdapter<UserDetails, NoteViewHolder>(allUsersName) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
                return NoteViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_item, parent, false))
            }

            override fun onBindViewHolder(
                holder: NoteViewHolder,
                position: Int,
                userDetail: UserDetails,
            ) {
                holder.itemView.TextViewUserNameChat.text = userDetail.name

                Glide.with(applicationContext).load(userDetail.image)
                    .into(holder.itemView.CircleImageViewChat)

                if (userDetail.status.equals("Online")) {
                    holder.itemView.TextViewStatusChat.text = userDetail.status
                    holder.itemView.TextViewStatusChat.setTextColor(Color.GREEN)
                } else {
                    holder.itemView.TextViewStatusChat.text = userDetail.status
                }

                holder.itemView.setOnClickListener {
                    val intent = Intent(this@MainActivity, ChatActivity::class.java)
                    intent.putExtra("name", userDetail.name)
                    intent.putExtra("receiverUid", userDetail.uid)
                    startActivity(intent)
                }
            }
        }


        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        RecyclerViewMain.let {
            it.setHasFixedSize(true)
            it.layoutManager = linearLayoutManager
            it.adapter = userAdapter
            userAdapter.startListening()
        }
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onStart() {
        super.onStart()
        userAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        userAdapter.stopListening()
    }
}