package com.example.android.appinventory;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;



public abstract class InventoryContract extends ContentProvider {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {}
       public static abstract class ProductEntry implements BaseColumns {
        //Authority of the content provider
        public static final String CONTENT_AUTHORITY = "com.example.android.appinventory";
        //Added schema of the content provider
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
                + CONTENT_AUTHORITY);
        //Path of the content provider
        public static final String PATH = "items";
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
    }
}
