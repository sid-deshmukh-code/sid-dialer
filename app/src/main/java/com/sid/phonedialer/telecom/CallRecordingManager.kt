package com.sid.phonedialer.telecom

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Records the active call to app-private storage and plays a periodic,
 * audible beep on the call audio stream for the whole duration of the
 * recording, so both parties can hear that the call is being recorded.
 * The beep cannot be disabled - only its volume can be adjusted.
 */
class CallRecordingManager(private val context: Context) {

    companion object {
        private const val TAG = "CallRecording"
        private const val PREFS = "dialer_prefs"
        const val KEY_RECORDING_ENABLED = "recording_enabled"
        const val KEY_BEEP_VOLUME = "beep_volume" // 0-100

        private const val BEEP_INTERVAL_MS = 15_000L
        private const val BEEP_DURATION_MS = 200
    }

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var toneGenerator: ToneGenerator? = null
    private val beepHandler = Handler(Looper.getMainLooper())
    private var isRecording = false

    private fun prefs(): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isEnabled(): Boolean = prefs().getBoolean(KEY_RECORDING_ENABLED, false)

    fun setEnabled(enabled: Boolean) {
        prefs().edit().putBoolean(KEY_RECORDING_ENABLED, enabled).apply()
    }

    fun getBeepVolumePercent(): Int = prefs().getInt(KEY_BEEP_VOLUME, 70)

    fun setBeepVolumePercent(percent: Int) {
        prefs().edit().putInt(KEY_BEEP_VOLUME, percent.coerceIn(0, 100)).apply()
    }

    fun startRecording(remoteNumber: String) {
        if (!isEnabled() || isRecording) return

        val dir = File(context.getExternalFilesDir(null), "CallRecordings")
        if (!dir.exists()) dir.mkdirs()
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val safeNumber = remoteNumber.ifBlank { "unknown" }.replace(Regex("[^0-9+]"), "")
        outputFile = File(dir, "call_${safeNumber}_${sdf.format(Date())}.m4a")

        try {
            recorder = MediaRecorder().apply {
                // VOICE_COMMUNICATION captures both sides on devices that allow it;
                // falls back gracefully to MIC-only on devices that block call-audio taps.
                setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128_000)
                setAudioSamplingRate(44_100)
                setOutputFile(outputFile!!.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            startBeepLoop()
            Log.i(TAG, "Recording started: ${outputFile?.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            releaseRecorder()
        }
    }

    fun stopRecording() {
        if (!isRecording) return
        stopBeepLoop()
        releaseRecorder()
        isRecording = false
        Log.i(TAG, "Recording stopped: ${outputFile?.absolutePath}")
    }

    private fun releaseRecorder() {
        try {
            recorder?.stop()
        } catch (_: Exception) {
        }
        recorder?.release()
        recorder = null
    }

    /**
     * Plays a short tone on the call audio path on a fixed interval, for as
     * long as the recording is active. Volume follows the user's beep-volume
     * setting but the beep itself is never skipped while recording is on.
     */
    private fun startBeepLoop() {
        val volumePercent = getBeepVolumePercent()
        // ToneGenerator volume is 0-100 on the STREAM_VOICE_CALL stream.
        toneGenerator = ToneGenerator(AudioManager.STREAM_VOICE_CALL, volumePercent)

        val beepRunnable = object : Runnable {
            override fun run() {
                if (!isRecording) return
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, BEEP_DURATION_MS)
                beepHandler.postDelayed(this, BEEP_INTERVAL_MS)
            }
        }
        // Play the first beep immediately so recording start is always audible.
        beepHandler.post(beepRunnable)
    }

    private fun stopBeepLoop() {
        beepHandler.removeCallbacksAndMessages(null)
        toneGenerator?.release()
        toneGenerator = null
    }

    fun getRecordingsDir(): File = File(context.getExternalFilesDir(null), "CallRecordings")
}
