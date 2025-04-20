package com.example.assignment_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class MainActivity extends AppCompatActivity {

    RecyclerView RVFlowers;
    RecyclerView RVBouquets;
    RecyclerView RVPots;
    ImageButton btnCart;
    ImageButton btnOpenSearch;
    ItemAdapter adapter0;
    ItemAdapter adapter1;
    ItemAdapter adapter2;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
            createData();
            setUpViews();
    }

    private void createData() {
        SharedPreferences preferences = getSharedPreferences("items", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(!preferences.contains("items")) { //initialize data if it doesn't exist
            Log.d("MainActivity", "Initializing items");

            ArrayList<List<Item>> allItems = new ArrayList<>();
            List<Item> flowers = Arrays.asList(
                    new Item("Rose", R.drawable.rose, 8, 15, "Flower"),
                    new Item("Lily", R.drawable.lily, 9, 15, "Flower"),
                    new Item("Tulip", R.drawable.tulip, 10, 15, "Flower"),
                    new Item("Daisy", R.drawable.daisy, 7, 15, "Flower"));
            List<Item> bouquets = Arrays.asList(
                    new Item("Cotton Candy Bouquet", R.drawable.cotton_candy_bouquet, 60, 6, "Bouquet"),
                    new Item("Sunflower Bouquet", R.drawable.sunflower_bouquet, 50, 6, "Bouquet"),
                    new Item("Rose and Lily Bouquet", R.drawable.rose_and_lily_bouquet, 80, 5, "Bouquet"),
                    new Item("Yellow Flower Bouquet", R.drawable.yellow_flower_bouquet, 70, 4, "Bouquet"));

            List<Item> pots = Arrays.asList(
                    new Item("Rainbow Tulip Plant", R.drawable.rainbow_tulip_plant, 100, 2, "Pot"),
                    new Item("Pink Tulip Plant", R.drawable.pink_tulip_plant, 80, 2, "Pot"),
                    new Item("White Orchid Plant", R.drawable.white_orchid_plant, 90, 2, "Pot"),
                    new Item("Pink Azalea Plant", R.drawable.pink_azalea_plant, 120, 3, "Pot"),
                    new Item("Pink Rose Plant", R.drawable.pink_rose_plant, 110, 1, "Pot"));

            allItems.add(flowers);
            allItems.add(bouquets);
            allItems.add(pots);
            try {
                Gson gson = new Gson();
                String json = gson.toJson(allItems);
                editor.putString("items", json); //save to shared preferences as json
                Log.d("MainActivity", "JSON: " + json);
                editor.apply();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        if(!preferences.contains("cart")) {
            ArrayList<Item> cart = new ArrayList<>();
            Gson gson = new Gson();
            String json = gson.toJson(cart);
            editor.putString("cart", json); //save cart to shared preferences as json
            Log.d("MainActivity", "cart JSON: " + json);
            editor.apply();
        }


    }


    public void setUpViews() {
        ArrayList<ArrayList<Item>> items = loadItemsFromPrefs();
        RVFlowers = findViewById(R.id.RVFlowers);
        RVFlowers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter0 = new ItemAdapter(items.get(0),false);
        RVFlowers.setAdapter(adapter0);
        Log.d("MainActivity", "Items: " + items);
        RVBouquets = findViewById(R.id.RVBouquets);
        RVBouquets.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter1 = new ItemAdapter(items.get(1),false);
        RVBouquets.setAdapter(adapter1);
        RVPots = findViewById(R.id.RVPots);
        RVPots.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter2 = new ItemAdapter(items.get(2),false);
        RVPots.setAdapter(adapter2);
        btnCart = findViewById(R.id.btnCart);
        btnCart.setOnClickListener(view -> {
            Intent intent = new Intent(this, Cart.class);
            startActivity(intent);
        });
        btnOpenSearch = findViewById(R.id.btnOpenSearch);
        btnOpenSearch.setOnClickListener(view -> {
            Intent intent = new Intent(this, Search.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<ArrayList<Item>> items = loadItemsFromPrefs();
        Log.d("MainActivity", "Items: " + items);

        adapter0.updateItems(items.get(0));
        adapter1.updateItems(items.get(1));
        adapter2.updateItems(items.get(2));

    }

    public ArrayList<ArrayList<Item>> loadItemsFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("items", MODE_PRIVATE);
        String json = prefs.getString("items", "");

        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<ArrayList<Item>>>() {}.getType(); // source: https://stackoverflow.com/questions/18544133/parsing-json-array-into-java-util-list-with-gson
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            Log.e("MainActivity", "Error loading items from shared preferences", e);
            e.printStackTrace();
            return new ArrayList<ArrayList<Item>>();
        }
    }



}