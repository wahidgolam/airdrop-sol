package com.zingit.user;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zingit.user.model.CampusChooser;

import java.util.ArrayList;

public class CampusAdapter extends RecyclerView.Adapter<CampusAdapter.ViewHolder>{

    ArrayList<CampusChooser> campusList;
    Context context;

    public CampusAdapter(ArrayList<CampusChooser> campusList) {
        this.campusList = campusList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.campus_rv_view, parent, false);
        // Inflate the custom layout
        // Return a new holder instance
        CampusAdapter.ViewHolder viewHolder = new CampusAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    //731224

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CampusChooser campusChooser = campusList.get(position);
        TextView campusName = holder.campusName;
        TextView campusSubName = holder.campusSubName;
        ImageView isChosenImage = holder.isChosenImage;
        RelativeLayout layout = holder.layout;

        campusName.setText(campusChooser.getCampus().getName());
        campusSubName.setText(campusChooser.getCampus().getSubName());
        if(campusChooser.isSelected()){
            isChosenImage.setImageResource(R.drawable.selected_campus);
        }
        else{
            isChosenImage.setImageResource(R.drawable.unselected_campus);
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("Adapter Position")
                ((ChooseCampus)context).campusSelected(campusList.get(holder.getAdapterPosition()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return campusList.size();
    }

    public void filterlist(ArrayList<CampusChooser> items)
    {
        campusList = items;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView campusName;
        public TextView campusSubName;
        public ImageView isChosenImage;
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            campusName = itemView.findViewById(R.id.campus_name);
            campusSubName = itemView.findViewById(R.id.campus_subname);
            isChosenImage = itemView.findViewById(R.id.is_chosen);
            layout = itemView.findViewById(R.id.campus_rv_layout);
            context = itemView.getContext();
        }
    }
}
