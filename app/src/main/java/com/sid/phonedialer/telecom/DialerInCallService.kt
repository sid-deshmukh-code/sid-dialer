package com.sid.phonedialer.telecom

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService
import com.sid.phonedialer.incall.InCallActivity

class DialerInCallService : InCallService() {

    private lateinit var recordingManager: CallRecordingManager

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            when (state) {
                Call.STATE_ACTIVE -> {
                    val number = call.details?.handle?.schemeSpecificPart ?: ""
                    recordingManager.startRecording(number)
                }
                Call.STATE_DISCONNECTED -> {
                    recordingManager.stopRecording()
                    CallHolder.setCall(null)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        recordingManager = CallRecordingManager(applicationContext)
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        call.registerCallback(callCallback)
        CallHolder.setCall(call)

        val intent = Intent(this, InCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        call.unregisterCallback(callCallback)
        recordingManager.stopRecording()
        if (CallHolder.activeCall == call) {
            CallHolder.setCall(null)
        }
    }
}
