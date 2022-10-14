package com.zingit.user;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ResultScreenAdapter extends
        RecyclerView.Adapter<ResultScreenAdapter.ViewHolder>{

    private ArrayList<CartItem> cartItems;
    Context context;

    public ResultScreenAdapter(ArrayList<CartItem> cartlist) {
        this.cartItems = cartlist;
    }

    @NonNull
    @Override
    public ResultScreenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_dets, parent, false);
        ResultScreenAdapter.ViewHolder viewHolder = new ResultScreenAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cart = cartItems.get(position);
        holder.itemName.setText(cart.getItem().getName()+"");
        holder.itemQuantity.setText("X "+cart.getQuantity()+" ");


    }






    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemName;
        TextView itemQuantity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemQuantity = itemView.findViewById(R.id.item_quantity);


            context = itemView.getContext();
        }
    }

}
