package com.example.android.appinventory.Data;

import android.provider.BaseColumns;



public class ProductContract {
    public static final int DATABASE_VERSION = 1;
    public class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "inventoryInfo";
        public static final String COLUMN_PRODUCT_ID = "productId";
        public static final String COLUMN_PRODUCT_NAME = "productName";
        public static final String COLUMN_PRODUCT_IMAGE = "productPic";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_STOCK = "availableStock";
        public static final String COLUMN_PRODUCT_SALES = "sales";
        public static final String COLUMN_SUPPLIER_CONTACT = "supplierContactInfo";
    }
}
