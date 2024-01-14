package com.example.medmate.medicines_repository;

import com.example.medmate.ui.medicine_list.Medicine;

public interface IMedicineRepositoryObserver {
    void onMedicineChanged(String operation);
}
