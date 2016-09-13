package com.example.android.appinventory;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by BlkH20 on 9/12/2016.
 */
public class ProductProvider extends ContentProvider {

    private ProductProvider mHelper = null;
    @Override
    public boolean onCreate() {
        mHelper = new ProductProvider(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                return Items.CONTENT_TYPE;
            case ITEM_ID:
                return Items.CONTENT_ITEM_TYPE;
            case PHOTO_ID:
                return Photos.CONTENT_PHOTO_TYPE;
            case PHOTO_LIST:
                return Photos.CONTENT_TYPE;
            case ENTITY_ID:
                return ItemEntities.CONTENT_ENTITY_TYPE;
            case ENTITY_LIST:
                return ItemEntities.CONTENT_TYPE;
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
                            DBSchema.TBL_PHOTOS,
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
                    sortOrder = Items.SORT_ORDER_DEFAULT;
                }
                break;
            case ITEM_ID:
                builder.setTables(DBSchema.TBL_ITEMS);
                // limit query to one row at most:
                builder.appendWhere(Items._ID + " = " +
                        uri.getLastPathSegment());
                break;
            case PHOTO_LIST:
                builder.setTables(DBSchema.TBL_PHOTOS);
                break;
            case PHOTO_ID:
                builder.setTables(DBSchema.TBL_PHOTOS);
                // limit query to one row at most:
                builder.appendWhere(Photos._ID +
                        " = " +
                        uri.getLastPathSegment());
                break;
            case ENTITY_LIST:
                builder.setTables(DBSchema.LEFT_OUTER_JOIN_STATEMENT);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ItemEntities.SORT_ORDER_DEFAULT;
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
                String where = Items._ID + " = " + idStr;
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
                String where = Items._ID + " = " + idStr;
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
