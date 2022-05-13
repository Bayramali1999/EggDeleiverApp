package com.example.eggdeleiverapp.fragment.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eggdeleiverapp.R
import kotlinx.android.synthetic.main.item_view.view.*
import java.text.SimpleDateFormat

class HistoryAdapter(
    private val list: MutableList<HistoryItem>
) :
    RecyclerView.Adapter<HistoryAdapter.VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false))
    }

    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list[position])
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun onBind(historyItem: HistoryItem) {
            val sd = SimpleDateFormat("dd.MM.yyyy hh:mm")

            itemView.order_time.text = "Sana: ${sd.format(historyItem.date)}"
            itemView.order_count.text = "Soni: ${historyItem.count}"
            itemView.order_adress.text = "Manzil ${historyItem.addressName}"
        }
    }
}