package com.example.eggdeleiverapp.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.eggdeleiverapp.R
import com.example.eggdeleiverapp.common.Constant
import com.example.eggdeleiverapp.common.Constant.HISTORY
import com.example.eggdeleiverapp.common.Constant.LOCATION_REQ_CODE
import com.example.eggdeleiverapp.common.Constant.ORDER
import com.example.eggdeleiverapp.common.Constant.addressName
import com.example.eggdeleiverapp.common.Constant.phone
import com.example.eggdeleiverapp.fragment.adapter.HistoryItem
import com.example.eggdeleiverapp.reg_view.LoginActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.util.*

class MainFragment : Fragment() {
    private var isView = true
    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var hasMyLoc = false
    private lateinit var myLoc: Location
    private lateinit var topAnim: Animation
    private lateinit var dialog: AlertDialog
    private val locationService by lazy(LazyThreadSafetyMode.NONE) {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }
    private var address: String? = null
    private lateinit var addresses: MutableList<Address>
    private var isLocated = false

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (address == null && !hasMyLoc) {
                hasMyLoc = true
                myLoc = result.lastLocation

                val geocoder = Geocoder(requireContext(), Locale.getDefault())

                addresses = geocoder.getFromLocation(
                    myLoc.latitude,
                    myLoc.longitude,
                    1
                )

                address = addresses[0].getAddressLine(0)
                location.text = address
                isLocated = true
                ordered.isEnabled = isLocated
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_main, container, false)
        ini(view)
        checkView()
        return view
    }

    private fun ini(view: View) {
        mAuth = FirebaseAuth.getInstance()
        viewAvailability(view)
        dialog = AlertDialog.Builder(requireActivity()).setTitle("Ilovadan chiqish")
            .setPositiveButton("Xa") { _, _ ->
                mAuth.signOut()
                openLoginActivity()
            }.setNegativeButton("Yo'q") { a, _ -> { a.dismiss() } }.create()

        databaseReference = FirebaseDatabase.getInstance().reference

        view.log_out.setOnClickListener {
            dialog.show()
        }

        view.ordered.setOnClickListener {
            if (isLocated &&
                !TextUtils.isEmpty(view.phone_number.text.toString()) &&
                !TextUtils.isEmpty(view.name.text.toString()) &&
                view.phone_number.text!!.length == 19 &&
                !TextUtils.isEmpty(view.count.text)
            ) {
                val date = System.currentTimeMillis()
                val item = HistoryItem(
                    address,
                    view.name.text.toString(),
                    date = date,
                    view.phone_number.text.toString(),
                    1000,
                    "yoq",
                    "yoq"
                )

                databaseReference.child(ORDER)
                    .child(mAuth.currentUser!!.uid)
                    .setValue(item)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            view.inout_order.visibility = View.GONE
                            view.canceling.visibility = View.VISIBLE
                        }
                    }.addOnFailureListener {

                    }


            } else {
                Toast.makeText(requireContext(), "Ma'lumotlarni tekshiring", Toast.LENGTH_LONG)
                    .show()
            }
        }

        view.get_it.setOnClickListener {
            databaseReference.child(ORDER)
                .child(mAuth.currentUser!!.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val myName = snapshot.child(Constant.name).value
                            val myAddressName = snapshot.child(addressName).value
                            val myDate = snapshot.child("date").getValue<Long>()
                            val myPhone = snapshot.child(phone).value
                            val myCount = snapshot.child(Constant.count).getValue<Int>()

                            if (myAddressName != null &&
                                myName != null &&
                                myPhone != null
                            ) {
                                val myData = HistoryItem(
                                    myAddressName as String,
                                    myName as String,
                                    myDate,
                                    myPhone as String,
                                    myCount,
                                    "xa",
                                    "xa"
                                )

                                databaseReference.child(HISTORY)
                                    .child(mAuth.currentUser!!.uid)
                                    .child(myDate.toString())
                                    .setValue(myData)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {

                                            removeFromOrderValue()

                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            requireContext(),
                                            "Qaytadan urinib ko'ring",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("TAG", "onDataChange: $error")
                    }
                })
        }

        view.cancel.setOnClickListener {
            removeFromOrderValue()
        }
    }

    private fun viewReset() {
        count.setText("")
        name.setText("")
        phone_number.setText("")
        ordered.isEnabled = false
        address = null
        hasMyLoc = true
        isLocated = false
        addresses.clear()
        location.text = "Manzi"

        canceling.visibility = View.GONE
        inout_order.visibility = View.VISIBLE

    }

    private fun removeFromOrderValue() {
        databaseReference.child(ORDER)
            .child(mAuth.currentUser!!.uid)
            .removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    viewReset()
                }
            }
    }

    private fun viewAvailability(view: View) {

        val affineFormats: MutableList<String> = ArrayList()
        affineFormats.add("+998 ([00]) [000]-[00]-[00]")

        val listener = MaskedTextChangedListener.Companion.installOn(
            view.phone_number,
            "+998 ([00]) [000]-[00]-[00]",
            affineFormats,
            AffinityCalculationStrategy.PREFIX
        )
        view.phone_number.hint = listener.placeholder()

        view.ordered.isEnabled = isLocated
    }

    private fun openLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onResume() {
        super.onResume()

        location.setOnClickListener {
            hasMyLoc = false
            checkLocation()
        }
    }

    private fun checkView() {
        databaseReference.child(ORDER)
            .child(mAuth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isView) {
                        if (snapshot.exists()) {
                            canceling.visibility = View.VISIBLE
                        } else {
                            topAnim = AnimationUtils.loadAnimation(context, R.anim.top_animation)
                            inout_order.animation = topAnim
                            inout_order.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Internetda muammo", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun checkLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQ_CODE
                )
            } else {
                checkLocationAvailability()
            }
        } else {
            checkLocationAvailability()
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkLocationAvailability() {
        locationService.locationAvailability
            .addOnSuccessListener {
                if (it.isLocationAvailable) {
                    requestLocation()
                } else {
                    setUpLocationRequest()
                }
            }
            .addOnFailureListener { setUpLocationRequest() }
    }

    private fun setUpLocationRequest() {
        val req = createLocationReq()
        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(req)
            .build()

        LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(settingsRequest)
            .addOnSuccessListener {
                requestLocation()
            }
            .addOnFailureListener { resolvableRequestException(it) }
    }

    private fun resolvableRequestException(it: Exception) {
        if (it is ResolvableApiException) {
            try {
                it.startResolutionForResult(requireActivity(), 1002)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        locationService.requestLocationUpdates(createLocationReq(), callback, Looper.myLooper()!!)
    }

    private fun createLocationReq() = LocationRequest().apply {
        interval = 10_000
        fastestInterval = 5_000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_REQ_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationAvailability()
            }
        } else {
            Toast.makeText(requireContext(), "Give me permission Pleace", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStop() {
        super.onStop()
        isView = false
    }
}