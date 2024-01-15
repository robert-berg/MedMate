package com.example.medmate.medicines_repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

import android.content.Context;

import com.example.medmate.ui.medicine_list.Medicine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class MedicinesRepositoryTest {

    private MedicinesRepository medicinesRepository;
    private MedicinesLocalDataSource mockLocalDataSource;
    private MedicineRemoteDataSource mockRemoteDataSource;

    @Before
    public void setUp() {
        mockLocalDataSource = mock(MedicinesLocalDataSource.class);
        mockRemoteDataSource = mock(MedicineRemoteDataSource.class);
        medicinesRepository = new MedicinesRepository(mockLocalDataSource, mockRemoteDataSource);
    }

    @Test
    public void testAddMedicine() {
        String name = "Aspirin";
        String time = "09:00";
        int amount = 1;
        long expectedId = 123;
        when(mockLocalDataSource.addMedicine(name, time, amount)).thenReturn(expectedId);
        long id = medicinesRepository.addMedicine(name, time, amount);
        assertEquals(expectedId, id);
        verify(mockLocalDataSource).addMedicine(name, time, amount);
        verify(mockRemoteDataSource).publishMedicineUpdate(anyString());
    }

    @Test
    public void testGetAllMedicines() {
        List<Medicine> expectedMedicines = new ArrayList<>();
        when(mockLocalDataSource.getAllMedicines()).thenReturn(expectedMedicines);
        List<Medicine> medicines = medicinesRepository.getAllMedicines();
        assertSame(expectedMedicines, medicines);
        verify(mockLocalDataSource).getAllMedicines();
    }

    @Test
    public void testUpdateMedicine() {
        Medicine medicine = new Medicine(123, "Aspirin", 1, "09:00");
        int expectedRowsAffected = 1;
        when(mockLocalDataSource.updateMedicine(medicine)).thenReturn(expectedRowsAffected);
        int rowsAffected = medicinesRepository.updateMedicine(medicine);
        assertEquals(expectedRowsAffected, rowsAffected);
        verify(mockLocalDataSource).updateMedicine(medicine);
        verify(mockRemoteDataSource).publishMedicineUpdate(anyString());
    }

    @Test
    public void testDeleteMedicine() {
        int medicineId = 123;
        int expectedRowsAffected = 1;
        when(mockLocalDataSource.deleteMedicine(medicineId)).thenReturn(expectedRowsAffected);
        int rowsAffected = medicinesRepository.deleteMedicine(medicineId);
        assertEquals(expectedRowsAffected, rowsAffected);
        verify(mockLocalDataSource).deleteMedicine(medicineId);
        verify(mockRemoteDataSource).publishMedicineUpdate(anyString());
    }
}