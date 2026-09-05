package com.sid.phonedialer

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.sid.phonedialer.databinding.ActivitySettingsBinding
import com.sid.phonedialer.telecom.CallRecordingManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var recordingManager: CallRecordingManager

    private val roleRequest = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { refreshDefaultDialerStatus() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recordingManager = CallRecordingManager(applicationContext)

        binding.btnSetDefault.setOnClickListener { requestDefaultDialer() }

        binding.switchRecording.isChecked = recordingManager.isEnabled()
        binding.switchRecording.setOnCheckedChangeListener { _, checked ->
            recordingManager.setEnabled(checked)
        }

        binding.seekBeepVolume.progress = recordingManager.getBeepVolumePercent()
        binding.seekBeepVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) recordingManager.setBeepVolumePercent(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        refreshDefaultDialerStatus()
    }

    private fun requestDefaultDialer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager.isRoleAvailable(RoleManager.ROLE_DIALER) &&
                !roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
            ) {
                roleRequest.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER))
            }
        } else {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
            roleRequest.launch(intent)
        }
    }

    private fun refreshDefaultDialerStatus() {
        val isDefault = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
        } else {
            val telecomManager = getSystemService(TelecomManager::class.java)
            telecomManager?.defaultDialerPackage == packageName
        }
        binding.tvDefaultStatus.text = if (isDefault) {
            "✓ Sid Dialer is your default phone app"
        } else {
            "Not currently your default phone app"
        }
    }
}
