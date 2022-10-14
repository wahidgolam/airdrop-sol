package com.zingit.user;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>{
    private ArrayList<Order> orderList;
    Context context;
    FirebaseFirestore db;
    String restaurant_name;
    Dialog dialog;
    ImageView qrCode;
    ImageView cross;
    TextView orderOTP;
    Dialog ratings;
    RatingBar ratingBar;
    TextView skip;
    TextView skip2,submit;
    String OrderIdFetched;
    String description[];
    Timer timer = new Timer();

    TextView outletName,itemName,itemQuantity,itemTotal;
    Outlet outlet;
    String months[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    public OrderHistoryAdapter(Context context,ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = LayoutInflater.from(context).inflate(R.layout.prev_orders,parent,false);
        // Return a new holder instance
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        String outletId = order.getOutletID();
        db = FirebaseFirestore.getInstance();
        description = new String[1];




        db.collection("outlet").document(outletId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 outlet = task.getResult().toObject(Outlet.class);
                restaurant_name = outlet.getName();
                Log.e("restaurantName",restaurant_name);
                holder.restaurantName.setText(restaurant_name);
                outletName.setText(restaurant_name);

            }
        });
        String orderId = order.getOrderID();

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.qr_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        cross = dialog.findViewById(R.id.cross);
        orderOTP = dialog.findViewById(R.id.orderOTP);

        ratings = new Dialog(context);
        ratings.setContentView(R.layout.rating_dialogbox);
        ratings.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ratings.setCancelable(false);
        ratings.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ratingBar = ratings.findViewById(R.id.ratings);
        skip = ratings.findViewById(R.id.skip);
        skip2 = ratings.findViewById(R.id.skip2);
        submit = ratings.findViewById(R.id.submit);



        outletName = dialog.findViewById(R.id.restaurant_name);
        itemName = dialog.findViewById(R.id.item_name);
        itemQuantity = dialog.findViewById(R.id.item_quantity);
        itemTotal = dialog.findViewById(R.id.item_price);


        qrCode = dialog.findViewById(R.id.qrCode);

        Log.e("orderId",orderId);
        description[0] = "food is preparing";


        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //ratings skip btn
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
                ((OrderHistory) context).upload(OrderIdFetched, ratingBar.getRating(),holder.getAdapterPosition());
                holder.help.setText("Help");
                ratings.dismiss();
                //Toast.makeText(context, "Your rating has been recorded!", Toast.LENGTH_SHORT).show();
            }
        });








        holder.itemName.setText(order.getItemName());
        holder.totalPrice.setText("₹ "+String.valueOf(order.getTotalAmount()) + ".00");
        holder.totalQuantity.setText(order.getQuantity()+"");
        Timestamp timestamp = order.getPlacedTime();



        holder.orderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(order.getStatusCode()==3)
                {
                    String OutletName = findOutletName(order.getOutletID());
                    outletName.setText(OutletName);
                    itemName.setText(order.getItemName());
                    itemQuantity.setText("X " + order.getQuantity());
                    itemTotal.setText("₹ " + order.getTotalAmount()+".00");
                    orderOTP.setText("Order #"+orderId.substring(orderId.length()-4,orderId.length()));
                    qrCode.setImageBitmap(createBitmap(orderId));
                    dialog.show();
                }
            }


        });

        holder.help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch(order.getStatusCode())
                {
                    case 0: openWhatsapp(order);
                        break;

                    case 1:openWhatsapp(order);break;
                    case 2:openWhatsapp(order);break;
                    case 3: openWhatsapp(order);break;
                    case 4:

                        ratings.show();
                        TextView outletName = ratings.findViewById(R.id.restaurant_name);
                        TextView itemName = ratings.findViewById(R.id.item_name);
                        TextView itemQuantity = ratings.findViewById(R.id.item_quantity);
                        TextView itemTotal = ratings.findViewById(R.id.item_price);
                        itemName.setText(order.getItemName());
                        itemQuantity.setText("X " + order.getQuantity());
                        itemTotal.setText("₹ " + order.getTotalAmount()+".00");
                        outletName.setText(restaurant_name);
                        OrderIdFetched = order.getOrderID();








                        ;
                        break;

                    case 5:
                        //holder.orderStatusText.setText("Completed");
                        //holder.orderStatus.setBackgroundResource(R.drawable.button_filled_green);
                        holder.help.setText("Help");
                        notifyDataSetChanged();
                        openWhatsapp(order);
                        break;




                    default:
                }

            }
        });

        /*if(order.getItemImage().startsWith("http"))
        Glide.with(context).load(order.getItemImage()).into(holder.itemImage);
*/
        Date date = timestamp.toDate();

        String timefinal = (String.valueOf(date));
        //Log.e("Timefinal",timefinal.substring(4,7));
        String mon = timefinal.substring(4,7);
        int i;
        for(i=0;i<months.length;i++)
        {
            if(mon.equals(months[i]))
            {
                break;
            }
        }
        i++;
       holder.orderDate.setText(timefinal.substring(8,10)+"-0"+i+"-"+timefinal.substring(32,34));

       switch(order.getStatusCode())
       {
           case -2:
               holder.orderStatusText.setText("Refund Initiated");
               holder.orderStatusText.setTextColor(Color.parseColor("#ffffff"));
               break;

           case -1:
               holder.orderStatusText.setText("Refund Initiated");
               holder.orderStatusText.setTextColor(Color.parseColor("#ffffff"));
               break;
           case 0: holder.orderStatusText.setText("Order Denied");
               holder.orderStatusText.setTextColor(Color.parseColor("#ffffff"));


               break;
           case 1: holder.orderStatusText.setText("Awaiting");
           holder.orderStatusText.setTextColor(Color.parseColor("#000000"));
           holder.orderStatus.setBackgroundResource(R.drawable.button_filled_yellow);
           break;
           case 2: holder.orderStatusText.setText("Order Accepted");
           holder.orderStatus.setBackgroundResource(R.drawable.button_filled_yellow);
           holder.orderStatusText.setTextColor(Color.parseColor("#2b2b2b"));

               //timer
               Timestamp zingTime = order.getZingTime();
               Date now = new Date();
               Timestamp nowTime = new Timestamp(now);
               long seconds1 = zingTime.getSeconds() - nowTime.getSeconds();
               description[0] = "collect in " + (int) seconds1 / 60 + " mins";
               if (seconds1 <= 0) {
                   description[0] = "timer expired";
               }
               //holder.itemStatus.setText(description[0]);
               holder.orderStatusText.setText(description[0]);
               final Runnable setTextViewUpdateRunnable = new Runnable() {
                   public void run() {
                       if (!holder.orderStatusText.equals("Show QR")) {
                           holder.orderStatusText.setText(description[0]);
                       }
                       else
                       {
                           holder.orderStatusText.setText("still awaiting");
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

                       if((int)seconds1/60==1)
                       {
                           description[0] = "collect in " + (int) seconds / 60 + " min";

                       }
                       if((int)seconds1/60==0)
                       {
                           description[0] = "collect in <1 min";
                       }
                       if (seconds <= 0 || order.getStatusCode() == 3) {
                           Log.d("expired", String.valueOf(order.getStatusCode()));
                           description[0] = "timer expired";
                           cancel();
                       }
                       ((OrderHistory) context).runOnUiThread(setTextViewUpdateRunnable);
                   }
               };
               timer.schedule(task,0,5000);

               break;
           case 3: holder.orderStatusText.setText("Show QR");
               holder.orderStatusText.setTextColor(Color.parseColor("#ffffff"));

               break;
           case 4:
               holder.orderStatusText.setText("Completed");
               holder.orderStatusText.setTextColor(Color.parseColor("#ffffff"));

               holder.help.setText("Rate");
               holder.orderStatus.setBackgroundResource(R.drawable.button_filled_green);
               break;

           case 5:
               holder.orderStatusText.setText("Completed");
               holder.orderStatusText.setTextColor(Color.parseColor("#ffffff"));

               holder.help.setText("Help");

                   holder.orderStatus.setBackgroundResource(R.drawable.button_filled_green);
                   break;


           default:
       }

        holder.viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e("longitude",outlet.getLongitude());
                if(outlet.getLongitude()==null)
                {
                    Toast.makeText(context, "Map data isn't currently available :(", Toast.LENGTH_SHORT).show();
                }
                else {
                    String lat = findLatitude(outletId);
                    String longitude = findLongitude(outletId);

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + outlet.getLatitude() + "," + outlet.getLongitude() + "&mode=w"));
                    intent.setPackage("com.google.android.apps.maps");
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    } else {
                        context.startActivity(intent);
                    }
                }
            }
        });
















    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView restaurantName;
        public TextView itemName,orderDate,totalQuantity,totalPrice,orderStatusText;
        public Button help;
        public LinearLayout orderStatus;
        TextView viewMap;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            orderDate = itemView.findViewById(R.id.orderDate);
            itemName = itemView.findViewById(R.id.itemName);
            totalQuantity = itemView.findViewById(R.id.totalQuantity);
            totalPrice = itemView.findViewById(R.id.item_price);
            help = itemView.findViewById(R.id.help);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderStatusText = itemView.findViewById(R.id.orderStatusText);
            viewMap = itemView.findViewById(R.id.viewMap);
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

    public void openWhatsapp(Order order){


        String text = "Payment Order ID: "+ order.getPaymentOrderID()+", Status: "+order.getPaymentStatus() + "\n" + "Hi, I am "+Dataholder.studentUser.getFirstName()+" and I wanted to talk about...";
        String url = "https://api.whatsapp.com/send?phone=" + Dataholder.contactNumber+ "&text=" + text;
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.putExtra(Intent.EXTRA_TEXT, text);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "Whatsapp is not installed in this phone", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    public String findOutletName(String outletId)
    {
        int i;
        for(i=0;i<Dataholder.outletList.size();i++)
        {
            if(Dataholder.outletList.get(i).getId().equals(outletId))
                break;
        }

        return Dataholder.outletList.get(i).getName();
    }
    public String findLatitude(String outletId)
    {
        int i;
        for(i=0;i<Dataholder.outletList.size();i++)
        {
            if(Dataholder.outletList.get(i).getId().equals(outletId))
                break;
        }
        return Dataholder.outletList.get(i).getLatitude();

    }
    public String findLongitude(String outletId)
    {
        int i;
        for(i=0;i<Dataholder.outletList.size();i++)
        {
            if(Dataholder.outletList.get(i).getId().equals(outletId))
                break;
        }
        return Dataholder.outletList.get(i).getLongitude();

    }



}
