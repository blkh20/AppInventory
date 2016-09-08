package com.example.android.appinventory;

import android.provider.BaseColumns;


public class InventoryContract {
    public static abstract class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "Products";
        public static final String COLUMN_NAME_PRODC_NAME = "ProductName";
        public static final String COLUMN_NAME_QUANTITY = "Quantity";
        public static final String COLUMN_NAME_PRICE = "Price";
        public static final String COLUMN_NAME_IMAGE = "Image";
        public static final String COLUMN_NAME_SOlD = "Sold";
    }
}
