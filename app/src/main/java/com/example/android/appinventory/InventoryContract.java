package com.example.android.appinventory;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class InventoryContract {
    //Authority of the content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.appinventory";

    //Added schema of the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Path of the content provider
    public static final String PATH = "items";

    public static abstract class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

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
