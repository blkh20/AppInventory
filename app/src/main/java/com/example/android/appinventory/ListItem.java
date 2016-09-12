package com.example.android.appinventory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.product_inventory.R;

public class ListItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_products);
    }
}
