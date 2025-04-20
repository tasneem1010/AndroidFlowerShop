package com.example.assignment_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.slider.RangeSlider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {
    //todo add more search functions

    Button btnBack;
    EditText search_bar;
    Spinner spnCat;
    TextView tvResCount;
    RecyclerView rvResult;
    Switch swInStock;
    RangeSlider rangeSlider;
    ItemAdapter adapter;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState},Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setUpViews();
    }


    String name = null;
    String category = "All";
    float min = 0;
    float max = 120f;
    boolean inStock = false;

    /**
     * set up views
     */
    public void setUpViews() {
        try {
            btnBack = findViewById(R.id.btnBack);
            btnBack.setOnClickListener(view -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
            search_bar = findViewById(R.id.search_bar);
            spnCat = findViewById(R.id.spnCat);
            bindSpinner(spnCat, new String[]{"All", "Flowers", "Bouquets", "Pots"});
            tvResCount = findViewById(R.id.tvResCount);
            rvResult = findViewById(R.id.rvResult);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
            rvResult.setLayoutManager(staggeredGridLayoutManager);
            adapter = new ItemAdapter(new ArrayList<>(), false);
            rvResult.setAdapter(adapter);

            rangeSlider = findViewById(R.id.slider);
            rangeSlider.setValues(0f, 120f); //initial min/max
            rangeSlider.addOnChangeListener((slider, value, fromUser) -> {
                List<Float> values = slider.getValues();
                min = values.get(0);
                max = values.get(1);
                Log.d("Search", "Range: " + min + " - " + max);
                doSearch();
            });
            swInStock = findViewById(R.id.inStock);

            search_bar.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    name = search_bar.getText().toString().trim().toLowerCase();
                    Log.d("Search", "name: " + name);
                    doSearch();
                }
            });
            spnCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    category = spnCat.getSelectedItem().toString();
                    Log.d("Search", "category: " + category);
                    doSearch();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            swInStock.setOnCheckedChangeListener((buttonView, isChecked) -> {
                inStock = isChecked;
                Log.d("Search", "inStock: " + inStock);
                doSearch();

            });
        } catch (
                Exception e) {
            Log.d("Search", e.getMessage());
        }
    }
    public void doSearch(){
        ArrayList<Item> searchResults = searchData(name, category, min, max, inStock);
        adapter.updateItems(searchResults);
        tvResCount.setText("Results: " + searchResults.size());
    }



    /**
     * bind string array to spinner
     *
     * @param spn     Spinner to bind
     * @param strings String array to bind
     */
    private void bindSpinner(Spinner spn, String[] strings) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
        spn.setAdapter(adapter);
    }

    private ArrayList<Item> searchData(String name, String category, float min, float max,
                                       boolean inStock) {
        SharedPreferences prefrences = getSharedPreferences("items", MODE_PRIVATE);
        ArrayList<ArrayList<Item>> items;
        try {
            //read items if exist and convert to arraylist
            // start with array having all items
            items = new Gson().fromJson(prefrences.getString("items", null), new TypeToken<ArrayList<ArrayList<Item>>>() {
            }.getType());
        } catch (Exception e) {
            Log.d("Search", "error reading items from prefs");
            return null;
        }
        if (items == null) return new ArrayList<>();
        //filter by category or flatten
        Log.d("Search", "before search: " + items.toString());
        ArrayList<Item> result = categoryFilter(category, items);
        Log.d("Search", "by category: " + result.toString());
        //filter by name
        if (name != null && !name.isEmpty()) {
            result = filterByName(name, result);
            if (result.isEmpty()) {
                return new ArrayList<>();
            }
        }
        Log.d("Search", "by name: " + result);
        //filter by price
        if (min != 0 || max != 120f) {
            result = filterByPrice(min, max, result);
            if (result.isEmpty()) {
                return new ArrayList<>();
            }
        }
        Log.d("Search", "by price: " + result);
        //filter by in stock
        if (inStock) {
            result = filterByStock(result);
            if (result.isEmpty()) {
                return new ArrayList<>();
            }
        }
        Log.d("Search", "final result: " + result);
        return result;

    }

    private ArrayList<Item> filterByStock(ArrayList<Item> result) {
        ArrayList<Item> result2 = new ArrayList<>();
        for (Item item :
                result) {
            // only items in stock are wanted
            if (item.getQuantity() != 0) {
                result2.add(item);
            }
        }
        return result2;
    }

    private ArrayList<Item> filterByPrice(float min, float max, ArrayList<Item> result) {
        if (min == 0 && max == 120f) return result;
        ArrayList<Item> result2 = new ArrayList<>();
        for (Item item :
                result) {
            if (!(item.getPrice() < min || item.getPrice() > max)) {
                result2.add(item);
            }
        }
        return result2;
    }

    private ArrayList<Item> filterByName(String name, ArrayList<Item> result) {
        ArrayList<Item> result2 = new ArrayList<>();
        for (Item item :
                result) {
            if (item.getName().toLowerCase().contains(name.toLowerCase())) {
                result2.add(item);
            }
        }
        return result2;
    }

    private ArrayList<Item> categoryFilter(String
                                                   category, ArrayList<ArrayList<Item>> items) {
        if (category == null || category.equals("All")) {
            ArrayList<Item> allItems = new ArrayList<>();
            for (ArrayList<Item> categoryList : items) { //if no category only flatten arraylist
                allItems.addAll(categoryList);
            }
            return allItems;
        }
        // if category was selected return needed list
        int cat = 0;
        switch (category) {
            case "Flowers":
                break;
            case "Bouquets":
                cat = 1;
                break;
            case "Pots":
                cat = 2;
                break;
        }
        return items.get(cat);
    }

}