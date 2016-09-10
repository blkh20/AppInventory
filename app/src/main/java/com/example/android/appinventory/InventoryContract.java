package com.example.android.appinventory;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;


public class InventoryContract extends ContentProvider {
    //Authority of the content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.appinventory";
    //Added schema of the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
                                                + CONTENT_AUTHORITY);
    //Path of the content provider
    public static final String PATH = "items";

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    public static abstract class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH;

        public static final String TABLE_NAME = "Products";
        public static final String COLUMN_NAME_PRODUCT_NAME = "ProductName";
        public static final String COLUMN_NAME_QUANTITY = "Quantity";
        public static final String COLUMN_NAME_PRICE = "Price";
        public static final String COLUMN_NAME_IMAGE = "Image";
        public static final String COLUMN_NAME_SOLD = "Sold";

        public static Uri buildItemsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
