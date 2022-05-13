package com.example.eggdeleiverapp.fragment.adapter

import android.location.Address
import java.util.*

data class HistoryItem(
    val name: String,
    val date: Date,
    val address: Address?,
    val phone: String,
    val count: Int,
    var delivered: Boolean = false,
    var seen: Boolean = false
)