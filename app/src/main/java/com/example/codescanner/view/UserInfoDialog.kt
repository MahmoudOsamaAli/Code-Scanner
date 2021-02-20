package com.example.codescanner.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.codescanner.R
import com.example.codescanner.databinding.FragmentProfileDialogBinding

class UserInfoDialog : DialogFragment() {

    private lateinit var binding: FragmentProfileDialogBinding
    private lateinit var pref: SharedPreferences
    private var defaultName: String? = null
    private var defaultCode: String? = null
    private var defaultPass: String? = null

    override fun onStart() {
        super.onStart()
        setWindowParams()
    }

    private fun setWindowParams() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        pref = requireActivity().getSharedPreferences("", Context.MODE_PRIVATE)
        defaultName = pref.getString(getString(R.string.user_name), "")
        defaultCode = pref.getInt(getString(R.string.user_code), 0).toString()
        defaultPass = pref.getString(getString(R.string.user_pass), "")

        binding.userNameEditText.setText(defaultName)
        binding.userCodeEditText.setText(defaultCode)
        binding.userPassEditText.setText(defaultPass)

        binding.saveButton.setOnClickListener { save() }

        binding.closeButton.setOnClickListener { dismiss() }

    }

    private fun save() {
        when {
            binding.userNameEditText.text.toString().isEmpty() -> {
                binding.userNameEditText.error = "Required"
            }
            binding.userCodeEditText.text.toString().isEmpty() -> {
                binding.userCodeEditText.error = "Required"
            }
            binding.userPassEditText.text.toString().isEmpty() -> {
                binding.userPassEditText.error = "Required"
            }
            else -> {
                with(pref.edit()) {
                    defaultPass = binding.userPassEditText.text.toString()
                    defaultCode = binding.userCodeEditText.text.toString()
                    defaultName = binding.userNameEditText.text.toString()
                    putString(getString(R.string.user_name), defaultName)
                    putInt(getString(R.string.user_code), defaultCode!!.toInt())
                    putString(getString(R.string.user_pass), defaultPass)
                    apply()
                }
                dismiss()
            }
        }
    }
}