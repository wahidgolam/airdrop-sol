package com.zingit.user;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartItemAdapter extends
        RecyclerView.Adapter<CartItemAdapter.ViewHolder>{

    private ArrayList<Item> itemList;
    Context context;
    private static final DecimalFormat df = new DecimalFormat("0.00");





    public CartItemAdapter(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public CartItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_rv_view2, parent, false);
        // Inflate the custom layout
        // Return a new holder instance
        CartItemAdapter.ViewHolder viewHolder = new CartItemAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position);
        // Set item views based on your views and data model
        //handle is available
        TextView itemName = holder.itemName;
        TextView itemPrice = holder.itemPrice;
        TextView itemQuantity = holder.itemQuantity;
        MaterialCardView quantityCard = holder.quantityCard;
        ImageView itemImage = holder.itemImage;
        Button addButton = holder.addButton;
        Button subButton = holder.subButton;
        ImageView vegImage = holder.vegImage;
        RelativeLayout itemRVLayout = holder.itemRVLayout;


        String priceDisplayText = "₹ "+df.format(item.getPrice());

        itemName.setText(item.getName());
        itemPrice.setText(priceDisplayText);
        if(item.isVegOrNot()) {
            vegImage.setImageResource(R.drawable.veg);
        }
        else{
            vegImage.setImageResource(R.drawable.nonveg);
        }
        //handle item not available
        int quantity = getQuantity(item);
        Log.d(item.getName(), String.valueOf(quantity));
        if(quantity>0){
            subButton.setVisibility(View.VISIBLE);
            quantityCard.setVisibility(View.VISIBLE);
            itemQuantity.setText(String.valueOf(quantity));
        }
        else{
            subButton.setVisibility(View.INVISIBLE);
            quantityCard.setVisibility(View.INVISIBLE);
        }
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subButton.setVisibility(View.VISIBLE);
                quantityCard.setVisibility(View.VISIBLE);
                int status = Dataholder.cart.updateItem(item, 1);
                switch (status){
                    case -1:
                        Toast.makeText(context, "Amount has reached maximum limit", Toast.LENGTH_SHORT).show();
                        break;
                    case 0:
                        //make a confirmation request
                        Dataholder.cart.flushCart(Dataholder.currentOutlet.getId(), item);
                        break;
                }
                int quantity = getQuantity(item);
                itemQuantity.setText(String.valueOf(quantity));
                ((CartScreen) context).updateCartDetails();
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
                            quantityCard.setVisibility(View.INVISIBLE);
                            break;
                        case 2:
                            break;
                    }
                    int quantity = getQuantity(item);
                    itemQuantity.setText(String.valueOf(quantity));
                    if(quantity==0){
                        itemList.remove(item);
                        notifyItemRemoved(holder.getAdapterPosition());
                    }
                    ((CartScreen) context).updateCartDetails();
                }
            }
        });
    }
    public int getQuantity(Item item){
        int quantity = Dataholder.cart.findQuantity(item);
        return quantity;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView itemName;
        public TextView itemPrice;
        public TextView itemQuantity;
        public ImageView itemImage;
        public ImageView vegImage;
        public Button addButton;
        public Button subButton;
        public  MaterialCardView quantityCard;
        public RelativeLayout itemRVLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemPrice = (TextView) itemView.findViewById(R.id.item_price);
            itemImage = (ImageView) itemView.findViewById(R.id.item_image);
            vegImage = (ImageView) itemView.findViewById(R.id.veg_image);
           // addButton = (Button) itemView.findViewById(R.id.add_button);
           // subButton = (Button) itemView.findViewById(R.id.sub_button);
            itemQuantity = (TextView) itemView.findViewById(R.id.item_quantity);
            quantityCard = (MaterialCardView) itemView.findViewById(R.id.quantity_card);
            itemRVLayout = (RelativeLayout) itemView.findViewById(R.id.item_rv_layout);
            context = itemView.getContext();
        }
    }
}
