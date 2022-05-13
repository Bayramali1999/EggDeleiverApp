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
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_history.view.*

class HistoryFragment : Fragment() {
    private lateinit var adapter: HistoryAdapter
    private val mDatabase = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //todo add right to left animation

        val query = mDatabase
            .child(HISTORY)
            .limitToLast(50)

        val options: FirebaseRecyclerOptions<HistoryItem> =
            FirebaseRecyclerOptions.Builder<HistoryItem>()
                .setQuery(query, HistoryItem::class.java)
                .build()
        val list =
            mutableListOf(
                HistoryItem(
                    "some where",
                    "ALi",
                    System.currentTimeMillis(),
                    null,
                    "+sdfas",
                    5,
                    "not",
                    "not"
                )
            )
//        adapter = HistoryAdapter(list, options)

        val view = inflater.inflate(R.layout.fragment_history, container, false)
//        view.rv_history.adapter = adapter
        return view
    }
}