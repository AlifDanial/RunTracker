package com.example.runtracker;

import android.net.Uri;

public class AppContract {
    //a container for constants that define the database
    public static final String AUTHORITY = "com.example.runtracker.provider.MyContentProvider";
    public static final String RUN_TABLE = "run";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + RUN_TABLE);
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_ELEVATION = "elevation";
    public static final String COLUMN_MAP = "map";
    public static final int DATABASE_VERSION = 4;
}
