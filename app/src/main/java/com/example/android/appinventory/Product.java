package com.example.android.appinventory;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private String image;
    private float price;
    private int stock;
    private int sales;
    private String supplier;

    public Product() {
    }

    public Product(int id, String name, String image, float price, int stock, int sales, String supplier) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.stock = stock;
        this.sales = sales;
        this.supplier = supplier;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) { this.image = image; }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public float getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public int getSales() {
        return sales;
    }

    public String getSupplier() {
        return supplier;
    }
}