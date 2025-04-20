package com.example.assignment_1;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    ArrayList<Item> itemList;
    boolean isCart;

    public ItemAdapter(ArrayList<Item> items, boolean isCart) {
        this.itemList = items;
        this.isCart = isCart;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.flower_card,
                parent,
                false);
        return new ViewHolder(v);
    }

    public void updateItems(ArrayList<Item> items) {
        this.itemList = items;
notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.imageView.setImageDrawable(null);

        CardView cardView = holder.cardView;
        ImageView imageView = (ImageView) cardView.findViewById(R.id.iv);
        Drawable dr = ContextCompat.getDrawable(cardView.getContext(), itemList.get(position).getImage());
        imageView.setImageDrawable(dr);
        TextView txt = (TextView) cardView.findViewById(R.id.tvName);
        txt.setText(itemList.get(position).getName());
        TextView price = (TextView) cardView.findViewById(R.id.tvPrice);
        price.setText(String.valueOf(String.format("%.2f ₪", item.getPrice())));
        TextView tvQuantity = (TextView) cardView.findViewById(R.id.tvQuantity);
        int q = itemList.get(position).getQuantity();
        if (q <= 0) {
            tvQuantity.setText("Out of Stock");
        } else {
            tvQuantity.setText("In Stock: " + itemList.get(position).getQuantity());
        }
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo open item details
            }
        });
        if (isCart) {
            holder.btnAddToCart.setVisibility(View.GONE);
            holder.btnRemoveFromCart.setVisibility(View.VISIBLE);
        } else {
            holder.btnAddToCart.setVisibility(View.VISIBLE);
            holder.btnRemoveFromCart.setVisibility(View.GONE);
        }
        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartManagement CartManagement = new CartManagement(v.getContext().getSharedPreferences("cart", MODE_PRIVATE), v.getContext().getSharedPreferences("items", MODE_PRIVATE));
                Item item = itemList.get(holder.getAdapterPosition());
                if(item.quantity<=0) {
                    Toast.makeText(v.getContext(), "Item is out of stock", Toast.LENGTH_LONG).show();
                    return;
                }
                CartManagement.addToCart(item);
            }
        });
        holder.btnRemoveFromCart.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) { // if the item is still in the list (not -1)
                CartManagement cartManager = new CartManagement(v.getContext().getSharedPreferences("cart", MODE_PRIVATE), v.getContext().getSharedPreferences("items", MODE_PRIVATE));//remove item from cart
                cartManager.removeFromCart(itemList.get(pos));
                //update the adapter's dataset
                itemList.remove(pos);
                //notify RecyclerView of the removal
                notifyItemRemoved(pos);
            }
        });
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        private CardView cardView;
        ImageButton btnAddToCart, btnRemoveFromCart;


        public ViewHolder(@NonNull CardView cardView) {
            super(cardView);
            this.cardView = cardView;
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnRemoveFromCart = itemView.findViewById(R.id.btnRemoveFromCart);
            imageView = itemView.findViewById(R.id.iv);
        }
    }

}
