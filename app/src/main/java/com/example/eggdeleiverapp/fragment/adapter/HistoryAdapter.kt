package com.example.eggdeleiverapp.fragment.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eggdeleiverapp.R
import kotlinx.android.synthetic.main.item_view.view.*
import java.text.SimpleDateFormat

class HistoryAdapter(private val list: MutableList<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount() = list.size

    class VH(private val itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SimpleDateFormat")
        fun onBind(historyItem: HistoryItem) {
            val sd = SimpleDateFormat("dd.MM.yyyy hh:mm")

            itemView.order_time.text = sd.format(historyItem.date).toString()
            itemView.order_adress.text = historyItem.address.countryName
            itemView.order_count.text = historyItem.count.toString()
        }
    }

}