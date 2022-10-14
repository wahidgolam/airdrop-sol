package com.zingit.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class OrderItemAdapter1 extends RecyclerView.Adapter<OrderItemAdapter1.ViewHolder>{
    private ArrayList<Order> orderList;
    Context context;

    public OrderItemAdapter1(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.order_rv_view, parent, false);
        // Return a new holder instance
        OrderItemAdapter1.ViewHolder viewHolder = new OrderItemAdapter1.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(holder.getAdapterPosition());

        TextView outletNameOrderID = holder.outletNameOrderID;
        TextView itemNameQuantity = holder.itemNameQuantity;
        TextView timerText = holder.timerText;
        TextView statusText = holder.statusText;
        MaterialCardView orderCard = holder.orderCard;
        //render basic data
        String orderId = order.getOrderID();
        String outletDetails = findOutletName(order.getOutletID())+ " #"+orderId.substring(orderId.length()-5);
        String name = order.getItemName();
        String itemDetails = name+ " x"+ order.getQuantity();
        final String[] timerDetails = {""};
        final String[] statusDetails = {""};
        Timer timer = new Timer();
        switch(order.getStatusCode()){
            case 0:
                orderCard.setBackground(AppCompatResources.getDrawable(context, R.drawable.alert));
                timerText.setTextColor(Color.BLACK);
                statusText.setTextColor(Color.BLACK);
                itemNameQuantity.setTextColor(Color.BLACK);
                outletNameOrderID.setTextColor(Color.GRAY);
                timerText.setText("decl.");
                statusText.setText("processing refund");
                break;
            case 1:
                //timerText.setText("-:-");
                statusText.setText("approval pending");
                break;
            case 2:
                //timerText.setText("-:-");
                statusText.setText("food is preparing");
                final Runnable setTextViewUpdateRunnable = new Runnable() {
                    public void run() {
                        if(!timerText.getText().equals("done.")) {
                            timerText.setText(timerDetails[0]);
                            statusText.setText(statusDetails[0]);
                        }
                    }
                };
                TimerTask task = new TimerTask(){
                    public void run() {
                        Timestamp zingTime = order.getZingTime();
                        Date date = new Date();
                        Timestamp nowTime = new Timestamp(date);
                        long seconds = zingTime.getSeconds() - nowTime.getSeconds();
                        timerDetails[0] = ""+seconds/60+":"+((seconds%60<10)?"0"+seconds%60:seconds%60);
                        statusDetails[0] = "food is preparing";
                        Log.d("status code", String.valueOf(order.getStatusCode()));
                        if(seconds<=0 || order.getStatusCode()==3) {
                            Log.d("status code", String.valueOf(order.getStatusCode()));
                            timerDetails[0] = String.valueOf(order.getOtp());
                            statusDetails[0] = "zing time expired";
                            cancel();
                        }
                        ((OrderHistory) context).runOnUiThread(setTextViewUpdateRunnable);
                    }
                };
                timer.schedule(task, 0, 1000);
                break;
            case 3:
                Log.d("IDHAR", "AA RHE H");
                orderCard.setBackground(AppCompatResources.getDrawable(context, R.drawable.orange_gradient));
                timerText.setText("done.");
                statusText.setText("tap to access QR");
                timer.cancel();
                break;
        }
        outletNameOrderID.setText(outletDetails);
        itemNameQuantity.setText(itemDetails);

        orderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(order.getStatusCode()==3) {
                    Intent intent = new Intent(context, QRScreen.class);
                    intent.putExtra("text", order.getOrderID());
                    context.startActivity(intent);
                }
                else{
                    //do nothing
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public String findOutletName(String outletID){
        for(int i=0; i<Dataholder.outletList.size(); i++){
            if(Dataholder.outletList.get(i).getId().equals(outletID)){
                return Dataholder.outletList.get(i).getName();
            }
        }
        return "";
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView outletNameOrderID;
        public TextView itemNameQuantity;
        public TextView timerText;
        public TextView statusText;
        MaterialCardView orderCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //outletNameOrderID = itemView.findViewById(R.id.outlet_name_id);
            //itemNameQuantity = itemView.findViewById(R.id.item_name_quantity);
            //timerText = itemView.findViewById(R.id.timer);
            //statusText = itemView.findViewById(R.id.status_text);
            //orderCard = itemView.findViewById(R.id.order_card);
            context = itemView.getContext();
        }
    }
}

