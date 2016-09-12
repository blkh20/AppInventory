package com.example.android.appinventory.Product;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.android.appinventory.Data.ProductDbHandler;
import com.example.android.appinventory.Data.ProductValidator;
import com.example.android.appinventory.R;

import java.io.FileDescriptor;
import java.io.IOException;

public class ProductDetails extends AppCompatActivity {
    private static final String TAG = "ProductDetails";
    ProductValidator pv = new ProductValidator();
    ProductDbHandler db = new ProductDbHandler(this);
    Product pc;
    @Override
    public void setActionBar(Toolbar toolbar) {
        super.setActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle(R.string.product_detail_title);
        actionBar.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        TextView textViewProductName = (TextView) findViewById(R.id.detail_prod_name_output);
        TextView textViewProductPrice = (TextView) findViewById(R.id.detail_prod_price_output);
        final TextView textViewProductQty = (TextView) findViewById(R.id.detail_prod_qty_output);
        TextView textViewProductSold = (TextView) findViewById(R.id.textViewProdSold2);
        ImageView iv = (ImageView) findViewById(R.id.detail_image_view);

        pc = (Product) getIntent().getSerializableExtra("class");
        textViewProductName.setText(pc.getName());
        textViewProductPrice.setText("$" + Float.toString(pc.getPrice()));
        textViewProductQty.setText(Integer.toString(pc.getStock()));
        textViewProductSold.setText(Integer.toString(pc.getSales()));
        iv.setImageBitmap(getBitmapFromUri(Uri.parse(pc.getImage())));

        final Button order = (Button) findViewById(R.id.detail_btn_order);
        Button deleteProd = (Button) findViewById(R.id.detail_btn_delete_prod);
        Button adjQty = (Button) findViewById(R.id.detail_btn_adjust_qty);

        final TextView tvModQty = (TextView) findViewById(R.id.detail_mod_qty);
        final LinearLayout adjQty1 = (LinearLayout) findViewById(R.id.detail_adj_qty_1);
        final LinearLayout adjQty2 = (LinearLayout) findViewById(R.id.detail_adj_qty_2);
        final Button btnDoneQty = (Button) findViewById(R.id.done);
        final Button btnCancelQty = (Button) findViewById(R.id.cancel);
        final Button btnUpQty = (Button) findViewById(R.id.up);
        final Button btnDownQty = (Button) findViewById(R.id.down);
        final EditText editQty = (EditText) findViewById(R.id.detail_edit_qty);
        final TextView editQtyError = (TextView) findViewById(R.id.detail_edit_qty_error);

        btnDownQty.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int qty = Integer.parseInt(editQty.getText().toString());
                if (qty > 0) {
                    qty--;
                    editQty.setText(Integer.toString(qty));
                }
            }
        });
        btnUpQty.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int qty = Integer.parseInt(editQty.getText().toString());
                qty++;
                editQty.setText(Integer.toString(qty));
            }
        });

        //not saving to db
        btnCancelQty.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                editQty.setText("");
                setVisToHide(adjQty1, adjQty2, tvModQty, editQtyError);
            }
        });
        //save to db for done
        btnDoneQty.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!pv.checkIsInteger(editQty)) {
                    editQtyError.setVisibility(View.VISIBLE);
                } else {
                    setVisToHide(adjQty1, adjQty2, tvModQty, editQtyError);
                    int qty = Integer.parseInt(editQty.getText().toString());
                    pc.setStock(qty);
                    textViewProductQty.setText(Integer.toString(qty));
                    db.updateProduct(pc);
                    editQty.setText("");//clear text field.
                }

            }
        });
        adjQty.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                tvModQty.setVisibility(View.VISIBLE);
                adjQty1.setVisibility(View.VISIBLE);
                adjQty2.setVisibility(View.VISIBLE);
                editQty.setText(Integer.toString(pc.getStock()));
            }
        });

        deleteProd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(ProductDetails.this);
                builder1.setMessage("Are you sure you want to delete?.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.deleteProduct(pc);
                                Intent intent = new Intent(ProductDetails.this, MainActivity.class);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();


            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{pc.getSupplier()});
                intent.putExtra(Intent.EXTRA_SUBJECT, "ORDER MORE ITEM");
                intent.putExtra(Intent.EXTRA_TEXT, "Product ID: " + pc.getId() + "\n" + "Product name: " + pc.getName());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });
    }

    private void setVisToHide(LinearLayout lv1, LinearLayout lv2, TextView tv, TextView editQtyError) {
        lv1.setVisibility(View.GONE);
        lv2.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        editQtyError.setVisibility(View.GONE);
    }

    private Bitmap getBitmapFromUri(Uri uri) {
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}