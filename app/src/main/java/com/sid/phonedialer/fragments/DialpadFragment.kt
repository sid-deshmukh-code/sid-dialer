package com.sid.phonedialer.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.sid.phonedialer.databinding.FragmentDialpadBinding

class DialpadFragment : Fragment() {

    private var _binding: FragmentDialpadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialpadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val keys = mapOf(
            binding.btn1 to "1", binding.btn2 to "2", binding.btn3 to "3",
            binding.btn4 to "4", binding.btn5 to "5", binding.btn6 to "6",
            binding.btn7 to "7", binding.btn8 to "8", binding.btn9 to "9",
            binding.btnStar to "*", binding.btn0 to "0", binding.btnHash to "#"
        )
        keys.forEach { (btn: Button, digit) ->
            btn.setOnClickListener {
                binding.etNumber.setText(binding.etNumber.text.toString() + digit)
                binding.etNumber.setSelection(binding.etNumber.text?.length ?: 0)
            }
        }

        binding.btnDelete.setOnClickListener {
            val current = binding.etNumber.text.toString()
            if (current.isNotEmpty()) {
                binding.etNumber.setText(current.dropLast(1))
                binding.etNumber.setSelection(binding.etNumber.text?.length ?: 0)
            }
        }

        binding.btnCall.setOnClickListener {
            val number = binding.etNumber.text.toString()
            if (number.isNotBlank()) makeCall(number)
        }
    }

    private fun makeCall(number: String) {
        val ctx = requireContext()
        val intent = if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        } else {
            // Fall back to the system dialer if we don't have CALL_PHONE permission
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
