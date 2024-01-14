package com.example.medmate.ui.medicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medmate.R
import com.example.medmate.medicines_repository.IMedicineRepositoryObserver
import com.example.medmate.ui.medicine_list.Medicine
import com.example.medmate.ui.medicine_list.MedicineAdapter
import com.example.medmate.medicines_repository.MedicinesRepository // Import your repository
import com.example.medmate.ui.FullScreenDialogEditFragment
import com.example.medmate.ui.medicine_list.OnEditButtonClickListener

class MedicineListFragment : Fragment(), OnEditButtonClickListener, IMedicineRepositoryObserver {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MedicineAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var medicinesRepository: MedicinesRepository
    private lateinit var view: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_medicine_list, container, false)

        medicinesRepository = MedicinesRepository.getInstance(requireContext())

        // Register this fragment as an observer
        medicinesRepository.addObserver(this)

        initializeRecyclerView()

        return view
    }

    private fun initializeRecyclerView() {
        val medicinesList = medicinesRepository.getAllMedicines()

        viewManager = LinearLayoutManager(context)
        viewAdapter = MedicineAdapter(medicinesList, this)

        recyclerView = view.findViewById<RecyclerView>(R.id.medicine_list_recyclerview).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
    override fun onEditButtonClick(medicine: Medicine) {
        val editFragment = FullScreenDialogEditFragment.newInstance(
            medicine.id, medicine.name, medicine.time, medicine.dose
        )
        editFragment.show(parentFragmentManager, "editMedicine")
    }

    override fun onMedicineChanged(operation: String?) {
        val updatedMedicinesList = medicinesRepository.getAllMedicines()
        viewAdapter.updateDataSet(updatedMedicinesList)
        viewAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister this fragment as an observer
        medicinesRepository.removeObserver(this)
    }
}
