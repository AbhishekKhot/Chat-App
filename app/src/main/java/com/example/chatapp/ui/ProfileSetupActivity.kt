package com.example.chatapp.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R
import com.example.chatapp.model.User
import com.example.chatapp.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile_setup.*
import java.io.ByteArrayOutputStream
import java.io.IOException


private const val REQUEST_CODE = 0

class ProfileSetupActivity : AppCompatActivity() {
    private var auth = FirebaseAuth.getInstance()
    private var firebaseStorage = FirebaseStorage.getInstance()
    private var storageReference: StorageReference = firebaseStorage.reference
    private var fireStore = FirebaseFirestore.getInstance()
    private lateinit var db:FirebaseDatabase
    private lateinit var imageUri: Uri
    private lateinit var name: String
    private lateinit var ImageUriToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)
        supportActionBar!!.hide()

        db= FirebaseDatabase.getInstance()

        TextViewSetProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE)
        }

        ButtonSaveProfile.setOnClickListener {
            name = EditTextUserNameSetUpProfile.text.toString()
            if (name.isNotEmpty()) {
                ProgressBarSetUpProfile.visibility = android.view.View.VISIBLE
                sendDataForNewUser()
                ProgressBarSetUpProfile.visibility = android.view.View.INVISIBLE
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendDataForNewUser() {
        sendDataToRealTimeDatabase()
    }

    private fun sendDataToRealTimeDatabase() {
        name = EditTextUserNameSetUpProfile.text.toString().trim()
        val databaseReference = db.getReference(auth.uid!!)
        val user = User(name,auth.uid.toString())
        databaseReference.setValue(user)
        Toast.makeText(applicationContext, "Profile saved successfully", Toast.LENGTH_SHORT).show()
        sendImagetoStorage()
    }

    private fun sendImagetoStorage() {
        val imageRef =
            storageReference.child("Images").child(auth.uid.toString()).child("Profile pic")
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                ImageUriToken = uri.toString()
                Toast.makeText(applicationContext, "URI get success", Toast.LENGTH_SHORT).show()
                saveToFirestore()
            }.addOnFailureListener {
                Toast.makeText(applicationContext,
                    "URI get Failed",
                    Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(applicationContext, "Image is uploaded", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext,
                "Image Not uploaded",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToFirestore() {
        val documentReference = fireStore.collection("Users").document(auth.uid.toString())
        val userdata: MutableMap<String, Any?> = HashMap()
        userdata["name"] = name
        userdata["image"] = ImageUriToken
        userdata["uid"] = auth.uid
        userdata["status"] = "Online"
        documentReference.set(userdata).addOnSuccessListener {
            Toast.makeText(applicationContext,
                "User information saved successfully",
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let {
                imageUri = it
                CircleImageViewSetProfile.setImageURI(it)
            }
        }
    }
}