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

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class MenuAdapter extends
        RecyclerView.Adapter<MenuAdapter.ViewHolder>{

     ArrayList<Item> itemList;
    Context context;

    public MenuAdapter(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_rv_view, parent, false);
        MenuAdapter.ViewHolder viewHolder = new MenuAdapter.ViewHolder(contactView);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position);

        Boolean IsVegOrNonVeg = item.isVegOrNot();
        if(IsVegOrNonVeg)
        {
            holder.vegOrNonveg.setImageResource(R.drawable.veg);
        }
        else
        {
            holder.vegOrNonveg.setImageResource(R.drawable.nonveg);
        }

        if(item.getItemImage().startsWith("http"))
            Glide.with(context).load(item.getItemImage()).into(holder.itemImage);

        String itemName = nameManager(item.getName());

        holder.itemName.setText(itemName);
        int price = item.getPrice();
        holder.itemPrice.setText("₹"+String.valueOf(price));
        //TODO: Item name function
        //define state
        //0 -> item not available
        //1 -> item is already present in cart
        //-1 -> item is not present in cart
        //ui updates
        //setup UI
        if(!item.isAvailableOrNot()){
            //state 0
            holder.addButton.setImageResource(R.drawable.disabled_button);
        }
        else{
            if(Dataholder.cart.findIfItemPresent(item)>-1){
                //state 1
                holder.addButton.setImageResource(R.drawable.delete_item);
            }
            else{
                //state -1
                holder.addButton.setImageResource(R.drawable.add_item);
            }
        }
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!item.isAvailableOrNot()){
                    //state 0
                    Toast.makeText(context, "Item is not available currently", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(Dataholder.cart.findIfItemPresent(item)>-1){
                        //state 1
                        Log.d("DELETE", "DEL");
                        int status = Dataholder.cart.updateItem(item, -1);
                        Log.d("Status of deletion", status+"");
                        holder.addButton.setImageResource(R.drawable.add_item);
                        ((Menuscreen) context).setupCartView();
                    }
                    else{
                        //state -1
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
                        Log.d("Status of addition", status+"");
                        holder.addButton.setImageResource(R.drawable.delete_item);
                        ((Menuscreen) context).setupCartView();
                    }
                }

                Log.d("Item quantity", Dataholder.cart.findQuantity(item)+"");
            }
        });
        //define state


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
    public class ViewHolder extends RecyclerView.ViewHolder{
       ImageView itemImage;
       TextView itemName;
       ImageView vegOrNonveg;
       TextView itemPrice;
       ImageView addButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            vegOrNonveg = itemView.findViewById(R.id.veg_image);
            itemPrice = itemView.findViewById(R.id.price);
            addButton = itemView.findViewById(R.id.add_button);


            context = itemView.getContext();
        }
    }
    public String nameManager(String item)
    {
        int c = 0;
        String ans="";
        for(int i=0;i<item.length();i++)
        {
            char a = item.charAt(i);
            if(a==' ')
            {
                c++;
            }
            if(c==2)
            {
                ans = ans + "\n" + item.substring(i+1,item.length());
                return ans;
            }
            else
            {
                ans = ans + a;
            }
        }
        return ans;
    }
}