package com.example.android.appinventory;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Product>> {
    private TextView mEmptyStateTextView;
    private ListView mProductsListView;
    private ProductAdapter mAdapter;
    private android.app.LoaderManager loaderManager;
    private static final int PRODUCT_LOADER_ID = 1;
    private int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get the contentResolver
        ContentResolver resolver = getContentResolver();
        //get a cursor
        Cursor cursor = resolver.query(UserDictionary.Words.CONTENT_URI,
                                                null, null, null, null);
        try{
            mProductsListView("Products contain " + cursor.getCount() + "products\n");
            mProductsListView("COLUMNS: " +words._ID + "-" + Words.FREQUENCY + "-" + Words.WORD );
            //Get the index
            int idColumn = cursor.getColumnIndex(UserDictionary.Words._ID);
            int frequencyColumn = cursor.getColumnIndex(UserDictionary.Words.FREQUENCY);
            int wordColumn = cursor.getColumnIndex(UserDictionary.Words.WORD);
            //Iterate thru returned rows
            while(cursor.moveToNext()) {
                //Use the index to extract
                int id = cursor.getInt(idColumn);
                int frequency = cursor.getInt(frequencyColumn);
                String word = cursor.getString(wordColumn);
            }
        }finally{
            //Always close your cursor
            cursor.close();
        }
        mProductsListView = (ListView) findViewById(R.id.list);
        mAdapter = new ProductAdapter(this, new ArrayList<Product>());

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mProductsListView.setEmptyView(mEmptyStateTextView);

        mProductsListView.setAdapter(mAdapter);


        loaderManager = getLoaderManager();
        loaderManager.initLoader(PRODUCT_LOADER_ID, null, this);

        mProductsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Product currentProduct = mAdapter.getItem(position);
                Intent productIntent = new Intent(MainActivity.this, OverviewActivity.class);
                productIntent.putExtra("currentProduct", currentProduct.getProductName());
                startActivityForResult(productIntent, REQUEST_CODE);
            }
        });
    }

    @Override
    public android.content.Loader<List<Product>> onCreateLoader(int id, Bundle args) {
        Cursor c = null;
        try {
            c = DbUtils.ReadAll(this);
        } catch (RuntimeException e) {
            Log.e("MainActivity", "Error reading from Database");
        }

        return new ProductLoader(this, c);
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<Product>> loader, List<Product> products) {
        mEmptyStateTextView.setText(R.string.empty_products);
        mAdapter.clear();
        if (products != null && !products.isEmpty()) {
            mAdapter.addAll(products);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<Product>> loader) {
        mAdapter.clear();
    }

    public void addButton(View view) {
        Intent addIntent = new Intent(this, AddActivity.class);
        startActivityForResult(addIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            loaderManager.restartLoader(0, null, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loaderManager.restartLoader(0, null, this);
    }
}
