package com.example.medmate.medicines_repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;

import com.example.medmate.ui.medicine_list.Medicine;

import java.util.ArrayList;
import java.util.List;


public class MedicinesLocalDataSource {

    private SQLiteDatabase database;
    private DBHelper dbHelper; // DBHelper is your SQLiteOpenHelper subclass

    public MedicinesLocalDataSource(Context context) {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    // Method to add a new medicine
    public long addMedicine(String name, String time, int amount) {
        long medicineId = -1;

        try {
            database.beginTransaction();

            // Insert medicine into Medicines table
            ContentValues medicineValues = new ContentValues();
            medicineValues.put("name", name);
            medicineValues.put("time", time);
            medicineValues.put("dose", amount);
            medicineId = database.insertOrThrow("Medicines", null, medicineValues);

            database.setTransactionSuccessful();
        } catch (SQLException e) {
            // Handle any SQL exceptions
        } finally {
            database.endTransaction();
        }

        return medicineId;
    }

    public List<Medicine> getAllMedicines() {
        List<Medicine> medicineList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.query("Medicines", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    Medicine medicine = new Medicine(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("dose")),
                            cursor.getString(cursor.getColumnIndexOrThrow("time"))
                    );
                    medicineList.add(medicine);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            // Handle exceptions
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return medicineList;
    }

    public int updateMedicine(Medicine medicine) {
        ContentValues values = new ContentValues();
        values.put("name", medicine.getName());
        values.put("time", medicine.getTime());
        values.put("dose", medicine.getDose());
        return database.update("Medicines", values, "id = ?", new String[] { String.valueOf(medicine.getId()) });
    }


    public int deleteMedicine(int medicineId) {
        return database.delete("Medicines", "id = ?", new String[] { String.valueOf(medicineId) });
    }
}
