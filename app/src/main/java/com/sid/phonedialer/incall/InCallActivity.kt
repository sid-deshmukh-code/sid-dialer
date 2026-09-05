package com.sid.phonedialer.incall

import android.media.AudioManager
import android.os.Bundle
import android.telecom.Call
import androidx.appcompat.app.AppCompatActivity
import com.sid.phonedialer.databinding.ActivityIncallBinding
import com.sid.phonedialer.telecom.CallHolder
import com.sid.phonedialer.telecom.CallRecordingManager

class InCallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIncallBinding
    private var audioManager: AudioManager? = null
    private lateinit var recordingManager: CallRecordingManager

    private val callListener: (Call?) -> Unit = { call -> refreshForCall(call) }

    private val callStateCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            runOnUiThread { updateUiForState(call, state) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        recordingManager = CallRecordingManager(applicationContext)

        CallHolder.addListener(callListener)
        refreshForCall(CallHolder.activeCall)

        binding.btnAnswer.setOnClickListener { CallHolder.activeCall?.answer(0) }
        binding.btnDecline.setOnClickListener { CallHolder.activeCall?.reject(false, null) }
        binding.btnHangup.setOnClickListener { CallHolder.activeCall?.disconnect() }

        binding.btnMute.setOnCheckedChangeListener { _, checked ->
            audioManager?.isMicrophoneMute = checked
        }
        binding.btnSpeaker.setOnCheckedChangeListener { _, checked ->
            audioManager?.isSpeakerphoneOn = checked
        }
    }

    private fun refreshForCall(call: Call?) {
        call?.registerCallback(callStateCallback)
        val number = call?.details?.handle?.schemeSpecificPart ?: "Unknown"
        binding.tvCallerNumber.text = number
        if (call != null) {
            updateUiForState(call, call.state)
        } else {
            finish()
        }
    }

    private fun updateUiForState(call: Call, state: Int) {
        when (state) {
            Call.STATE_RINGING -> {
                binding.tvCallStatus.text = "Incoming call"
                binding.layoutIncomingActions.visibility = android.view.View.VISIBLE
                binding.layoutActiveActions.visibility = android.view.View.GONE
                binding.tvRecordingIndicator.visibility = android.view.View.GONE
            }
            Call.STATE_DIALING -> {
                binding.tvCallStatus.text = "Dialing..."
                binding.layoutIncomingActions.visibility = android.view.View.GONE
                binding.layoutActiveActions.visibility = android.view.View.VISIBLE
            }
            Call.STATE_ACTIVE -> {
                binding.tvCallStatus.text = "In call"
                binding.layoutIncomingActions.visibility = android.view.View.GONE
                binding.layoutActiveActions.visibility = android.view.View.VISIBLE
                binding.tvRecordingIndicator.visibility =
                    if (recordingManager.isEnabled()) android.view.View.VISIBLE else android.view.View.GONE
            }
            Call.STATE_DISCONNECTED -> {
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CallHolder.removeListener(callListener)
        CallHolder.activeCall?.unregisterCallback(callStateCallback)
    }
}
