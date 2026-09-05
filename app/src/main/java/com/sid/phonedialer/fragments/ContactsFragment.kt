package com.sid.phonedialer.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sid.phonedialer.adapters.ContactsAdapter
import com.sid.phonedialer.databinding.FragmentContactsBinding
import com.sid.phonedialer.models.Contact

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadContacts()
    }

    private fun loadContacts() {
        val ctx = requireContext()
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            binding.tvEmptyContacts.visibility = View.VISIBLE
            binding.recyclerContacts.visibility = View.GONE
            return
        }

        val contacts = mutableListOf<Contact>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        ctx.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )?.use { cursor ->
            val nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val seen = HashSet<String>()

            while (cursor.moveToNext()) {
                val number = cursor.getString(numberIdx) ?: continue
                if (!seen.add(number)) continue
                contacts.add(
                    Contact(
                        name = cursor.getString(nameIdx) ?: number,
                        number = number
                    )
                )
            }
        }

        binding.tvEmptyContacts.visibility = if (contacts.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerContacts.visibility = if (contacts.isEmpty()) View.GONE else View.VISIBLE
        binding.recyclerContacts.layoutManager = LinearLayoutManager(ctx)
        binding.recyclerContacts.adapter = ContactsAdapter(contacts) { number -> makeCall(number) }
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
