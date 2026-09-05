package com.sid.phonedialer.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sid.phonedialer.adapters.CallLogAdapter
import com.sid.phonedialer.databinding.FragmentRecentsBinding
import com.sid.phonedialer.models.CallLogEntry

class RecentsFragment : Fragment() {

    private var _binding: FragmentRecentsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadCallLog()
    }

    private fun loadCallLog() {
        val ctx = requireContext()
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED
        ) {
            binding.tvEmptyRecents.visibility = View.VISIBLE
            binding.recyclerRecents.visibility = View.GONE
            return
        }

        val entries = mutableListOf<CallLogEntry>()
        val projection = arrayOf(
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.NUMBER,
            CallLog.Calls.DATE,
            CallLog.Calls.TYPE
        )
        ctx.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )?.use { cursor ->
            val nameIdx = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val numberIdx = cursor.getColumnIndex(CallLog.Calls.NUMBER)
            val dateIdx = cursor.getColumnIndex(CallLog.Calls.DATE)
            val typeIdx = cursor.getColumnIndex(CallLog.Calls.TYPE)

            while (cursor.moveToNext() && entries.size < 200) {
                entries.add(
                    CallLogEntry(
                        name = cursor.getString(nameIdx) ?: "",
                        number = cursor.getString(numberIdx) ?: "",
                        date = cursor.getLong(dateIdx),
                        type = cursor.getInt(typeIdx)
                    )
                )
            }
        }

        binding.tvEmptyRecents.visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerRecents.visibility = if (entries.isEmpty()) View.GONE else View.VISIBLE
        binding.recyclerRecents.layoutManager = LinearLayoutManager(ctx)
        binding.recyclerRecents.adapter = CallLogAdapter(entries) { number -> makeCall(number) }
    }

    private fun makeCall(number: String) {
        val ctx = requireContext()
        val intent = if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        } else {
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
