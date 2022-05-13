package com.example.eggdeleiverapp.fragment.adapter

import android.location.Address
import java.util.*

data class HistoryItem(
    val addressName:String,
    val name: String,
    val date: Long,
    val address: Address?,
    val phone: String,
    val count: Int,
    var delivered:String,
    var seen: String
)