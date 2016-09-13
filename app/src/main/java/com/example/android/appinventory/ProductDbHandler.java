package com.example.android.appinventory;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ProductDbHandler extends SQLiteOpenHelper {
    private Context mContext;
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
    // Database Name
    public static final String DATABASE_NAME = "ProductInventory";

    public ProductDbHandler(Context context) {
        super(context, DATABASE_NAME, null, ProductContract.DATABASE_VERSION);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String TEXT_TYPE = " TEXT";
        final String COMMA_SEP = ",";
        final String INTEGER_TYPE = " INTEGER";
        final String FLOAT_TYPE = " REAL";
        String CREATE_TABLE = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + "("
                + ProductContract.ProductEntry.COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY,"
                + ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + TEXT_TYPE + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE + TEXT_TYPE + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + FLOAT_TYPE + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK + INTEGER_TYPE + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_PRODUCT_SALES + INTEGER_TYPE + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_SUPPLIER_CONTACT + TEXT_TYPE + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME);
        // Creating tables again
        onCreate(db);
    }

    /**
     * CRUDD Operations (Create, Read, Update, Delete, DeleteDatabase)
     */

    // Adding new product
    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, product.getName());
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE, product.getImage());
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, product.getPrice());
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK, product.getStock());
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SALES, product.getSales());
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_CONTACT, product.getSupplier());

        // Inserting Row
        db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    // Getting one product
    public Cursor getProduct(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ProductContract.ProductEntry.TABLE_NAME,
                new String[]{
                        ProductContract.ProductEntry.COLUMN_PRODUCT_ID,
                        ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                        ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE,
                        ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                        ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK,
                        ProductContract.ProductEntry.COLUMN_PRODUCT_SALES,
                        ProductContract.ProductEntry.COLUMN_SUPPLIER_CONTACT
                },
                ProductContract.ProductEntry.COLUMN_PRODUCT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    // Getting All Products
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<Product>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + ProductContract.ProductEntry.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(Integer.parseInt(cursor.getString(0)));
                product.setName(cursor.getString(1));
                product.setImage(cursor.getString(2));
                product.setPrice(cursor.getFloat(3));
                product.setStock(Integer.parseInt(cursor.getString(4)));
                product.setSales(Integer.parseInt(cursor.getString(5)));
                product.setSupplier(cursor.getString(6));

                productList.add(product);
            } while (cursor.moveToNext());
        }

        return productList;
    }

    // Getting products Count
    public int getProductsCount() {
        String countQuery = "SELECT * FROM " + ProductContract.ProductEntry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    // Updating a single product
    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK, product.getStock());
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SALES, product.getSales());

        // updating row
        return db.update(ProductContract.ProductEntry.TABLE_NAME,
                values,
                ProductContract.ProductEntry.COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(product.getId())});
    }

    // Deleting a product
    public void deleteProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ProductContract.ProductEntry.TABLE_NAME, ProductContract.ProductEntry.COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(product.getId())});
        db.close();
    }

    //Delete all products
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ProductContract.ProductEntry.TABLE_NAME, null, null);
        db.execSQL("delete  from " + ProductContract.ProductEntry.TABLE_NAME);
        db.close();
    }

    //Delete the database file
    public void deleteDatabase() {

        mContext.deleteDatabase(DATABASE_NAME);
    }
}