package com.example.medmate.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medmate.adherence_repository.AdherenceRemoteDataSource
import com.example.medmate.databinding.FragmentHomeBinding
import com.example.medmate.medicines_repository.MedicinesRepository
import com.example.medmate.ui.medicine_list.Medicine
import java.text.SimpleDateFormat
import java.util.*
import com.example.medmate.medicines_repository.IMedicineRepositoryObserver

class HomeFragment : Fragment(), AdherenceRemoteDataSource.AdherenceMessageObserver,IMedicineRepositoryObserver
{
    private lateinit var medicinesRepository: MedicinesRepository

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adherenceDataSource: AdherenceRemoteDataSource
    private var receivedMessagesCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val currentDate = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
        (activity as AppCompatActivity).supportActionBar?.title = currentDate

        initializeRecyclerView()
        subscribeToRepositories()
        return root
    }

    private fun initializeRecyclerView() {
        medicinesRepository = MedicinesRepository.getInstance(requireContext())
        val medicinesList = medicinesRepository.getAllMedicines().sortedBy { it.time }.drop(receivedMessagesCount)
            .toMutableList()

        binding.medicineListRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MedicineAdapter(medicinesList)
        }
    }

    private fun subscribeToRepositories() {
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("AppPrefs", AppCompatActivity.MODE_PRIVATE)
        val ipAddress = sharedPreferences.getString("IPAddress", "192.168.0.105")
        adherenceDataSource = AdherenceRemoteDataSource.getInstance(ipAddress)
        adherenceDataSource.addObserver(this)
        medicinesRepository.addObserver(this)
    }

    override fun onAdherenceMessageReceived(messageCount: Int) {
        receivedMessagesCount = messageCount
        val updatedMedicinesList = medicinesRepository.getAllMedicines().sortedBy { it.time }.drop(receivedMessagesCount)
            .toMutableList()
        activity?.runOnUiThread {
            (binding.medicineListRecyclerview.adapter as MedicineAdapter).updateDataSet(updatedMedicinesList)
        }

    }

    override fun onMedicineChanged(operation: String?) {
        // React to changes in MedicinesRepository
        val updatedMedicinesList = medicinesRepository.getAllMedicines().sortedBy { it.time }.drop(receivedMessagesCount)
            .toMutableList()
        (binding.medicineListRecyclerview.adapter as MedicineAdapter).updateDataSet(updatedMedicinesList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adherenceDataSource.removeObserver(this)
        medicinesRepository.removeObserver(this)
        _binding = null
    }
}