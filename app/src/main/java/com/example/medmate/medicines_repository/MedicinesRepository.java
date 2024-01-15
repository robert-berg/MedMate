package com.example.medmate.medicines_repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.medmate.ui.medicine_list.Medicine;

import java.util.ArrayList;
import java.util.List;

public class MedicinesRepository {
    private static MedicinesRepository instance;
    private MedicinesLocalDataSource localDataSource;
    private MedicineRemoteDataSource remoteDataSource;
    private List<IMedicineRepositoryObserver> observers;

    // Private constructor
    MedicinesRepository(MedicinesLocalDataSource localDataSource,
                        MedicineRemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
        observers = new ArrayList<>();
    }
    // Public static method to get the instance
    public static synchronized MedicinesRepository getInstance(Context context) {
        if (instance == null) {
            MedicinesLocalDataSource localDataSource = new MedicinesLocalDataSource(context);
            MedicineRemoteDataSource remoteDataSource = new MedicineRemoteDataSource(context);
            instance = new MedicinesRepository(localDataSource, remoteDataSource);
        }
        return instance;
    }

    public void addObserver(IMedicineRepositoryObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(IMedicineRepositoryObserver observer) {
        observers.remove(observer);
    }

    private void notifyMedicineChanged(String operation) {
        Log.d("#tagForOnmedicinechanged", "before for");

        for (IMedicineRepositoryObserver observer : observers) {
            observer.onMedicineChanged(operation);
            Log.d("#tagForOnmedicinechanged", "...");

        }
    }

    @SuppressLint("DefaultLocale")
    public long addMedicine(String name, String time, int amount) {
        long result = localDataSource.addMedicine(name, time, amount);
        if (result != -1) {
            notifyMedicineChanged("add");
            remoteDataSource.publishMedicineUpdate(String.format(
                    "{\"operation\": \"create\", " +
                    "\"medicine\": {\"id\": %d, \"name\": \"%s\", \"time\": \"%s\", \"dose\": %d}" +
                    "}",
                    result, name, time, amount
            ));
        }
        return result;
    }

    public List<Medicine> getAllMedicines() {
        return localDataSource.getAllMedicines();
    }

    @SuppressLint("DefaultLocale")
    public int updateMedicine(Medicine medicine) {
        int result = localDataSource.updateMedicine(medicine);
        if (result > 0) {
            notifyMedicineChanged("update");
            remoteDataSource.publishMedicineUpdate(String.format(
                    "{\"operation\": \"update\", " +
                            "\"medicine\": {\"id\": %d, \"name\": \"%s\", \"time\": \"%s\", \"dose\": %d}" +
                            "}",
                    medicine.getId(), medicine.getName(), medicine.getTime(), medicine.getDose()
            ));
        }
        return result;
    }

    public int deleteMedicine(int medicineId) {
        int result = localDataSource.deleteMedicine(medicineId);
        if (result > 0) {
            notifyMedicineChanged("delete");
            remoteDataSource.publishMedicineUpdate("{" +
                    "\"operation\": \"delete\"" +
                    "\"medicine\": {\"id\": " + medicineId + "}" +
                    "}");
        }
        return result;
    }


}

