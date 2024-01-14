package com.example.medmate.medicines_repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MedicineDatabase";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_DAYS_OF_WEEK_TABLE = "CREATE TABLE DaysOfWeek (id INTEGER PRIMARY KEY, day_name TEXT);";
        db.execSQL(CREATE_DAYS_OF_WEEK_TABLE);

        // Populate the DaysOfWeek table with days
        for (String day : new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"}) {
            ContentValues values = new ContentValues();
            values.put("day_name", day);
            db.insert("DaysOfWeek", null, values);
        }
        String CREATE_MEDICINE_TABLE = "CREATE TABLE Medicines (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "time TEXT," +
                "dose INTEGER" +
                ");";

        db.execSQL(CREATE_MEDICINE_TABLE);
        // Additional table creations
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS Medicines");
        // Create tables again
        onCreate(db);
    }
}
