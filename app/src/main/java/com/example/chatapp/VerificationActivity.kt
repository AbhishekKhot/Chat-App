package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_verification.*

class VerificationActivity : AppCompatActivity() {
    private var firebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        TextViewChangeNumber.setOnClickListener {
            val intent = Intent(this@VerificationActivity, MobileNumberActivity::class.java)
            startActivity(intent)
            finish()
        }

        ButtonVerify.setOnClickListener {
            val OTP = EditTextOTP.text.toString()
            if (OTP.isEmpty()) {
                Toast.makeText(this, "Please enter provided OTP", Toast.LENGTH_SHORT).show()
            } else {
                ProgressBarVerification.visibility = View.VISIBLE
                val codeReceived = intent.getStringExtra("otp")
                val credential = PhoneAuthProvider.getCredential(codeReceived!!, OTP)
                signInWithPhoneAuthCredential(credential)
            }
        }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                ProgressBarVerification.visibility = View.INVISIBLE
                Toast.makeText(this, "Successfully login", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProfileSetupActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                ProgressBarVerification.visibility = View.INVISIBLE
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}