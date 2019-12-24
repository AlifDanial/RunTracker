package com.example.runtracker;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.content.ContentResolver;
import android.util.Log;

import com.example.runtracker.provider.MyContentProvider;

public class DBHandler extends SQLiteOpenHelper {

    private ContentResolver CR;
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "mapsDB.db";
    public static final String TABLE_RUN ="run";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_ELEVATION = "elevation";
    public static final String COLUMN_MAP = "map";

    public DBHandler(Context context, String name,
                     SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        CR = context.getContentResolver();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RUN_TABLE = String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "%s TEXT," +
                        "%s TEXT," +
                        "%s TEXT," +
                        "%s TEXT," +
                        "%s BLOB)",
                TABLE_RUN,
                COLUMN_ID,
                COLUMN_DURATION,
                COLUMN_DISTANCE,
                COLUMN_DATE,
                COLUMN_ELEVATION,
                COLUMN_MAP
        );
        db.execSQL(CREATE_RUN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUN);
        onCreate(db);
    }

    //method to add recipe into database through content provider
    public void addRun(Runs run) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DURATION, run.getRunDuration());
        values.put(COLUMN_DISTANCE, run.getRunDistance());
        values.put(COLUMN_DATE, run.getRunDate());
        values.put(COLUMN_ELEVATION, run.getRunElevation());
        values.put(COLUMN_MAP, run.getRunMap());
        CR.insert(MyContentProvider.CONTENT_URI, values);
    }

    //method to delete recipe through content provider according to title
    public boolean deleteRun (String id) {
        boolean result = false;
        String selection = "id = \"" + id + "\"";
        int rowsDeleted = CR.delete(MyContentProvider.CONTENT_URI,
                selection, null);
        if (rowsDeleted > 0)
            result = true;
        return result;
    }




}
