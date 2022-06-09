package com.example.chatapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.chatapp.R
import kotlinx.android.synthetic.main.activity_user_image.*

class UserImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_image)
        supportActionBar!!.hide()
        val image = intent.getStringExtra("userimage")
        val name = intent.getStringExtra("username")

        ButtonBackUserImage.setOnClickListener { finish() }
        TextViewUserNameImage.text=name
        Glide.with(this).load(image).into(CircleImageViewUserImage)
    }
}