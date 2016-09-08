package com.example.android.appinventory;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class AddActivity extends AppCompatActivity {
    private Uri imageUri;
    private int IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);
        final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etPrice = (EditText) findViewById(R.id.etPrice);
        final EditText etQuantity = (EditText) findViewById(R.id.etQuantity);
        Button AddImage = (Button) findViewById(R.id.ImageAdd);
        AddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        Intent imageIntent;
                        if (Build.VERSION.SDK_INT < 19) {
                            imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        } else {
                            imageIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            imageIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        }
                        checkPermissions();
                        imageIntent.setType("image/*");
                        startActivityForResult(Intent.createChooser(imageIntent, "Select Image"), IMAGE_REQUEST);
                    }
                });
        Button done = (Button) findViewById(R.id.UpdateButton);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();

                String insertName = etName.getText().toString();
                String insertPrice = etPrice.getText().toString();
                String insertQuantity = etQuantity.getText().toString();

                if (!insertName.isEmpty() && !insertPrice.isEmpty() && !insertQuantity.isEmpty()){
                    if (imageUri != null) {
                        values.put(InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME, insertName);
                        values.put(InventoryContract.ProductEntry.COLUMN_NAME_PRICE, insertPrice);
                        values.put(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY, insertQuantity);
                        values.put(InventoryContract.ProductEntry.COLUMN_NAME_IMAGE, imageUri.toString());

                        getContentResolver().insert(InventoryContract.ProductEntry.CONTENT_URI, values);
                        Intent returnIntent = new Intent(AddActivity.this, MainActivity.class);
                        startActivity(returnIntent);

                    } else {
                        Toast.makeText(getBaseContext(), "Enter item image!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getBaseContext(), "Enter item information!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
//            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//            assert cursor != null;
//            cursor.moveToFirst();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ImageView imageView = (ImageView) findViewById(R.id.ImageOfProduct);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e("Attaching Image", "Error attaching the image", e);
            }
        }
    }
}
