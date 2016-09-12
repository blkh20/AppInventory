package com.example.android.appinventory;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    ArrayList<Product> products = new ArrayList<>();

    public ProductAdapter(Activity context, ArrayList<Product> products) {
        super(context, 0, products);
    }

    ProductDbHandler db = new ProductDbHandler(getContext());

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_products, parent, false);
        }

        final Product currentProduct = getItem(position);

        Button btnSell = (Button) listItemView.findViewById(R.id.list_sell_btn);

        TextView tvProductName = (TextView) listItemView.findViewById(R.id.list_product_name);
        tvProductName.setText(currentProduct.getName());

        final TextView tvInStock = (TextView) listItemView.findViewById(R.id.list_product_stock);
        tvInStock.setText(Integer.toString(currentProduct.getStock()));

        TextView tvProductPrice = (TextView) listItemView.findViewById(R.id.list_product_price);
        tvProductPrice.setText("$" + Float.toString(currentProduct.getPrice()));

        final TextView tvProductSold = (TextView) listItemView.findViewById(R.id.list_product_sold);
        tvProductSold.setText(Integer.toString(currentProduct.getSales()));

        btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stock = Integer.parseInt(tvInStock.getText().toString());
                int sold = Integer.parseInt(tvProductSold.getText().toString());

                if (stock > 0) {
                    stock--;
                    Log.d("btnSell stock: ", String.valueOf(stock));
                    sold++;
                    Log.d("btnSell sales: ", String.valueOf(sold));
                    currentProduct.setSales(sold);
                    currentProduct.setStock(stock);
                    tvInStock.setText(Integer.toString(stock));
                    tvProductSold.setText(Integer.toString(sold));
                    db.updateProduct(currentProduct);
                }

            }
        });

        return listItemView;
    }

}