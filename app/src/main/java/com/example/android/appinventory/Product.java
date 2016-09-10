package com.example.android.appinventory;

public class Product {
    private String mProductName;
    private int mQuantity;
    private float mPrice;
    private int mSold;
    public Product(String ProductName,int Quantity, float Price, int sold){
        mProductName = ProductName;
        mQuantity = Quantity;
        mPrice = Price;
        mSold = sold;
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
