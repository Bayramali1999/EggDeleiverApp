package com.example.eggdeleiverapp.reg_view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eggdeleiverapp.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    val MY_PREFS_NAME = "MyPrefsFile"
    private var phone: String = ""
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var topAnim: Animation
    private lateinit var bottomAnim: Animation
    private lateinit var mVerificationId: String
    private lateinit var mResendToken: ForceResendingToken
    private val mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onCodeSent(p0: String, p1: ForceResendingToken) {
            super.onCodeSent(p0, p1)
            mVerificationId = p0
            mResendToken = p1
        }

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential);
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Toast.makeText(this@LoginActivity, "Something gets wrong", Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        login_top.animation = topAnim
        login_form.animation = bottomAnim


        login.setOnClickListener {
            if (TextUtils.isEmpty(et_username.text) || et_username.text!!.length == 12) {
                Toast.makeText(this@LoginActivity, "Telefon raqamni tekshiring", Toast.LENGTH_LONG)
                    .show()
            } else {
                phone = et_username.text.toString()
                checkView()
                sendPhoneNumber()
            }
        }

        login_sms.setOnClickListener {
            checkSms()
        }
    }

    private fun checkSms() {
        val text: String = tv_password.text.toString()
        if (!TextUtils.isEmpty(text)) {
            val credential = PhoneAuthProvider.getCredential(mVerificationId, text)
            signInWithPhoneAuthCredential(credential)
        } else {
            Toast.makeText(this, "Parolni kiriting", Toast.LENGTH_LONG).show()
        }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
//                    val user: FirebaseUser = task.getResult(FirebaseAuth::class).user
                    gotoMainActivity()
                } else {

                }
            }
    }

    private fun sendPhoneNumber() {
        val options =
            PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)       // Phone number to verify
                .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private fun checkView() {
        til_username.visibility = View.GONE
        login.visibility = View.GONE

        til_password.visibility = View.VISIBLE
        login_sms.visibility = View.VISIBLE
    }

    private fun gotoMainActivity() {
        val intent = Intent(this, SettingActivity::class.java)
        startActivity(intent)
        finish()
    }
}