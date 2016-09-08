package com.example.android.appinventory;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ProductLoader extends AsyncTaskLoader {

    private Cursor mProductData;
    static final int COL_PRODUCT_NAME = 0;
    static final int COL_QUANTITY = 1;
    static final int COL_PRICE = 2;
    static final int COL_SOLD=3;

    public ProductLoader(Context context, Cursor c){
        super(context);
        mProductData = c;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Product> loadInBackground() {

        ArrayList<Product> products = new ArrayList<>();

        if(mProductData == null || mProductData.getCount() == 0) {
            return null;
        }

        while (mProductData.moveToNext()) {
            products.add(
                    new Product(mProductData.getString(COL_PRODUCT_NAME),
                            mProductData.getInt(COL_QUANTITY),
                            mProductData.getFloat(COL_PRICE),
                            mProductData.getInt(COL_SOLD))
            );
            Log.e("test",products.toString());
        }

        return products;
    }
}

