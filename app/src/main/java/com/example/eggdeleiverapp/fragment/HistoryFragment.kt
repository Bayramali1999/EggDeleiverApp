package com.example.eggdeleiverapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eggdeleiverapp.R
import com.example.eggdeleiverapp.fragment.adapter.HistoryAdapter
import com.example.eggdeleiverapp.fragment.adapter.HistoryItem
import kotlinx.android.synthetic.main.fragment_history.view.*
import java.util.*

class HistoryFragment : Fragment() {
    private lateinit var adapter: HistoryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //todo add right to left animation

        val list =
            mutableListOf(HistoryItem("ALi", Date(System.currentTimeMillis()), null, "+sdfas", 5))
        adapter = HistoryAdapter(list, context)
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        view.rv_history.adapter = adapter
        return view
    }
}