package com.example.medmate.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medmate.R
import com.example.medmate.ui.medicine_list.Medicine

class MedicineAdapter(
    private var medicines: MutableList<Medicine>, // Change to MutableList
) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    class MedicineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.medicine_name)
        val dosageTextView: TextView = view.findViewById(R.id.medicine_dosage)
        val timeTextView: TextView = view.findViewById(R.id.medicine_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item_medicine, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.nameTextView.text = medicine.name
        holder.dosageTextView.text = medicine.dose.toString()
        holder.timeTextView.text = medicine.time
    }

    override fun getItemCount() = medicines.size

    fun updateDataSet(newMedicines: List<Medicine>) {
        medicines.clear()
        medicines.addAll(newMedicines)
        notifyDataSetChanged()
    }
}

interface OnEditButtonClickListener {
    fun onEditButtonClick(medicine: Medicine)
}
