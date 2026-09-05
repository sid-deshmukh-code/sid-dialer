package com.sid.phonedialer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sid.phonedialer.databinding.ItemContactBinding
import com.sid.phonedialer.models.Contact

class ContactsAdapter(
    private val contacts: List<Contact>,
    private val onCallClick: (String) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.binding.tvContactName.text = contact.name
        holder.binding.tvContactNumber.text = contact.number
        holder.binding.tvInitial.text = contact.name.firstOrNull()?.uppercase() ?: "#"

        holder.binding.btnContactCall.setOnClickListener { onCallClick(contact.number) }
        holder.binding.root.setOnClickListener { onCallClick(contact.number) }
    }

    override fun getItemCount() = contacts.size
}
