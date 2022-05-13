package com.example.eggdeleiverapp.reg_view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eggdeleiverapp.MainActivity
import com.example.eggdeleiverapp.R
import com.example.eggdeleiverapp.common.Constant.PROFILE_IMAGE
import com.example.eggdeleiverapp.common.Constant.REQ_GALLERY_CODE
import com.example.eggdeleiverapp.common.Constant.USERS
import com.example.eggdeleiverapp.common.Constant.image
import com.example.eggdeleiverapp.common.Constant.name
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_setting.*


class SettingActivity : AppCompatActivity() {
    private lateinit var topAnim: Animation
    private lateinit var bottomAnim: Animation
    private lateinit var uid: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference
    private val progressDialog = ProgressDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        ini()

    }

    private fun ini() {
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        setting_img.animation = topAnim
        setting_form.animation = bottomAnim
        progressDialog.setTitle("Loading")
        mAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child(PROFILE_IMAGE)
        databaseReference = FirebaseDatabase.getInstance().reference
        uid = mAuth.currentUser!!.uid

        databaseReference.child(USERS)
            .child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.hasChild(name))
                            et_name.setText(snapshot.child(name).value.toString())
                        if (snapshot.hasChild(image))
                            Glide.with(this@SettingActivity)
                                .load(snapshot.child(image).value.toString()).into(profile_image)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


        profile_image.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, REQ_GALLERY_CODE)
        }


        save.setOnClickListener {
            databaseReference.child(USERS)
                .child(uid)
                .child(name)
                .setValue(et_name.text.toString())
                .addOnCompleteListener {
                    val intent = Intent(this@SettingActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val uri = data!!.data
        val sReference = storageReference.child("$uid.jpg")
        progressDialog.show()
        sReference.putFile(uri!!).addOnSuccessListener {
            it.metadata!!
                .reference!!
                .downloadUrl
                .addOnSuccessListener { uriUrl ->
                    val downloadURl = uriUrl.toString()
                    databaseReference.child(USERS)
                        .child(uid)
                        .child("image")
                        .setValue(downloadURl)
                        .addOnCompleteListener {
                            Glide.with(this@SettingActivity)
                                .load(downloadURl)
                                .into(profile_image)
                            progressDialog.cancel()
                        }

                }

        }
    }
}