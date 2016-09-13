package com.example.android.appinventory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Product> productInventory = new ArrayList<>();
    private ListView listView;
    private TextView textView;
    private ProductAdapter adapter;
    private ProductDbHandler db = new ProductDbHandler(this);

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete_database:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.menu_alert_title)
                        .setMessage(R.string.menu_delete_warning)
                        .setPositiveButton(R.string.menu_confirm_btn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listView = (ListView) findViewById(R.id.product_list);
                                //Delete entire Database
                                db.close();
                                db.deleteDatabase();
                                adapter.notifyDataSetChanged();
                                listView.setVisibility(View.GONE);

                            }
                        })
                        .setNegativeButton(R.string.menu_cancel_btn, null)
                        .create();
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addItemBtn = (Button) findViewById(R.id.add_item_btn);

        listView = (ListView) findViewById(R.id.product_list);
        adapter = new ProductAdapter(MainActivity.this, productInventory);
        listView.setAdapter(adapter);

        if (db.getProductsCount() == 0) {
            textView = (TextView) findViewById(R.id.no_product);
            textView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);

            productInventory.addAll(db.getAllProducts());
            adapter.notifyDataSetChanged();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ProductDetails.class);
                intent.putExtra("class", productInventory.get(position));
                startActivityForResult(intent, 0);
            }
        });



        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddProduct.class);

                startActivityForResult(i, 0);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(listView.getVisibility() == View.GONE)
        {
            listView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }
        adapter.clear();
        productInventory.clear();
        productInventory.addAll(db.getAllProducts());
        adapter.notifyDataSetChanged();

        if(productInventory != null)
        {
            if(productInventory.size() == 0)
            {
                listView.setVisibility(View.GONE);
            }
            else
            {
                listView.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            listView.setVisibility(View.GONE);
        }
    }

}
