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
import com.example.eggdeleiverapp.common.Constant.LOCATION_REQ_CODE
import com.example.eggdeleiverapp.common.Constant.ORDER
import com.example.eggdeleiverapp.reg_view.LoginActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.util.*

class MainFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var hasMyLoc = false
    private lateinit var myLoc: Location
    private lateinit var topAnim: Animation
    private lateinit var dialog: AlertDialog
    private val locationService by lazy(LazyThreadSafetyMode.NONE) {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }
    private lateinit var address: String
    private lateinit var addresses: List<Address>

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (!hasMyLoc) {
                hasMyLoc = true
                myLoc = result.lastLocation

                val geocoder = Geocoder(context, Locale.getDefault())

                addresses = geocoder.getFromLocation(
                    myLoc.latitude,
                    myLoc.longitude,
                    1
                )

                address = addresses[0].getAddressLine(0)
                location.text = address
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_main, container, false)
        ini(view)
        return view
    }

    private fun ini(view: View) {
        viewAvailability(view)

        dialog = AlertDialog.Builder(requireActivity()).setTitle("Ilovadan chiqish")
            .setPositiveButton("Xa") { _, _ ->
                mAuth.signOut()
                openLoginActivity()
            }.setNegativeButton("Yo'q") { a, _ -> { a.dismiss() } }.create()

        mAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        topAnim = AnimationUtils.loadAnimation(context, R.anim.top_animation)
        view.inout_order.animation = topAnim


        view.log_out.setOnClickListener {
            dialog.show()
        }
        view.ordered.setOnClickListener {

            databaseReference.child(ORDER)
                .child(mAuth.currentUser.toString())
//       todo unique         .
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

        view.ordered.isEnabled = false
    }

    private fun openLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onResume() {
        super.onResume()

        location.setOnClickListener {
            checkLocation()
            ordered.isEnabled = true
        }
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
}