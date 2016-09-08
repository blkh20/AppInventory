package com.example.android.appinventory;

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
        Button UpdateButton = (Button) findViewById(R.id.UpdateButton);
        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = 0;
                float price = 1;
                boolean goodData = true;
                String ImageofProduct = ImageOfProduct.getText().toString();
                if (ImageOfProduct.isEmpty()){
//                try {
//                    String ImageOfProduct = imageUri.toString();
//                } catch (NullPointerException e) {
                    Toast badData = Toast.makeText(getApplicationContext(), "No Image for Product", Toast.LENGTH_SHORT);
                    badData.show();
                    goodData = false;
                }
                String NameOfProduct = etName.getText().toString();
                if (NameOfProduct.isEmpty()) {
                    Toast badData = Toast.makeText(getApplicationContext(), "No Name for Product Provided", Toast.LENGTH_SHORT);
                    badData.show();
                    goodData = false;
                } else {
                    try {
                        quantity = Integer.parseInt(etQuantity.getText().toString());
                        if (quantity < 0) {
                            Toast worseData = Toast.makeText(getApplicationContext(), "Quantity must be more than 0", Toast.LENGTH_SHORT);
                            worseData.show();
                            goodData = false;
                        }
                        price = Float.parseFloat(etPrice.getText().toString());
                        if (price < 0) {
                            Toast worseData = Toast.makeText(getApplicationContext(), "Price must be more than 0", Toast.LENGTH_SHORT);
                            worseData.show();
                            goodData = false;
                        }
                    } catch (Exception e) {
                        Toast wurstData = Toast.makeText(getApplicationContext(), "Check your spelling", Toast.LENGTH_SHORT);
                        wurstData.show();
                        goodData = false;
                    }
                }
                if (goodData) {
                    String image = imageUri.toString();
                    int initSold = 0;
                    DbUtils.insertNewProduct(NameOfProduct, quantity, price, image, initSold, getApplicationContext());
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
        Button ImageAdd = (Button) findViewById(R.id.ImageAdd);
        ImageAdd.setOnClickListener(new View.OnClickListener() {
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
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ImageView imageView = (ImageView) findViewById(R.id.ImageOfProduct);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
