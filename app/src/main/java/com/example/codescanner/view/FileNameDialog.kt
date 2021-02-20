package com.example.codescanner.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.codescanner.R
import com.example.codescanner.databinding.FragmentFileNameDialogBinding

class FileNameDialog : DialogFragment() {

    private lateinit var listener: OnDialogDismiss
    private lateinit var binding: FragmentFileNameDialogBinding

    override fun onStart() {
        super.onStart()
        setWindowParams()
    }

    fun setListener(listener: OnDialogDismiss) {
        this.listener = listener
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
            DataBindingUtil.inflate(inflater, R.layout.fragment_file_name_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.saveFile.setOnClickListener {
            val fileName = binding.fileNameEditText.text.toString()
            if (fileName.isEmpty()) {
                binding.fileNameEditText.error = "Required"
            } else {
                listener.onDialogDismiss(fileName)
                dismiss()
            }
        }
        binding.cancelButton.setOnClickListener {dismiss()}
    }
}