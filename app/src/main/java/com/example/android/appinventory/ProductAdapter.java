package com.example.android.appinventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by BlkH20 on 9/8/2016.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    public ProductAdapter(Context context, ArrayList<Product> products){
        super(context,0,products);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_products,parent,false);
        }

        final Product currentProduct = getItem(position);

        TextView productNameTextView = (TextView) listItemView.findViewById(R.id.NameOfProduct);
        productNameTextView.setText(currentProduct.getProductName());

        final TextView quantityTextView = (TextView) listItemView.findViewById(R.id.Sale);
        quantityTextView.setText(Integer.toString(currentProduct.getQuantity()));

        TextView priceTextView =(TextView) listItemView.findViewById(R.id.PriceOfProduct);
        String priceTag = "$" + currentProduct.getPrice();
        priceTextView.setText(priceTag);

        final TextView soldTextView = (TextView) listItemView.findViewById(R.id.AmountOfSale);
        String soldString = "Sold:"+ currentProduct.getSold();
        soldTextView.setText(soldString);

        Button makeSale = (Button) listItemView.findViewById(R.id.SaleButton);
        makeSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newJosh = currentProduct.getQuantity() - 1;
                if(!(newJosh < 0)){
                    int newRocks = currentProduct.getSold() + 1;
                    Utility.verifyStoragePermissions(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY,
                            Integer.toString(newJosh),
                            currentProduct.getProductName(),
                            getContext());
                    Utility.verifyStoragePermissions(InventoryContract.ProductEntry.COLUMN_NAME_SOLD,
                            Integer.toString(newRocks),
                            currentProduct.getProductName(),
                            getContext());
                    currentProduct.setQuantity(newJosh);
                    currentProduct.setSold(newRocks);

                    String soldString = "Sold:"+ currentProduct.getSold();
                    soldTextView.setText(soldString);

                    quantityTextView.setText(Integer.toString(currentProduct.getQuantity()));
                }
            }
        });

        return listItemView;
    }
}
