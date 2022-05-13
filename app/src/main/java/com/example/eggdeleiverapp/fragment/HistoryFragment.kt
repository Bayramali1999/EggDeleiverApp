package com.example.eggdeleiverapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eggdeleiverapp.R
import com.example.eggdeleiverapp.common.Constant.HISTORY
import com.example.eggdeleiverapp.fragment.adapter.HistoryAdapter
import com.example.eggdeleiverapp.fragment.adapter.HistoryItem
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_history.view.*

class HistoryFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var adapter: HistoryAdapter
    private val mDatabase = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mAuth = FirebaseAuth.getInstance()
        //todo add right to left animation

        val query = mDatabase
            .child(HISTORY)
            .child(mAuth.currentUser!!.uid)

        val options: FirebaseRecyclerOptions<HistoryItem> =
            FirebaseRecyclerOptions.Builder<HistoryItem>()
                .setQuery(query, HistoryItem::class.java)
                .build()

        adapter = HistoryAdapter(options)

        val view = inflater.inflate(R.layout.fragment_history, container, false)
        view.rv_history.adapter = adapter
        return view
    }
}