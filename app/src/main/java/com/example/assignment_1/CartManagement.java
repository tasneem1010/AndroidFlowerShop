package com.example.assignment_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CartManagement {

    private SharedPreferences sharedPreferences;
    private SharedPreferences itemsSharedPref;

    public CartManagement(SharedPreferences sharedPreferences, SharedPreferences itemsSharedPref) {
        this.sharedPreferences = sharedPreferences;
        this.itemsSharedPref = itemsSharedPref;
    }

    public ArrayList<Item> loadCartFromPrefs() {
        String json = sharedPreferences.getString("cart", null); //Return null instead of empty string

        if (json == null) {
            return new ArrayList<>();
        }
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Item>>() {
            }.getType(); // source: https://stackoverflow.com/questions/18544133/parsing-json-array-into-java-util-list-with-gson
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            Log.e("MainActivity", "Error loading cart from shared preferences", e);
            return new ArrayList<Item>();
        }
    }

    public void addToCart(Item item) {
        ArrayList<Item> cart = loadCartFromPrefs();
        cart.add(item);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cart); //convert the updated cart to JSON
        editor.putString("cart", json);
        editor.apply();
    }

    public void removeFromCart(Item item) {
        ArrayList<Item> cart = loadCartFromPrefs();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (getItemMatchIndex(item) != -1) {
            cart.remove(getItemMatchIndex(item));
            Gson gson = new Gson();
            String json = gson.toJson(cart); //convert the updated cart to JSON
            Log.d("main", json);
            editor.putString("cart", json);
            editor.apply();
        }

    }

    public void clearCart() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public double getTotalPrice() {
        ArrayList<Item> cart = loadCartFromPrefs();
        double totalPrice = 0;
        for (Item item : cart) {
            totalPrice += item.getPrice();
        }
        return totalPrice;
    }

    private int getItemMatchIndex(Item item) {
        ArrayList<Item> cart = loadCartFromPrefs();
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getName().equals(item.getName())) {
                return i;
            }
        }
        return -1;

    }
    public void checkout() {
        //clear cart
        //decrease stock


        try {
            SharedPreferences.Editor editor =  itemsSharedPref.edit();
            Gson gson = new Gson();
            ArrayList<ArrayList<Item>> allItems = loadItemsFromPrefs();

            for (ArrayList<Item> cat : allItems) {
                for (Item item : cat) {
                    for (Item cartItem : loadCartFromPrefs()) {
                        if (item.getName().equals(cartItem.getName())) {
                            item.quantity--;
                        }
                    }
                }
            }

            String json = gson.toJson(allItems);
            editor.putString("items", json); //save to shared preferences as json
            editor.apply();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        clearCart();
    }

    public ArrayList<ArrayList<Item>> loadItemsFromPrefs() {
        String json = itemsSharedPref.getString("items", "");

        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<ArrayList<Item>>>() {}.getType(); // source: https://stackoverflow.com/questions/18544133/parsing-json-array-into-java-util-list-with-gson
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            Log.e("CartManagement", "Error loading items from shared preferences", e);
            e.printStackTrace();
            return new ArrayList<ArrayList<Item>>();
        }
    }
}
