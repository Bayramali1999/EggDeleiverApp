package com.example.eggdeleiverapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.example.eggdeleiverapp.fragment.HistoryFragment
import com.example.eggdeleiverapp.fragment.MainFragment
import com.example.eggdeleiverapp.reg_view.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        checkHasLogin()

        restAll()

        supportFragmentManager.beginTransaction().replace(R.id.frame_container, MainFragment())
            .commit()
        container_order.setBackgroundColor(resources.getColor(R.color.main_color))
        container_order.setPadding(7)

        openView()
    }

    private fun openView() {
        container_history.setOnClickListener {
            restAll()
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, HistoryFragment()).commit()
            container_history.setBackgroundColor(resources.getColor(R.color.main_color))
            container_history.setPadding(7)
        }

        container_order.setOnClickListener {
            restAll()
            supportFragmentManager.beginTransaction().replace(R.id.frame_container, MainFragment())
                .commit()
            container_order.setBackgroundColor(resources.getColor(R.color.main_color))
            container_order.setPadding(7)

        }
    }

    private fun checkHasLogin() {
        if (auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun restAll() {
        container_history.setBackgroundColor(resources.getColor(R.color.white))
        container_order.setBackgroundColor(resources.getColor(R.color.white))

        container_history.setPadding(15)
        container_order.setPadding(15)
    }
}