package com.example.eggdeleiverapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.example.eggdeleiverapp.reg_view.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var phone: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        checkHasLogin()

        restAll()
        container_order.setBackgroundColor(resources.getColor(R.color.main_color))
        order.setPadding(7)
    }

    private fun checkHasLogin() {
        if (auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "already login", Toast.LENGTH_LONG).show()
        }
    }

    private fun restAll() {
        container_history.setBackgroundColor(resources.getColor(R.color.white))
        container_order.setBackgroundColor(resources.getColor(R.color.white))

        history.setPadding(15)
        order.setPadding(15)
    }
}