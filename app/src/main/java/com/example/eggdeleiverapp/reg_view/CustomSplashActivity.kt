package com.example.eggdeleiverapp.reg_view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.eggdeleiverapp.MainActivity
import com.example.eggdeleiverapp.R
import kotlinx.android.synthetic.main.activity_splash.*

@SuppressLint("CustomSplashScreen")
class CustomSplashActivity : AppCompatActivity() {

    private lateinit var topAnim: Animation
    private lateinit var bottomAnim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_splash)

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)


        splash_iv.animation = topAnim
        textView2.animation = bottomAnim
        textView.animation = bottomAnim


        Handler(Looper.myLooper()!!).postDelayed({
            val intent = Intent(this@CustomSplashActivity, MainActivity::class.java)
// how to add this figure
//            val pairs = arrayOf(
//                androidx.core.util.Pair(splash_iv, "logo_image"),
//                androidx.core.util.Pair(textView2, "logo_text")
//            )
//
//
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                this@CustomSplashActivity,
//                androidx.core.util.Pair.create(splash_iv, "logo_image")
//            )

            startActivity(intent)
            finish()
        }, 4000L)
    }


}