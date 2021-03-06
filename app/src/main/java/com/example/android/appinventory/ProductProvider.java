package com.example.android.appinventory;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
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

    private SQLiteDatabase db;
    // Database Name
    public static final String DATABASE_NAME = "ProductInventory";
    // helper constants for use with the UriMatcher
    private static final int ITEM_LIST = 1;
    private static final int ITEM_ID = 2;
    private static final int PHOTO_LIST = 5;
    private static final int PHOTO_ID = 6;
    private static final int ENTITY_LIST = 10;
    private static final int ENTITY_ID = 11;
    private static final UriMatcher URI_MATCHER;
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(ProductContract.AUTHORITY,"items",ITEM_LIST);
        URI_MATCHER.addURI(ProductContract.AUTHORITY,"items/#",ITEM_ID);
        URI_MATCHER.addURI(ProductContract.AUTHORITY,"photos",PHOTO_LIST);
        URI_MATCHER.addURI(ProductContract.AUTHORITY,"photos/#",PHOTO_ID);
        URI_MATCHER.addURI(ProductContract.AUTHORITY,"entities",ENTITY_LIST);
        URI_MATCHER.addURI(ProductContract.AUTHORITY,"entities/#",ENTITY_ID);
    }
    ProductDbHandler mHelper = null;
    private final ThreadLocal<Boolean> mIsInBatchMode = new ThreadLocal<Boolean>();
    @Override
    public boolean onCreate() {
        /*
         * Creates a new helper object. This method always returns quickly.
         * Notice that the database itself isn't created or opened
         * until SQLiteOpenHelper.getWritableDatabase is called
         */
        mHelper = new ProductDbHandler(getContext());
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
        getContext().getContentResolver().notifyChange(uri, null);
        if (URI_MATCHER.match(uri) == ITEM_LIST) {
            long id =
                    db.insert(
                            ProductContract.ProductEntry.TABLE_NAME,
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
                            ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE,
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
                getContext().getContentResolver().notifyChange(itemUri, null);
            }
            return itemUri;
        }
        // s.th. went wrong:
        throw new SQLException(
                "Problem while inserting into uri: " + uri);
    }
    @Override
    public Cursor query(Uri uri, String[] projection,String selection, String[] selectionArgs,
                        String sortOrder) {
       // doAnalytics(uri, "query");

        SQLiteDatabase db = mHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        boolean useAuthorityUri = false;
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                builder.setTables(ProductContract.ProductEntry.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ProductContract.ProductEntry.COLUMN_PRODUCT_ID;
                }
                break;
            case ITEM_ID:
                builder.setTables(ProductContract.ProductEntry.TABLE_NAME);
                // limit query to one row at most:
                builder.appendWhere(ProductContract.ProductEntry._ID + " = " +
                        uri.getLastPathSegment());
                break;
            case PHOTO_LIST:
                builder.setTables(ProductContract.ProductEntry.TABLE_NAME);
                break;
            case PHOTO_ID:
                builder.setTables(ProductContract.ProductEntry.TABLE_NAME);
                // limit query to one row at most:
                builder.appendWhere(ProductContract.ProductEntry._ID + " = " + uri.getLastPathSegment());
                break;
            case ENTITY_LIST:
                builder.setTables(ProductContract.ProductEntry.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ProductContract.ProductEntry.COLUMN_PRODUCT_ID;
                }
                useAuthorityUri = true;
                break;
            case ENTITY_ID:
                builder.setTables(ProductContract.ProductEntry.TABLE_NAME);
                // limit query to one row at most:
                builder.appendWhere(ProductContract.ProductEntry.TABLE_NAME +
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
                builder.query(db, projection, selection, selectionArgs,
                        null, null, sortOrder);
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
        int updateCount;
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                updateCount = db.update(
                        ProductContract.ProductEntry.TABLE_NAME,
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
                        ProductContract.ProductEntry.TABLE_NAME,
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
        int delCount;
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                delCount = db.delete(
                        ProductContract.ProductEntry.TABLE_NAME,
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
                        ProductContract.ProductEntry.TABLE_NAME,
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

    private boolean isInBatchMode() {
        return mIsInBatchMode.get() != null && mIsInBatchMode.get();
    }
}
