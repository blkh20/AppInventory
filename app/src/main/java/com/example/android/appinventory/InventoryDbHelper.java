package com.example.android.appinventory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class InventoryDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = " Products.db ";
    private static final int DATABASE_VERSION = 4;
    private static final String SQL_DELETE_ENTRIES = " DROP TABLE IF EXISTS "
            + InventoryContract.ProductEntry.TABLE_NAME;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.d(LOG_TAG, "onCreate method called");
        String SQL_CREATE_TABLE = " CREATE TABLE "
                + InventoryContract.ProductEntry.TABLE_NAME + " ("
                + InventoryContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY + " INTEGER NOT NULL, "
                + InventoryContract.ProductEntry.COLUMN_NAME_PRICE + " INTEGER NOT NULL, "
                + InventoryContract.ProductEntry.COLUMN_NAME_IMAGE + " TEXT NOT NULL, "
                + InventoryContract.ProductEntry.COLUMN_NAME_SOLD + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }
    // Method to delete the db
    public void deleteDatabase(Context context, String dbName) {
        context.deleteDatabase(dbName);
        Log.d(LOG_TAG, "deleteDatabase method called");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
        Log.d(LOG_TAG, "onUpgrade method called");
    }
}

