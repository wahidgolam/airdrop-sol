package com.zingit.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ItemAdapter extends
        RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private ArrayList<Item> itemList;
    Context context;
    int activityCode;
    public ItemAdapter(ArrayList<Item> itemList, int activityCode) {
        this.itemList = itemList;
        this.activityCode = activityCode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView;
        // Inflate the custom layout
        contactView = inflater.inflate(R.layout.item_rv_view2, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        // Set item views based on your views and data model
        //handle is available
        TextView itemName = holder.itemName;
        TextView itemPrice = holder.itemPrice;
        TextView itemQuantity = holder.itemQuantity;
       // MaterialCardView quantityCard = holder.quantityCard;
        ImageView itemImage = holder.itemImage;
        ImageView addButton = holder.add;
        ImageView subButton = holder.substract;
        //RelativeLayout itemRVLayout = holder.itemRVLayout;

        if(item.getItemImage().startsWith("http"))
            Glide.with(context).load(item.getItemImage()).into(holder.itemImage);


        //render basic data
        itemName.setText(item.getName());
        holder.itemPrice.setText("₹"+(item.getPrice()*getQuantity(item))+".00");


        //handle item not available
        Log.d("AVAILAILITY", item.getName()+item.isAvailableOrNot());
        if(!item.isAvailableOrNot()){
            addButton.setBackgroundColor(context.getColor(R.color.divider_grey));
            addButton.setClickable(false);
            ColorMatrix matrix = new ColorMatrix();
            ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
            itemImage.setColorFilter(cf);
        }
        int quantity = getQuantity(item);
        //setting visibility of sub button and text view
        if(quantity>0){
            //deal with not available
            if(item.isAvailableOrNot()) {
                subButton.setVisibility(View.VISIBLE);
                itemQuantity.setVisibility(View.VISIBLE);
                itemQuantity.setText(String.valueOf(quantity));
            }
            else{
                updateQuantity(item);
                Toast.makeText(context, "Some items from cart aren't available", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            subButton.setVisibility(View.INVISIBLE);
            itemQuantity.setVisibility(View.INVISIBLE);
        }
        //add button click listener
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.isAvailableOrNot()) {
                    //setting visibility of sub button and text view
                    subButton.setVisibility(View.VISIBLE);
                    itemQuantity.setVisibility(View.VISIBLE);
                    //updating item to cart and receiving status code
                    int status = Dataholder.cart.updateItem(item, 1);
                    switch (status) {
                        case -1:
                            Toast.makeText(context, "Amount has reached maximum limit", Toast.LENGTH_SHORT).show();
                            break;
                        case 0:
                            //make a confirmation request, attempt to add item from another outlet
                            //directly flushing for now
                            Dataholder.cart.flushCart(Dataholder.currentOutlet.getId(), item);
                            break;
                    }
                    //retrieving item quantity and updating it on display
                    itemQuantity.setText(String.valueOf(getQuantity(item)));
                }
                else {
                    Toast.makeText(context, "Item unavailable😢", Toast.LENGTH_SHORT).show();
                }
                ((CartScreen) context).updateCartDetails();

                holder.itemPrice.setText("₹"+(item.getPrice()*getQuantity(item))+".00");

            }
        });
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(subButton.getVisibility()==View.VISIBLE){
                    int status = Dataholder.cart.updateItem(item, -1);
                    switch (status){
                        case -3:
                        case 3:
                        case 4:
                            subButton.setVisibility(View.INVISIBLE);
                            itemQuantity.setVisibility(View.INVISIBLE);
                            break;
                        case 2:
                            break;
                    }
                    itemQuantity.setText(String.valueOf(getQuantity(item)));

                    if(getQuantity(item)==0){
                        itemList.remove(item);
                        notifyItemRemoved(holder.getAdapterPosition());
                    }
                    ((CartScreen) context).updateCartDetails();

                }
                holder.itemPrice.setText("₹"+(item.getPrice()*getQuantity(item))+".00");
            }
        });
    }

    public int getQuantity(Item item){
        int quantity = Dataholder.cart.findQuantity(item);
        return quantity;
    }
    public void updateQuantity(Item item){
        int pos = Dataholder.cart.findIfItemPresent(item);
        Dataholder.cart.getCartItems().get(pos).setQuantity(0);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void filterlist(ArrayList<Item> items)
    {
        itemList = items;
        notifyDataSetChanged();
    }

    public void filterVegList(ArrayList<Item> vegList)
    {
        itemList = vegList;
        notifyDataSetChanged();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView itemName;
        public TextView itemPrice;
        public TextView itemQuantity;
        public ImageView itemImage;
        //public ImageView vegImage;
        ImageView add,substract;
        public Button addButton;
        public Button subButton;
        public  MaterialCardView quantityCard;
        public RelativeLayout itemRVLayout;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemPrice = (TextView) itemView.findViewById(R.id.item_price);
            itemImage = (ImageView) itemView.findViewById(R.id.item_image);
            add = itemView.findViewById(R.id.add);
            substract = itemView.findViewById(R.id.substract);
            itemQuantity = (TextView) itemView.findViewById(R.id.item_quantity);

            context = itemView.getContext();
        }
    }

}