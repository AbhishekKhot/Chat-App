package com.example.chatapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import kotlinx.android.synthetic.main.activity_mobile_number.*
import java.util.concurrent.TimeUnit

class MobileNumberActivity : AppCompatActivity() {
    private lateinit var mCallbacks: OnVerificationStateChangedCallbacks
    private var firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var countryCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_number)
        supportActionBar!!.hide()
        countryCode = CountryCodePicker.selectedCountryCodeWithPlus

        CountryCodePicker.setOnCountryChangeListener {
            countryCode = CountryCodePicker.selectedCountryCodeWithPlus
        }

        ButtonSendOTP.setOnClickListener {
            val number = EditTextMobileNumber.text.toString()
            if (number.isEmpty()) {
                Toast.makeText(applicationContext,
                    "Please enter your mobile number",
                    Toast.LENGTH_SHORT).show()
            } else if (number.length < 10) {
                Toast.makeText(applicationContext,
                    "Please enter valid mobile  number",
                    Toast.LENGTH_SHORT).show()
            } else {
                ProgressBarMobile.visibility = View.VISIBLE
                val phoneNumber = countryCode + number
                val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(mCallbacks)
                    .build()

                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {}

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                Toast.makeText(applicationContext, "OTP is sent", Toast.LENGTH_SHORT).show()
                ProgressBarMobile.visibility = View.INVISIBLE
                val codeSent = s
                val intent = Intent(this@MobileNumberActivity, VerificationActivity::class.java)
                intent.putExtra("otp", codeSent)
                startActivity(intent)
                finish()
            }
        }
    }
}