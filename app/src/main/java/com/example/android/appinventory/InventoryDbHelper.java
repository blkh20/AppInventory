package com.example.android.appinventory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Products.db";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + InventoryContract.ProductEntry.TABLE_NAME + " ("
            + InventoryContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME + " TEXT NOT NULL,"
            + InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY + "INTEGER NOT NULL,"
            + InventoryContract.ProductEntry.COLUMN_NAME_PRICE + "INTEGER NOT NULL,"
            + InventoryContract.ProductEntry.COLUMN_NAME_IMAGE + "TEXT NOT NULL,"
            + InventoryContract.ProductEntry.COLUMN_NAME_SOLD + "TEXT NOT NULL);";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
            + InventoryContract.ProductEntry.TABLE_NAME;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}

