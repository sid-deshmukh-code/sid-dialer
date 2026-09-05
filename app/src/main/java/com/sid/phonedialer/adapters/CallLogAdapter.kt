package com.sid.phonedialer.adapters

import android.provider.CallLog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sid.phonedialer.databinding.ItemCallLogBinding
import com.sid.phonedialer.models.CallLogEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CallLogAdapter(
    private val entries: List<CallLogEntry>,
    private val onCallClick: (String) -> Unit
) : RecyclerView.Adapter<CallLogAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCallLogBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCallLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.binding.tvName.text = entry.name.ifBlank { entry.number }
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        holder.binding.tvDate.text = sdf.format(Date(entry.date))

        val icon = when (entry.type) {
            CallLog.Calls.MISSED_TYPE -> android.R.drawable.sym_call_missed
            CallLog.Calls.OUTGOING_TYPE -> android.R.drawable.sym_action_call
            else -> android.R.drawable.sym_action_call
        }
        holder.binding.ivCallType.setImageResource(icon)

        holder.binding.btnCallBack.setOnClickListener { onCallClick(entry.number) }
        holder.binding.root.setOnClickListener { onCallClick(entry.number) }
    }

    override fun getItemCount() = entries.size
}
