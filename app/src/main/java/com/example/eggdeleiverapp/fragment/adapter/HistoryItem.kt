package com.example.eggdeleiverapp.fragment.adapter

data class HistoryItem(
    val addressName: String?,
    val name: String?,
    val date: Long?,
    val phone: String?,
    val count: Int?,
    var delivered: String?,
    var seen: String?,
)