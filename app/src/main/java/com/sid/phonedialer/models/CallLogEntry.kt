package com.sid.phonedialer.models

data class CallLogEntry(
    val name: String,
    val number: String,
    val date: Long,
    val type: Int // CallLog.Calls.INCOMING_TYPE / OUTGOING_TYPE / MISSED_TYPE
)
