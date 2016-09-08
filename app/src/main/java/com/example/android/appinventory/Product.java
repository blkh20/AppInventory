package com.example.android.appinventory;

/**
 * Created by BlkH20 on 9/8/2016.
 */
public class Product {
    private String mProductName;
    private int mQuantity;
    private float mPrice;
    private int mSold;


    public Product(String name,int quantity,float price, int sold){
        mProductName = name;
        mQuantity = quantity;
        mPrice = price;
    }

    public String getProductName() {
        return mProductName;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public float getPrice() {
        return mPrice;
    }

    public int getSold(){return mSold;}

    public void setSold(int sold){
        mSold = sold;
    }

    public void setQuantity(int quantity){
        mQuantity = quantity;
    }


}
