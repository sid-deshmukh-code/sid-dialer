package com.sid.phonedialer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

/**
 * Listens for incoming/outgoing call state changes.
 * Extend this to show a custom in-call UI, log calls, or trigger call screening.
 */
class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        Log.d("CallReceiver", "Phone state changed: $state")
    }
}
