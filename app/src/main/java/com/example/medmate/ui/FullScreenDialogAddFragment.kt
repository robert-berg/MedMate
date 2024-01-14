package com.example.medmate.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.medmate.R
import com.example.medmate.medicines_repository.MedicinesRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FullScreenDialogAddFragment : DialogFragment() {

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        val windowParams = window?.attributes
        windowParams?.dimAmount = 0.75f // Adjust this value for darker or lighter overlay
        windowParams?.flags = windowParams?.flags?.or(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.attributes = windowParams

        // Set dialog width and height
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // Apply margins
        window?.setGravity(Gravity.CENTER)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_layout, container, false)

        val nameEditText = view.findViewById<EditText>(R.id.med_name)
        val timeEditText = view.findViewById<EditText>(R.id.med_time)
        val amountEditText = view.findViewById<EditText>(R.id.med_amount)
        val saveButton = view.findViewById<Button>(R.id.btn_save)
        val cancelButton = view.findViewById<Button>(R.id.btn_cancel)
        val dialogTitle = view.findViewById<TextView>(R.id.dialog_title)
        dialogTitle.setText("Hinzufügen")


        // Disabling direct input on the time EditText, setting it as clickable
        timeEditText.isFocusable = false
        timeEditText.isClickable = true

        // Set up the time picker dialog
        timeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                timeEditText.setText(timeFormat.format(calendar.time))
            }
            TimePickerDialog(
                it.context,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // true for 24-hour time format
            ).show()
        }

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val time = timeEditText.text.toString()
            val amount = amountEditText.text.toString().toIntOrNull()

            if (name.isNotBlank() && time.isNotBlank() && amount != null && amount > 0) {
                // Initialize or get the instance of MedicinesRepository
                val medicinesRepository = MedicinesRepository.getInstance(requireContext())

                // Call the addMedicine method to save the data
                val medicineId = medicinesRepository.addMedicine(name, time, amount)

                if (medicineId != -1L) {
                    Toast.makeText(context, "Medicine saved successfully.", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to save medicine.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill in all fields correctly.", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        return view
    }
}

