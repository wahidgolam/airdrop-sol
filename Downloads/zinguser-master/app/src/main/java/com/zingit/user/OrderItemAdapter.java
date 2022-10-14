package com.zingit.user;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.Timestamp;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder>{
    private ArrayList<Order> orderList;
    Context context;
    Dialog qrDialog;
    TextView orderOTP;
    ImageView cross;
    ImageView qrCode;
    TextView itemName,itemQuantity,itemTotal,outletName;
    TextView itemNameRating,itemQuantityRating,itemTotalRating,outletNameRating;
    Dialog ratings;
    RatingBar ratingBar;
    TextView skip,skip2,submit;
    int i;
    String outletName1="";


    public OrderItemAdapter(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.order_rv_view, parent, false);
        // Return a new holder instance
        OrderItemAdapter.ViewHolder viewHolder = new OrderItemAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(holder.getAdapterPosition());

        //TextView outle = holder.outletNameOrderID;
        // TextView itemNameQuantity = holder.itemNameQuantity;
        // TextView timerText = holder.timerText;
        //TextView statusText = holder.statusText;
        //MaterialCardView orderCard = holder.orderCard;
        //render basic data

        qrDialog = new Dialog(context);
        qrDialog.setContentView(R.layout.qr_dialog);
        qrDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        qrDialog.setCancelable(false);
        qrDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cross = qrDialog.findViewById(R.id.cross);
        orderOTP = qrDialog.findViewById(R.id.orderOTP);
        qrCode = qrDialog.findViewById(R.id.qrCode);
        itemName = qrDialog.findViewById(R.id.item_name);
        itemQuantity = qrDialog.findViewById(R.id.item_quantity);
        itemTotal = qrDialog.findViewById(R.id.item_price);
        outletName = qrDialog.findViewById(R.id.restaurant_name);

        ratings = new Dialog(context);
        ratings.setContentView(R.layout.rating_dialogbox);
        ratings.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ratings.setCancelable(false);
        ratings.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ratingBar = ratings.findViewById(R.id.ratings);
        itemNameRating = ratings.findViewById(R.id.item_name);
        itemQuantityRating = ratings.findViewById(R.id.item_quantity);
        itemTotalRating = ratings.findViewById(R.id.item_price);
        outletNameRating = ratings.findViewById(R.id.restaurant_name);
        skip = ratings.findViewById(R.id.skip);
        skip2 = ratings.findViewById(R.id.skip2);
        submit = ratings.findViewById(R.id.submit);







        String orderId = order.getOrderID();
        //String outletDetails = findOutletName(order.getOutletID())+ " #"+orderId.substring(orderId.length()-5);
        String name = order.getItemName();
        String itemDetails = name + " x" + order.getQuantity();


        final String[] description = {""};
        Timer timer = new Timer();
        holder.itemName.setText(itemDetails);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrDialog.dismiss();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratings.dismiss();
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                skip.setVisibility(View.GONE);
                skip2.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratings.dismiss();
            }
        });

        skip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratings.dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Homescreen) context).upload(order.getOrderID(), ratingBar.getRating(),holder.getAdapterPosition());
                //notifyDataSetChanged();
                ratings.dismiss();

            }
        });



        //if(order.getItemImage().startsWith("http"))
        // Glide.with(context).load(order.getItemImage()).into(holder.itemImage);


        switch (order.getStatusCode()) {

            case -1:
                holder.itemName.setText(itemDetails);
                description[0] = "order denied, refunding";
                holder.goToOrders.setImageResource(R.drawable.white_cross);
                holder.itemStatus.setText(description[0]);
                holder.goToOrders.setImageResource(R.drawable.white_cross);
                break;

            case 0:
                holder.itemName.setText(itemDetails);
                description[0] = "order denied, refunding";
                holder.goToOrders.setImageResource(R.drawable.white_cross);
                holder.itemStatus.setText(description[0]);
                holder.goToOrders.setImageResource(R.drawable.white_cross);
            break;

            case 1:
                holder.goToOrders.setImageResource(R.drawable.right_arrow);
                description[0] = "awaiting confirmation";
                holder.itemStatus.setText(description[0]);
                //timerText.setText("-:-");
                //statusText.setText("approval pending");
                break;
            case 2:
                holder.goToOrders.setImageResource(R.drawable.right_arrow);

                description[0] = "collect in " + "15 mins";
                holder.itemStatus.setText(description[0]);
                final Runnable setTextViewUpdateRunnable = new Runnable() {
                    public void run() {
                        if (!holder.itemStatus.getText().equals("prepared, tap for QR")) {
                            holder.itemStatus.setText(description[0]);
                        }
                    }
                }

                    ;
                    TimerTask task = new TimerTask() {
                        public void run() {
                            Timestamp zingTime = order.getZingTime();
                            Date date = new Date();
                            Timestamp nowTime = new Timestamp(date);
                            long seconds = zingTime.getSeconds() - nowTime.getSeconds();
                            description[0] = "collect in " + (int) seconds / 60 + " mins";
                            if((int)seconds/60==1)
                            {
                                description[0] = "collect in 1 min";
                            }
                            if((int)seconds/60==0)
                            {
                                description[0] = "collect in <1 min";
                            }
                            if (seconds <= 0 || order.getStatusCode() == 3) {
                                Log.d("status code", String.valueOf(order.getStatusCode()));
                                description[0] = "food is still preparing";
                                cancel();
                            }
                            ((Homescreen) context).runOnUiThread(setTextViewUpdateRunnable);
                        }
                    };
                timer.schedule(task,0,60000);
                break;
            case 3:
                holder.goToOrders.setImageResource(R.drawable.right_arrow);
                holder.itemStatus.setText("prepared, tap for QR");
                timer.cancel();
                break;
            case 4:holder.goToOrders.setImageResource(R.drawable.white_cross);
                   holder.itemStatus.setText("rate your order");
                   break;

                }
                holder.tapForQR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (order.getStatusCode() == 3) {
                            /*Intent intent = new Intent(context, QRScreen.class);
                            intent.putExtra("text", order.getOrderID());
                            context.startActivity(intent);*/
                            findOutletName(order.getOutletID());
                            String OutletName = outletName1;
                            outletName.setText(OutletName);
                            itemName.setText(order.getItemName());
                            itemQuantity.setText("X " + order.getQuantity());
                            itemTotal.setText("₹ " + order.getTotalAmount()+".00");
                            orderOTP.setText("Order #"+orderId.substring(orderId.length()-4,orderId.length()));
                            qrCode.setImageBitmap(createBitmap(orderId));
                            qrDialog.show();

                        }
                        else if(order.getStatusCode()==4)
                        {
                            findOutletName(order.getOutletID());
                            String OutletName = outletName1;
                            outletNameRating.setText(OutletName);
                            itemNameRating.setText(order.getItemName());
                            itemQuantityRating.setText("X " + order.getQuantity());
                            itemTotalRating.setText("₹ " + order.getTotalAmount()+".00");
                            holder.goToOrders.setImageResource(R.drawable.white_cross);
                            ratings.show();
                        }

                    }
                });

        holder.goToOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(order.getStatusCode()==4){
                    ((Homescreen) context).uploadWithoutRating(order.getOrderID());
                ratings.dismiss();}
                if(order.getStatusCode()==-1 || order.getStatusCode()==0)
                {
                    Log.e("Order Refund","Working");
                    ((Homescreen) context).RefundAndDenied(order.getOrderID());

                }
                if (order.getStatusCode() == 3) {
                            /*Intent intent = new Intent(context, QRScreen.class);
                            intent.putExtra("text", order.getOrderID());
                            context.startActivity(intent);*/
                    findOutletName(order.getOutletID());
                    String OutletName = outletName1;
                    outletName.setText(OutletName);
                    itemName.setText(order.getItemName());
                    itemQuantity.setText("X " + order.getQuantity());
                    itemTotal.setText("₹ " + order.getTotalAmount()+".00");
                    orderOTP.setText("Order #"+orderId.substring(orderId.length()-4,orderId.length()));
                    qrCode.setImageBitmap(createBitmap(orderId));
                    qrDialog.show();

                }
            }
        });
                /*holder.goToOrders.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (order.getStatusCode() == 3) {
                            *//*Intent intent = new Intent(context, QRScreen.class);
                            intent.putExtra("text", order.getOrderID());
                            context.startActivity(intent);*//*
                        } else if(order.getStatusCode() ==0) {
                            ((Homescreen) context).orderDismiss(holder.getAdapterPosition());
                            holder.goToOrders.setImageResource(R.drawable.right_arrow);
                            //do nothing
                        }
                    }
                });*/
        }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public TextView itemStatus;
        public ImageView goToOrders;
        public RelativeLayout tapForQR;


        //MaterialCardView orderCard;
        //ImageView itemImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.item_name);
            itemStatus = itemView.findViewById(R.id.item_status);
            goToOrders = itemView.findViewById(R.id.goToOrder);
            tapForQR = itemView.findViewById(R.id.tapForQR);
            //goToOrders = itemView.findViewById(R.id.goToOrder);


            //outletNameOrderID = itemView.findViewById(R.id.outlet_name_id);
            //itemNameQuantity = itemView.findViewById(R.id.item_name_quantity);
            //timerText = itemView.findViewById(R.id.timer);
            //statusText = itemView.findViewById(R.id.status_text);
            //orderCard = itemView.findViewById(R.id.order_card);
            //itemView = itemView.findViewById(R.id.itemImage);
            context = itemView.getContext();
        }
    }
    public Bitmap createBitmap(String text){
        BitMatrix result;
        try{
            result = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 500,500, null);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        int height = result.getHeight();
        int width = result.getWidth();
        int[] pixels = new int[width*height];
        for(int x=0;x<height;x++) {
            int offset = x * width;
            for (int k = 0; k < width; k++) {
                pixels[offset + k] = result.get(k, x) ? BLACK : WHITE;

            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0 , width, 0, 0, width, height);
        return bitmap;
    }
    public void findOutletName(String outletId) {
        i = -1;
        for (i = 0; i < Dataholder.outletList.size(); i++) {
            if (Dataholder.outletList.get(i).getId().equals(outletId))
                break;
        }

        if (i == Dataholder.outletList.size()) {
            ((Homescreen) context).findOutletName(outletId);


            Thread thread = new Thread() {
                public void run() {
                    try {
                        sleep(3000);
                    } catch (Exception e) {

                    } finally {
                        for (i = 0; i < Dataholder.outletList.size(); i++) {
                            Log.e("i1",Dataholder.outletList.get(i).getName());
                            Log.e("i_s",i+"");
                            if (Dataholder.outletList.get(i).getId().equals(outletId))
                                break;

                        }
                        Log.e("I",i+"");

                       OutletName(Dataholder.outletList.get(i).getName());

                        }

                }


            };
            thread.start();
            Log.e("I1",Dataholder.outletList.get(0).getName()+"");

        }
    }









    public void OutletName(String outletName)
    {
        outletName1 = outletName;
    }
}
