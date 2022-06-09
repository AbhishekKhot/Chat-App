package com.example.chatapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var textMessage: String
    private lateinit var receiverName: String
    private lateinit var receiverUid: String
    private lateinit var senderUid: String
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    private val auth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar!!.hide()

        var messageList = mutableListOf<Message>()

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        messageAdapter = MessageAdapter(messageList)

        RecyclerViewPersonChat.let {
            it.layoutManager = linearLayoutManager
            it.adapter = messageAdapter
        }


        senderUid = auth.uid.toString()
        receiverUid = intent.getStringExtra("receiverUid").toString()
        receiverName = intent.getStringExtra("name").toString()

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid


        val databaseReference: DatabaseReference =
            firebaseDatabase.reference.child("chats").child(senderRoom).child("messages")
        messageAdapter = MessageAdapter( messageList)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (snapshot1 in snapshot.children) {
                    val message = snapshot1.getValue(Message::class.java)
                    message?.let {
                        messageList.add(message)
                    }
                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        ButtonBackPersonChat.setOnClickListener { finish() }

        TextViewUserNamePersonChat.text = receiverName


        val firebaseStorage = FirebaseStorage.getInstance()
        val storageReference = firebaseStorage.reference
        storageReference.child("Images").child(receiverUid)
            .child("Profile Pic").downloadUrl.addOnSuccessListener { uri ->
                Glide.with(applicationContext).load(uri)
                    .into(CircleImageViewPersonChat)
            }

        ImageButtonSendPersonChat.setOnClickListener {
            textMessage = EditTextMessagePersonChat.text.toString().trim()
            if (textMessage.isNotEmpty()) {
                val date = Date()
                val calendar = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat("hh:mm a")
                val currentTime = simpleDateFormat.format(calendar.time)
                val message = Message(textMessage, auth.uid, date.time, currentTime)
                firebaseDatabase.reference.child("chats").child(senderRoom).child("messages").push()
                    .setValue(message)
                    .addOnCompleteListener {
                        firebaseDatabase.reference.child("chats").child(receiverRoom)
                            .child("messages").push().setValue(message)
                            .addOnCompleteListener {

                            }
                    }
                EditTextMessagePersonChat.text = null
            } else {
                Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}