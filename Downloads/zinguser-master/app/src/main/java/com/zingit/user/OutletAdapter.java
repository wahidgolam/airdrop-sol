package com.zingit.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import club.cred.neopop.PopFrameLayout;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class OutletAdapter extends
        RecyclerView.Adapter<OutletAdapter.ViewHolder> {

    private ArrayList<Outlet> outletList;
    Context context;
    public OutletAdapter(ArrayList<Outlet> outletList) {
        this.outletList = outletList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.outlet_rv_view, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Outlet outlet = outletList.get(position);

        // Set item views based on your views and data model
        TextView restaurantName = holder.restaurantName;
        TextView restaurantDescription = holder.restaurantDescription;
        TextView restaurantZingTime = holder.restaurantZingTime;
        Button restaurantStatusButton = holder.restaurantStatusButton;
        ImageView restaurantPhoto = holder.restaurantPhoto;
        String zingTimeDisplayText;

        //setting data
        if(outlet.getOpenStatus().equals("CLOSED"))
        {
            zingTimeDisplayText = "Closed";
        }
        else
        {
            zingTimeDisplayText = outlet.getZingTime() + " min";
        }

        restaurantName.setText(outlet.getName());
        restaurantDescription.setText(outlet.getDescription());
        restaurantZingTime.setText(zingTimeDisplayText);

        // Restaurant Image Setting
        if(outlet.getOutletImage().startsWith("http")) {
            //Glide.with(context).load(outlet.getOutletImage()).into(restaurantPhoto.setBackground);
            Glide.with(context).load(outlet.getOutletImage()).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Drawable> transition) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        restaurantPhoto.setBackground(resource);
                    }
                }


            });
        }


        //TODO set restaurant photo
      //  restaurantStatusButton.setText(outlet.getOpenStatus());
        if(outlet.getOpenStatus().equals("CLOSED")){
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);  //0 means grayscale
            ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
            restaurantPhoto.setColorFilter(cf);
            restaurantPhoto.setImageAlpha(128);  // 128 = 0.5
        }
        else{
            restaurantPhoto.setColorFilter(null);
            restaurantPhoto.setImageAlpha(255);
        }
        holder.outletCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(outlet.getOpenStatus().equals("OPEN")) {
                    //handle student user object ka currentOutletID
                    //if studentuser.cartOutletid != this outlet id OR !=null throw confirmation -> flash cart(full)
                    Dataholder.currentOutlet = outletList.get(holder.getAdapterPosition());
                    Intent intent = new Intent(context, Menuscreen.class);
                    context.startActivity(intent);
                }
                else{
                    Toast.makeText(context, "Restaurant closed. Please try again after some time", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return outletList.size();
    }

    public void filterList(ArrayList<Outlet> outletList1)
    {
        outletList = outletList1;
        notifyDataSetChanged();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView restaurantName;
        public TextView restaurantDescription;
        public TextView restaurantZingTime;
        public Button restaurantStatusButton;
        public ImageView restaurantPhoto;
        //public CardView outletCard;
        PopFrameLayout outletCard;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            //outletCard = (CardView) itemView.findViewById(R.id.outlet_card);
            restaurantName = (TextView) itemView.findViewById(R.id.outletName);
            restaurantDescription = (TextView) itemView.findViewById(R.id.outlet_category);
            restaurantZingTime = (TextView) itemView.findViewById(R.id.outlet_zing_time);
            //restaurantStatusButton = (Button) itemView.findViewById(R.id.restaurant_status_button);
            restaurantPhoto = (ImageView) itemView.findViewById(R.id.restaurant_image);
            outletCard = (itemView).findViewById(R.id.outletCard);
            context = itemView.getContext();
        }
    }
}