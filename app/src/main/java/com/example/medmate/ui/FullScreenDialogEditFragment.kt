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
import com.example.medmate.ui.medicine_list.Medicine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class FullScreenDialogEditFragment : DialogFragment() {

    private lateinit var medicinesRepository: MedicinesRepository
    private lateinit var deleteButton: Button

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        val windowParams = window?.attributes
        windowParams?.dimAmount = 0.75f
        windowParams?.flags = windowParams?.flags?.or(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.attributes = windowParams
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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
        deleteButton = view.findViewById<Button>(R.id.btn_delete)
        medicinesRepository = MedicinesRepository.getInstance(requireContext())

        val dialogTitle = view.findViewById<TextView>(R.id.dialog_title)

        val medicineId = arguments?.getInt("medicineId") ?: -1
        val existingName = arguments?.getString("name") ?: ""
        val existingTime = arguments?.getString("time") ?: ""
        val existingAmount = arguments?.getInt("amount") ?: 0

        nameEditText.setText(existingName)
        timeEditText.setText(existingTime)
        dialogTitle.setText("Bearbeiten")
        amountEditText.setText(existingAmount.toString())

        setupTimePicker(timeEditText)

        saveButton.setOnClickListener {
            val updatedName = nameEditText.text.toString()
            val updatedTime = timeEditText.text.toString()
            val updatedAmount = amountEditText.text.toString().toIntOrNull()

            if (updatedName.isNotBlank() && updatedTime.isNotBlank() && updatedAmount != null && updatedAmount > 0) {
                val updatedResult = medicinesRepository.updateMedicine(
                    Medicine(medicineId, updatedName, updatedAmount, updatedTime)
                )


                if (updatedResult > 0) { // Assuming updateMedicine returns the number of rows affected
                    Toast.makeText(context, "Medicine updated successfully.", Toast.LENGTH_SHORT)
                        .show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to update medicine.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill in all fields correctly.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        cancelButton.setOnClickListener {

            dismiss()
        }
        setupDeleteButton()
        return view
    }

    private fun setupDeleteButton() {

            deleteButton.visibility = View.VISIBLE
            deleteButton.setOnClickListener {
                val medicineId = arguments?.getInt("medicineId") ?: -1
                if (medicineId != -1) {
                    val deleteResult = medicinesRepository.deleteMedicine(medicineId)
                    if (deleteResult > 0) {
                        Toast.makeText(context, "Medicine deleted successfully.", Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        Toast.makeText(context, "Failed to delete medicine.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error: Invalid medicine ID.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupTimePicker(timeEditText: EditText) {
        timeEditText.isFocusable = false
        timeEditText.isClickable = true

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
                true
            ).show()
        }
    }

    companion object {
        fun newInstance(
            medicineId: Int,
            name: String,
            time: String,
            amount: Int
        ): FullScreenDialogEditFragment {
            val fragment = FullScreenDialogEditFragment()
            val args = Bundle().apply {
                putInt("medicineId", medicineId)
                putString("name", name)
                putString("time", time)
                putInt("amount", amount)
            }
            fragment.arguments = args
            return fragment
        }
    }
}