package com.example.android.appinventory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by BlkH20 on 9/7/2016.
 */
public class OverviewActivity extends AppCompatActivity {

    private InventoryDbHelper mDbHelper;
    static final int COL_PRODUCT_NAME = 1;
    static final int COL_QUANTITY = 2;
    static final int COL_PRICE = 3;
    static final int COL_IMAGE = 4;
    static final int COL_SOLD = 5;
    private Cursor Josh;
    private SQLiteDatabase db;
    private String selection;
    private String[] projection = {InventoryContract.ProductEntry._ID,
            InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME,
            InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY,
            InventoryContract.ProductEntry.COLUMN_NAME_PRICE,
            InventoryContract.ProductEntry.COLUMN_NAME_IMAGE,
            InventoryContract.ProductEntry.COLUMN_NAME_SOLD};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);
        mDbHelper = new InventoryDbHelper(getApplicationContext());
        String name = "";
        db = mDbHelper.getWritableDatabase();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("currentProduct");
        }
        selection = InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME + " LIKE '" + name + "'";
        UpdateUi();
        Button UpdateButton = (Button) findViewById(R.id.button);
        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        Button increment = (Button) findViewById(R.id.increment);
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQn = Josh.getInt(COL_QUANTITY) + 1;
                DbUtils.Update(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY,
                        Integer.toString(newQn),
                        Josh.getInt(0),
                        getApplicationContext());
                UpdateUi();

            }
        });
        Button decrement = (Button) findViewById(R.id.decrement);
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQn = Josh.getInt(COL_QUANTITY) - 1;
                if (!(newQn < 0)) {
                    DbUtils.Update(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY,
                            Integer.toString(newQn),
                            Josh.getInt(0),
                            getApplicationContext());
                    UpdateUi();
                }
            }
        });
        Button makeSaleBtn = (Button) findViewById(R.id.sale);
        makeSaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQn = Josh.getInt(COL_QUANTITY) - 1;
                if (!(newQn < 0)) {
                    DbUtils.Update(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY,
                            Integer.toString(newQn),
                            Josh.getInt(0), getApplicationContext());
                    UpdateUi();
                }
            }
        });
        Button orderBtn = (Button) findViewById(R.id.shipment);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent orderIntent = new Intent(Intent.ACTION_SENDTO);
                orderIntent.setData(Uri.parse("mailto:"));
                orderIntent.putExtra(Intent.EXTRA_SUBJECT, "Order For " + Josh.getString(COL_PRODUCT_NAME));
                orderIntent.putExtra(Intent.EXTRA_TEXT, "This is an Order request for "
                        + Josh.getString(COL_PRODUCT_NAME)
                        + ". \n" + "Requested Amount:");
                if (orderIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(orderIntent);
                }
            }
        });
        Button deleteBtn = (Button) findViewById(R.id.deleteButton);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OverviewActivity.this);
                builder.setMessage(R.string.deletewarn);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DbUtils.delete(Josh.getInt(0), OverviewActivity.this);
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog delProdcAlert = builder.create();
                delProdcAlert.show();
            }
        });
    }
    private void UpdateUi() {
        Josh = db.query(InventoryContract.ProductEntry.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                null);
        Josh.moveToFirst();
        TextView productNameTextView = (TextView) findViewById(R.id.nameoverview);
        productNameTextView.setText(Josh.getString(COL_PRODUCT_NAME));

        TextView quantityTextView = (TextView) findViewById(R.id.quantityoverview);
        quantityTextView.setText(Integer.toString(Josh.getInt(COL_QUANTITY)));

        TextView priceTextView = (TextView) findViewById(R.id.priceoverview);
        String priceTag = "$" + Josh.getFloat(COL_PRICE);
        priceTextView.setText(priceTag);

        Uri imageUri = Uri.parse(Josh.getString(COL_IMAGE));
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            ImageView imageView = (ImageView) findViewById(R.id.imageOverview);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView soldTextView = (TextView) findViewById(R.id.quantityoverview);
        String soldString = "Sold:" + Josh.getInt(COL_SOLD);
        soldTextView.setText(soldString);

    }
    ParcelFileDescriptor parcelFileDescriptor = null;
    try {
        parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    } catch (Exception e) {
        Log.e(TAG, "Failed to load image.", e);
        return null;
    } finally {
        try {
            if (parcelFileDescriptor != null) {
                parcelFileDescriptor.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error closing ParcelFile Descriptor");
        }
    }
}
}

