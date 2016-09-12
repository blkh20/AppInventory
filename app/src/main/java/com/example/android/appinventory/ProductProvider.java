package com.example.android.appinventory;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class ProductProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private InventoryDbHelper mInventoryHelper;

    static final int PRODUCT = 100;
    static final int PRODUCT_NAME = 101;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = InventoryContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, InventoryContract.PATH_PRODUCT, PRODUCT);
        matcher.addURI(authority, InventoryContract.PATH_PRODUCT + "/#", PRODUCT_NAME);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mInventoryHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri,
                        String[] projections,
                        String selections,
                        String[] selectionsArgs,
                        String sortOrder) {

        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case PRODUCT:
                retCursor = mInventoryHelper.getReadableDatabase().query(
                        InventoryContract.ProductEntry.TABLE_NAME,
                        projections,
                        selections,
                        selectionsArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PRODUCT_NAME:
                retCursor = mInventoryHelper.getReadableDatabase().query(
                        InventoryContract.ProductEntry.TABLE_NAME,
                        projections,
                        InventoryContract.ProductEntry._ID + " =? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCT:
                return InventoryContract.ProductEntry.CONTENT_TYPE;
            case PRODUCT_NAME:
                return InventoryContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mInventoryHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PRODUCT:
                long _id = db.insert(InventoryContract.ProductEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = InventoryContract.ProductEntry.buildProductUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mInventoryHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case PRODUCT:
                rowsDeleted = db.delete(
                        InventoryContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        InventoryContract.ProductEntry.TABLE_NAME + "'");
                break;
            case PRODUCT_NAME:
                rowsDeleted = db.delete(
                        InventoryContract.ProductEntry.TABLE_NAME,
                        InventoryContract.ProductEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        InventoryContract.ProductEntry.TABLE_NAME + "'");
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri,
                      ContentValues contentValues,
                      String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mInventoryHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case PRODUCT:
                rowsUpdated = db.update(InventoryContract.ProductEntry.TABLE_NAME,
                        contentValues, selection, selectionArgs);
                break;
            case PRODUCT_NAME:
                rowsUpdated = db.update(InventoryContract.ProductEntry.TABLE_NAME,
                        contentValues, InventoryContract.ProductEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
