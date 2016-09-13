package com.example.android.appinventory;

import android.net.Uri;
import android.provider.BaseColumns;



public final class ProductContract {
    /**
     * The authority of the lentitems provider.
     */
    public static final String AUTHORITY =
            "com.example.android.appinventory";
    /**
     * The content URI for the top-level
     * lentitems authority.
     */
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);
//    /**
//     * Constants for the Items table
//     * of the lentitems provider.
//     */
//    public static final class Items
//            implements CommonColumns {
//        /**
//     * The content URI for this table.
//     */
//    public static final Uri CONTENT_URI =
//            Uri.withAppendedPath(
//                    ProductContract.CONTENT_URI,
//                    "items");
//    /**
//     * The mime type of a directory of items.
//     */
//    public static final String CONTENT_TYPE =
//            ContentResolver.CURSOR_DIR_BASE_TYPE +
//                    "/vnd.de.openminds.lentitems_items";
//    /**
//     * The mime type of a single item.
//     */
//    public static final String CONTENT_ITEM_TYPE =
//            ContentResolver.CURSOR_ITEM_BASE_TYPE +
//                    "com.example.android.AppInventory.ProductProvider";
//    /**
//     * A projection of all columns
//     * in the items table.
//     */
//    public static final String[] PROJECTION_ALL =
//            {_ID, NAME, BORROWER};
//    /**
//     * The default sort order for
//     * queries containing NAME fields.
//     */
//    public static final String SORT_ORDER_DEFAULT =
//            NAME + " ASC";
//}
//    /**
//     * Constants for the Photos table of the
//     * lentitems provider. For each item there
//     * is exactly one photo. You can
//     * safely call insert with the an already
//     * existing ITEMS_ID. You won’t get constraint
//     * violations. The content provider takes care
//     * of this.<br>
//     * Note: The _ID of the new record in this case
//     * differs from the _ID of the old record.
//     */
//    public static final class Photos
//            implements BaseColumns { … }

//    /**
//     * Constants for a joined view of Items and
//     * Photos. The _id of this joined view is
//     * the _id of the Items table.
//     */
//    public static final class ItemEntities
//            implements CommonColumns { …}

//    /**
//     * This interface defines common columns
//     * found in multiple tables.
//     */
//    public static interface CommonColumns
//            extends BaseColumns { … }

    // Database Version
    public static final int DATABASE_VERSION = 1;

    public class ProductEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "inventoryInfo";
        // Table Columns names
        public static final String COLUMN_PRODUCT_ID = "productId";
        public static final String COLUMN_PRODUCT_NAME = "productName";
        public static final String COLUMN_PRODUCT_IMAGE = "productPic";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_STOCK = "availableStock";
        public static final String COLUMN_PRODUCT_SALES = "sales";
        public static final String COLUMN_SUPPLIER_CONTACT = "supplierContactInfo";
    }
}