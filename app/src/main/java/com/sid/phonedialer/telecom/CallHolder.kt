package com.sid.phonedialer.telecom

import android.telecom.Call

/**
 * Bridges the active telecom.Call object between DialerInCallService
 * (which Android hands calls to) and InCallActivity (which shows the UI).
 */
object CallHolder {
    var activeCall: Call? = null
    val listeners = mutableListOf<(Call?) -> Unit>()

    fun setCall(call: Call?) {
        activeCall = call
        listeners.forEach { it(call) }
    }

    fun addListener(listener: (Call?) -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: (Call?) -> Unit) {
        listeners.remove(listener)
    }
}
