package com.example.runtracker.provider;


import com.example.runtracker.DBHandler;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;


public class MyContentProvider extends ContentProvider {

    //initialization of variable and objects
    private DBHandler myDB;
    private static final String AUTHORITY = "com.example.runtracker.provider.MyContentProvider";
    private static final String RUN_TABLE = "run";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + RUN_TABLE);
    public static final int RUNS = 1;
    public static final int RUNS_ID = 2;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, RUN_TABLE, RUNS);
        sURIMatcher.addURI(AUTHORITY, RUN_TABLE + "/#", RUNS_ID);
    }


    public MyContentProvider() {
    }

    //method to delete rows in database
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = myDB.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case RUNS:
                rowsDeleted = sqlDB.delete(DBHandler.TABLE_RUN,
                        selection,
                        selectionArgs);
                break;
            case RUNS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(DBHandler.TABLE_RUN,
                            DBHandler.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(DBHandler.TABLE_RUN,
                            DBHandler.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " +
                        uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;

    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //method to insert new data into the database
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = myDB.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case RUNS:
                id = sqlDB.insert(DBHandler.TABLE_RUN,
                        null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: "
                        + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(RUN_TABLE + "/" + id);

    }

    //method for initialization of database handler object
    @Override
    public boolean onCreate() {
        myDB = new DBHandler(getContext(), null, null, 4);
        return false;
    }

    //method to identify the data to be retrieved, and extracted in a cursor object
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DBHandler.TABLE_RUN);
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case RUNS_ID:
                queryBuilder.appendWhere(DBHandler.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            case RUNS:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        Cursor cursor = queryBuilder.query(myDB.getReadableDatabase(),
                projection, selection, selectionArgs, null, null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    //method to update the rows and return the number of rows updated
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = myDB.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case RUNS:
                rowsUpdated = sqlDB.update(DBHandler.TABLE_RUN, values,
                        selection, selectionArgs);
                break;
            case RUNS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(DBHandler.TABLE_RUN, values,
                            DBHandler.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(DBHandler.TABLE_RUN, values,
                            DBHandler.COLUMN_ID + "=" + id
                                    + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}