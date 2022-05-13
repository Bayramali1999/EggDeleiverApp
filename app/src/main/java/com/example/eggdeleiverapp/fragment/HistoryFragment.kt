package com.example.eggdeleiverapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eggdeleiverapp.R
import com.example.eggdeleiverapp.common.Constant
import com.example.eggdeleiverapp.common.Constant.HISTORY
import com.example.eggdeleiverapp.fragment.adapter.HistoryAdapter
import com.example.eggdeleiverapp.fragment.adapter.HistoryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.fragment_history.view.*

class HistoryFragment : Fragment() {
    private val list = mutableListOf<HistoryItem>()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var adapter: HistoryAdapter
    private val mDatabase = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mAuth = FirebaseAuth.getInstance()
        adapter = HistoryAdapter(list)
        mDatabase.child(HISTORY)
            .child(mAuth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (i in snapshot.children) {
                            if (i.exists()) {
                                val myName = i.child(Constant.name).value
                                val myAddressName = i.child(Constant.addressName).value
                                val myDate = i.child("date").getValue<Long>()
                                val myPhone = i.child(Constant.phone).value
                                val myCount = i.child(Constant.count).getValue<Int>()

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
                                    list.add(myData)
                                }
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


        val view = inflater.inflate(R.layout.fragment_history, container, false)
        view.rv_history.setHasFixedSize(true)
        view.rv_history.adapter = adapter
        return view
    }
}