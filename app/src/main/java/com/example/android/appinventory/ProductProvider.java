package com.example.android.appinventory;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by BlkH20 on 9/12/2016.
 */
public class ProductProvider extends ContentProvider {
    // Database Name
    public static final String DATABASE_NAME = "ProductInventory";

    private ProductProvider mHelper = null;

//    // Holds the database object
//    private SQLiteDatabase db;
//
//    public ProductProvider(Context context, String databaseName, Object o, int i) {
//    }
//
//    public boolean onCreate() {
//
//        /*
//         * Creates a new helper object. This method always returns quickly.
//         * Notice that the database itself isn't created or opened
//         * until SQLiteOpenHelper.getWritableDatabase is called
//         */
//        mHelper = new ProductProvider(
//                getContext(),        // the application context
//                DATABASE_NAME,              // the name of the database)
//                null,                // uses the default SQLite cursor
//                1                    // the version number
//        );
//
//        return true;
//    }

    // helper constants for use with the UriMatcher
    private static final int ITEM_LIST = 1;
    private static final int ITEM_ID = 2;
    private static final int PHOTO_LIST = 5;
    private static final int PHOTO_ID = 6;
    private static final int ENTITY_LIST = 10;
    private static final int ENTITY_ID = 11;
    private static final UriMatcher URI_MATCHER;

    // prepare the UriMatcher
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(ProductContract.AUTHORITY,
                "items",
                ITEM_LIST);
        URI_MATCHER.addURI(ProductContract.AUTHORITY,
                "items/#",
                ITEM_ID);
        URI_MATCHER.addURI(ProductContract.AUTHORITY,
                "photos",
                PHOTO_LIST);
        URI_MATCHER.addURI(ProductContract.AUTHORITY,
                "photos/#",
                PHOTO_ID);
        URI_MATCHER.addURI(ProductContract.AUTHORITY,
                "entities",
                ENTITY_LIST);
        URI_MATCHER.addURI(ProductContract.AUTHORITY,
                "entities/#",
                ENTITY_ID);
    }

    @Override
    public boolean onCreate() {
        mHelper = new ProductProvider(getContext());
        return true;
    }
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                return ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
            case ITEM_ID:
                return ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE;
            case PHOTO_ID:
                return ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE;
            case PHOTO_LIST:
                return ProductContract.ProductEntry.COLUMN_PRODUCT_SALES;
            case ENTITY_ID:
                return ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK;
            case ENTITY_LIST:
                return ProductContract.ProductEntry.COLUMN_SUPPLIER_CONTACT;
            default:
                return null;
        }
    }
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != ITEM_LIST
                && URI_MATCHER.match(uri) != PHOTO_LIST) {
            throw new IllegalArgumentException(
                    "Unsupported URI for insertion: " + uri);
        }
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if (URI_MATCHER.match(uri) == ITEM_LIST) {
            long id =
                    db.insert(
                            DBSchema.TBL_ITEMS,
                            null,
                            values);
            return getUriForId(id, uri);
        } else {
            // this insertWithOnConflict is a special case;
            // CONFLICT_REPLACE means that an existing entry
            // which violates the UNIQUE constraint on the
            // item_id column gets deleted. In this case this
            // INSERT behaves nearly like an UPDATE. Though
            // the new row has a new primary key.
            // See how I mentioned this in the Contract class.
            long id =
                    db.insertWithOnConflict(
                            ProductContract.ProductEntry.TBL_PHOTOS,
                            null,
                            values,
                            SQLiteDatabase.CONFLICT_REPLACE);
            return getUriForId(id, uri);
        }
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            if (!isInBatchMode()) {
                // notify all listeners of changes:
                getContext().
                        getContentResolver().
                        notifyChange(itemUri, null);
            }
            return itemUri;
        }
        // s.th. went wrong:
        throw new SQLException(
                "Problem while inserting into uri: " + uri);
    }
    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        boolean useAuthorityUri = false;
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                builder.setTables(DBSchema.TBL_ITEMS);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ProductContract.ProductEntry.SORT_ORDER_DEFAULT;
                }
                break;
            case ITEM_ID:
                builder.setTables(DBSchema.TBL_ITEMS);
                // limit query to one row at most:
                builder.appendWhere(ProductContract.ProductEntry._ID + " = " +
                        uri.getLastPathSegment());
                break;
            case PHOTO_LIST:
                builder.setTables(DBSchema.TBL_PHOTOS);
                break;
            case PHOTO_ID:
                builder.setTables(DBSchema.TBL_PHOTOS);
                // limit query to one row at most:
                builder.appendWhere(ProductContract.ProductEntry._ID +
                        " = " +
                        uri.getLastPathSegment());
                break;
            case ENTITY_LIST:
                builder.setTables(DBSchema.LEFT_OUTER_JOIN_STATEMENT);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ProductContract.ProductEntry.SORT_ORDER_DEFAULT;
                }
                useAuthorityUri = true;
                break;
            case ENTITY_ID:
                builder.setTables(DBSchema.LEFT_OUTER_JOIN_STATEMENT);
                // limit query to one row at most:
                builder.appendWhere(DBSchema.TBL_ITEMS +
                        "." +
                        ProductContract.ProductEntry._ID +
                        " = " +
                        uri.getLastPathSegment());
                useAuthorityUri = true;
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + uri);
        }
        Cursor cursor =
                builder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
        // if we want to be notified of any changes:
        if (useAuthorityUri) {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    ProductContract.CONTENT_URI);
        }
        else {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    uri);
        }
        return cursor;
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int updateCount = 0;
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                updateCount = db.update(
                        DBSchema.TBL_ITEMS,
                        values,
                        selection,
                        selectionArgs);
                break;
            case ITEM_ID:
                String idStr = uri.getLastPathSegment();
                String where = ProductContract.ProductEntry._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        DBSchema.TBL_ITEMS,
                        values,
                        where,
                        selectionArgs);
                break;
            default:
                // no support for updating photos or entities!
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (updateCount > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int delCount = 0;
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                delCount = db.delete(
                        DBSchema.TBL_ITEMS,
                        selection,
                        selectionArgs);
                break;
            case ITEM_ID:
                String idStr = uri.getLastPathSegment();
                String where = ProductContract.ProductEntry._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        DBSchema.TBL_ITEMS,
                        where,
                        selectionArgs);
                break;
            default:
                // no support for deleting photos or entities –
                // photos are deleted by a trigger when the item is deleted
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (delCount > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return delCount;
    }
}
