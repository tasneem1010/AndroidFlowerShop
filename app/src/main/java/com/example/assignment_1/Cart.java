package com.example.assignment_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {

    TextView tvCartCount;
    RecyclerView rvCartItems;
    Button btnBack;
    Button btnCheckout;
    TextView tvTotalPrice;
    SharedPreferences preferences;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setUpViews();
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateCart();
    }

    private void setUpViews() {
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        tvCartCount = findViewById(R.id.tvCartCount);
        rvCartItems = findViewById(R.id.rvCart);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvCartItems.setLayoutManager(staggeredGridLayoutManager);
        CartManagement cartManagement = new CartManagement(getSharedPreferences("cart", MODE_PRIVATE), getSharedPreferences("items", MODE_PRIVATE));
        adapter = new ItemAdapter(cartManagement.loadCartFromPrefs(),true);
        rvCartItems.setAdapter(adapter);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(view -> {
            try {
                if (cartManagement.loadCartFromPrefs().isEmpty()) {
                    Toast.makeText(this, "Cart is empty", Toast.LENGTH_LONG).show(); //source: https://stackoverflow.com/questions/7738509/how-to-make-custom-toast-message-to-take-the-whole-screen
                    return;
                }
                addOrder(cartManagement.loadCartFromPrefs());
                Toast.makeText(this, "Order of " + cartManagement.getTotalPrice() + " Has Been Placed", Toast.LENGTH_LONG).show(); //notify user. source: https://stackoverflow.com/questions/7738509/how-to-make-custom-toast-message-to-take-the-whole-screen
                cartManagement.checkout(); //checkout
                updateCart();
                tvTotalPrice.setText("Total Price: 0 ₪");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText("Total Price: " + cartManagement.getTotalPrice());
    }
    private void updateCart() {
        CartManagement cartManagement = new CartManagement(getSharedPreferences("cart", MODE_PRIVATE), getSharedPreferences("items", MODE_PRIVATE));
        ArrayList<Item> cartItems = cartManagement.loadCartFromPrefs();
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        tvCartCount.setText("Number of items: " + cartItems.size());
        adapter.itemList = cartItems; //replace the dataset
        adapter.notifyDataSetChanged(); //refresh
    }
    public void addOrder(ArrayList<Item> order) {
        ArrayList<ArrayList<Item>> orders = loadOrdersFromPrefs();
        if (orders == null) {
            orders = new ArrayList<>();
        }
        Log.d("Order",order.toString());
        orders.add(order);
        Log.d("Order",orders.toString());
        SharedPreferences.Editor editor = getSharedPreferences("orders", MODE_PRIVATE).edit();
        editor.putString("orders", new Gson().toJson(orders));
        editor.apply();
    }

    private ArrayList<ArrayList<Item>> loadOrdersFromPrefs() {
        preferences = getSharedPreferences("orders", MODE_PRIVATE);
        String json = preferences.getString("orders", null);
        if (json == null) {
            return new ArrayList<>();
        }
        try {
            Type listType = new TypeToken<ArrayList<ArrayList<Item>>>(){}.getType();
            return new Gson().fromJson(json, listType);
        } catch (Exception e) {
            Log.e("YourClass", "Error loading orders", e);
            return new ArrayList<>();
        }
    }

}