package com.example.android.appinventory;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    //public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();
    // For the SimpleCursorAdapter to match the UserDictionary columns to layout items.
    private static final String[] COLUMNS_TO_BE_BOUND = new String[]{
            InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME,
            InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY};
//    private static final int[] LAYOUT_ITEMS_TO_FILL = new int[]{
//            android.R.id.NameOfProduct,
//            android.R.id.PriceOfProduct};
    private TextView mEmptyStateTextView;
    private ListView mProductsListView;
    private ProductAdapter mAdapter;
    private android.app.LoaderManager loaderManager;
    //private static final int PRODUCT_LOADER_ID = 1;
    private int REQUEST_CODE = 0;
    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get the contentResolver
        ContentResolver resolver = getContentResolver();
        //get a cursor
//        Cursor cursor = resolver.query(InventoryContract.ProductEntry.CONTENT_URI,
//                null, null, null, null);
        // Set the Adapter to fill the standard two_line_list_item layout with data from the Cursor.
//        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
//                android.R.layout.two_line_list_item,
//                cursor,
//                COLUMNS_TO_BE_BOUND,
//                LAYOUT_ITEMS_TO_FILL,
//                0);
        // Attach the adapter to the ListView.
        //mProductsListView.setAdapter(adapter);
        mProductsListView = (ListView) findViewById(R.id.list);
        mAdapter = new ProductAdapter(this, new ArrayList<Product>());
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mProductsListView.setEmptyView(mEmptyStateTextView);
        mProductsListView.setAdapter(mAdapter);
        loaderManager = getLoaderManager();
        //loaderManager.initLoader(PRODUCT_LOADER_ID, null, this);
        mProductsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Product currentProduct = mAdapter.getItem(position);
                Intent productIntent = new Intent(MainActivity.this, OverviewActivity.class);
                productIntent.putExtra("currentProduct", currentProduct.getProductName());
                startActivityForResult(productIntent, REQUEST_CODE);
            }
        });
//        try {
//            mProductsListView("Products contain " + cursor.getCount() + "products\n");
//            mProductsListView("COLUMNS: " + InventoryContract.ProductEntry._ID + "-" + InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME + "-"
//                    + InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY);
//            //Get the index
//            int id = cursor.getColumnIndex(InventoryContract.ProductEntry._ID);
//            String productName = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME);
//            String quantity = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY);
//            //Iterate thru returned rows
//            while (cursor.moveToNext()) {
//            //Use the index to extract
//                id = cursor.getInt(_ID);
//                productName = cursor.getString(COLUMN_NAME_PRODUCT_NAME);
//                quantity = cursor.getString(COLUMN_NAME_QUANTITY);
//            }
//        } finally {
//        //Always close your cursor
//            cursor.close();
//        }
        mDbHelper = new InventoryDbHelper(this);
    }


//    @Override
//    public void onLoadFinished(android.content.Loader<List<Product>> loader, List<Product> products) {
//        mEmptyStateTextView.setText(R.string.empty_products);
//        mAdapter.clear();
//        if (products != null && !products.isEmpty()) {
//            mAdapter.addAll(products);
//            mAdapter.notifyDataSetChanged();
//        }
//    }
    @Override
    protected void onStart() {
        super.onStart();
       // displayDatabaseInfo();
    }

    public void addButton(View view) {
        Intent addIntent = new Intent(this, AddActivity.class);
        startActivityForResult(addIntent, REQUEST_CODE);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
//            loaderManager.restartLoader(0, null, this);
//        }
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        loaderManager.restartLoader(0, null, this);
//    }
//    public void clearTable() {
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//        db.delete(InventoryContract.ProductEntry.TABLE_NAME, null, null);
//        Log.d(LOG_TAG, "Clear Table Method");
//    }
//    public void update() {
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//        db.update(SQLiteDatabase database, int arg1, int arg2)
//        Log.d(LOG_TAG, "updateDB method is called");
//    }
}
