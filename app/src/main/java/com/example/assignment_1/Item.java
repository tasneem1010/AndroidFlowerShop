package com.example.assignment_1;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

public class Item {

    String name;
    int price;
    int image;
    int quantity;
    String category;

    public Item() {
        name = "Rose";
        price = 5;
        image = R.drawable.rose;
        quantity = 1;
        category = "Flower";
    }
    public Item(String name, int imageID, int price, int quantity, String catergory){
        this.name = name;
        this.image = imageID;
        this.price = price <= 0 ? 5 : price;
        this.quantity= quantity < 0 ? 1 : quantity;
        this.category = catergory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
    @NonNull
    public String toString(){
        return getName();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
