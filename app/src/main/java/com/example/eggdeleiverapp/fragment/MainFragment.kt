package com.example.eggdeleiverapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.example.eggdeleiverapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : Fragment() {

    private lateinit var topAnim: Animation
    private lateinit var mAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_main, container, false)
        ini(view)
        return view
    }

    private fun ini(view: View) {
        mAuth = FirebaseAuth.getInstance()
        topAnim = AnimationUtils.loadAnimation(context, R.anim.top_animation)
        view.inout_order.animation = topAnim

        view.log_out.setOnClickListener {
            mAuth.signOut()
        }

    }
}