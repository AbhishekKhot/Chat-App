package com.example.chatapp.ui

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
import com.example.chatapp.R
import com.example.chatapp.model.UserDetails
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.chat_item.view.*

class MainActivity : AppCompatActivity() {
    private lateinit var userAdapter: FirestoreRecyclerAdapter<UserDetails, NoteViewHolder>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var user_name: String
    private lateinit var user_image: String
    private lateinit var user_id: String
    private lateinit var user_status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val query = firestore.collection("Users").whereNotEqualTo("uid", auth.uid)
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
                user_name = userDetail.name.toString()
                user_id = userDetail.uid.toString()
                user_image = userDetail.image.toString()
                user_status = userDetail.status.toString()


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

                holder.itemView.CircleImageViewChat.setOnClickListener {
                    val intent = Intent(this@MainActivity, UserImageActivity::class.java)
                    intent.putExtra("userimage", userDetail.image)
                    intent.putExtra("username", userDetail.name)
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
        firestore.collection("Users").document(auth.uid.toString()).update("status","Online")
        userAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        firestore.collection("Users").document(auth.uid.toString()).update("status","Offline")
        userAdapter.stopListening()
    }
}