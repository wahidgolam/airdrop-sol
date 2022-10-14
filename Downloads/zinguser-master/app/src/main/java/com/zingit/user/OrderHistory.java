package com.zingit.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderHistory extends AppCompatActivity {
    TextView totalOrders;
    FirebaseFirestore db;
    ArrayList<Order> orderArrayList;
    ArrayList<Order> prevOrderArrayList;
    //OrderItemAdapter1 orderItemAdapter1;
    static OrderHistoryAdapter orderHistoryAdapter;
    RecyclerView orderRV;
    public static Context mContext;

    ImageView backBtn;
    int status;
    ImageView historyImage,cartImage,homeImage;
    LinearLayout cart,home,history;
    LoadingDialog loadingDialog;
    RelativeLayout header2,header3;
    LinearLayout empty_orderhistory_view;
    TextView completedOrderText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        //totalOrders = findViewById(R.id.totalOrders);
        backBtn = findViewById(R.id.back_btn);
        db = FirebaseFirestore.getInstance();
        orderRV = findViewById(R.id.recyclerView);
        mContext = this;
        historyImage = findViewById(R.id.history_image);
        cartImage = findViewById(R.id.cart_image);
        homeImage = findViewById(R.id.home_image);
        cart = findViewById(R.id.cart_layout);
        home = findViewById(R.id.home_layout);
        history = findViewById(R.id.history_layout);
        header2 = findViewById(R.id.header2);
        header3 = findViewById(R.id.header3);
        empty_orderhistory_view = findViewById(R.id.empty_orderhistory_view);
        completedOrderText = findViewById(R.id.completeOrderText);
       // prevOrderArrayList = new ArrayList<Order>();

       // orderItemAdapter = new OrderItemAdapter(Dataholder.orderList);
        //orderRV.setLayoutManager(new LinearLayoutManager(this));
        //orderRV.setAdapter((orderItemAdapter));

        //EventListener(); //Fetches no of total orders by an user. removed now.

        //On Backbtn (Arrow) is pressed
       /* backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Homescreen.class);
                startActivity(intent);
            }
        });*/// Not working because UI is getting overlapped.


        historyImage.setImageResource(R.drawable.history_white);
        history.setBackgroundResource(R.drawable.rad8_roundedlayout);
        home.setBackgroundResource(0);
        cart.setBackgroundResource(0);
        homeImage.setImageResource(R.drawable.zing);
        cartImage.setImageResource(R.drawable.cart_black);
        Dataholder.path=1;

        setupUi();

        homeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeImage.setImageResource(R.drawable.zing_white);
                home.setBackgroundResource(R.drawable.rad8_roundedlayout);
                history.setBackgroundResource(0);
                cart.setBackgroundResource(0);
                cartImage.setImageResource(R.drawable.cart_black);
                historyImage.setImageResource(R.drawable.history_black);
                Dataholder.path=2;
                Intent intent = new Intent(getApplicationContext(), Homescreen.class);
                startActivity(intent);
            }
        });

        cartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartImage.setImageResource(R.drawable.cart_white);
                cart.setBackgroundResource(R.drawable.rad8_roundedlayout);
                history.setBackgroundResource(0);
                home.setBackgroundResource(0);
                homeImage.setImageResource(R.drawable.zing);
                historyImage.setImageResource(R.drawable.history_black);
                Dataholder.path=3;
                Intent intent = new Intent(getApplicationContext(),CartScreen.class);
                startActivity(intent);
            }});

        EventListener1();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Homescreen.class);
                startActivity(intent);
            }
        });







    }

    private void setupUi() {
        header2.setVisibility(View.GONE);
        header3.setVisibility(View.GONE);
        loadingDialog = new LoadingDialog(OrderHistory.this, "Fetching past orders");
        loadingDialog.startLoadingDialog();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                disappearDialog();
            }
        }, 2000);
    }

    public void EventListener1()
    {
        if(Dataholder.orderHistoryList.size()==0)
        fetchOrders();

        //Log.e("OrderHistory",Dataholder.orderList.get(0).getItemName());
        orderHistoryAdapter = new OrderHistoryAdapter(getApplicationContext(),Dataholder.orderHistoryList);
        orderRV.setAdapter((orderHistoryAdapter));
        orderRV.setLayoutManager(new LinearLayoutManager(this));
        //orderHistoryAdapter.notifyDataSetChanged();

    }
    public Timestamp startOfDay() {
        Date date = new Date();
        Timestamp nowTime = new Timestamp(date);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(nowTime.getSeconds()*1000);
        cal.set(Calendar.HOUR_OF_DAY, 0); //set hours to zero
        cal.set(Calendar.MINUTE, 0); // set minutes to zero
        cal.set(Calendar.SECOND, 0); //set seconds to zero
        Date date2 = cal.getTime();
        nowTime = new Timestamp(date2);
        return (nowTime);
    }
    public Timestamp tenDaysEarlier() {
        LocalDateTime localDateTime = LocalDateTime.of(
                LocalDate.now().minusDays(10),
                LocalTime.of(12, 0)
        );
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(instant);
        Log.d("Time", date.toString());
        Timestamp nowTime = new Timestamp(date);
        return (nowTime);
    }
    public Timestamp endOfDay() {
        Date date = new Date();
        Timestamp nowTime = new Timestamp(date);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(nowTime.getSeconds()*1000);
        cal.set(Calendar.HOUR_OF_DAY, 23); //set hours to zero
        cal.set(Calendar.MINUTE, 59); // set minutes to zero
        cal.set(Calendar.SECOND, 59); //set seconds to zero
        Date date2 = cal.getTime();
        nowTime = new Timestamp(date2);
        return (nowTime);
    }

    public void fetchOrders(){
        tenDaysEarlier();
        Log.d("Fetching Order History", "In process");
        //Query query = db.collection("order").whereEqualTo("studentID", Dataholder.studentUser.getUserID()).whereGreaterThan("statusCode", -3).whereLessThan("statusCode",6).whereGreaterThan("placedTime", tenDaysEarlier()).whereLessThan("placedTime", endOfDay()).orderBy("placedTime", Query.Direction.DESCENDING);
        Query query = db.collection("order").whereEqualTo("studentID", Dataholder.studentUser.getUserID()).whereGreaterThan("placedTime", tenDaysEarlier()).orderBy("placedTime", Query.Direction.DESCENDING);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("FETCH ERROR", "listen:error" + error.getLocalizedMessage());
                    return;
                }
                if(!snapshots.isEmpty()) {

                    Log.d("SNAPSHOT", String.valueOf(snapshots.isEmpty()));
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d("Added1", "New order1: " + dc.getDocument().getData());
                                Order order = dc.getDocument().toObject(Order.class);
                                Dataholder.orderHistoryList.add(order);
                                //sort(Dataholder.orderHistoryList);
                                break;
                            case MODIFIED:
                                Log.d("REM", "is it real?");
                                Log.d("Modified", "Modified order: " + dc.getDocument().getData());
                                Order updated_order = dc.getDocument().toObject(Order.class);
                                int statusCode = updated_order.getStatusCode();
                                //statusCode 2,3 -> Show alert
                                Log.d("Modified", String.valueOf(statusCode));
                                updateOrder(updated_order);//updates UI
                                break;
                            case REMOVED:
                                Log.d("Removed", "Removed order: " + dc.getDocument().getData());
                                Order removed_order = dc.getDocument().toObject(Order.class);
                                removeOrder(removed_order);
                                break;
                            //Notification about rating?
                        }
                    }
                    orderHistoryAdapter.notifyDataSetChanged();
                }
                else{
                    //empty state
                    emptyState();
                }
            }
        });
    }
    public void updateOrder(Order order){
        for(int i=0; i<Dataholder.orderHistoryList.size(); i++){
            if(Dataholder.orderHistoryList.get(i).getOrderID().equals(order.getOrderID())){
                Dataholder.orderHistoryList.set(i, order);
            }
        }
    }
    public void removeOrder(Order order){
        for(int i=0; i<Dataholder.orderHistoryList.size(); i++){
            if(Dataholder.orderHistoryList.get(i).getOrderID().equals(order.getOrderID())){
                Dataholder.orderHistoryList.remove(i);
            }
        }

    }





        public static void sort(ArrayList<Order> list) {

            list.sort(Comparator.comparing(Order::getPlacedTime));
            Collections.reverse(list);
        }
        public void upload(String orderID,double ratings, int position){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String,Object> map = new HashMap<String,Object>();
            //Log.e("OrderIsHere",orderID);
            map.put("orderID",orderID);
            map.put("rating",ratings);
            db.collection("rating").document().set(map);

            DocumentReference documentReference2 = db.collection("order").document(orderID);
            documentReference2.update("rating", ratings,"statusCode",5).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.e("Ratings updated", "Ratings Updated");
                    Toast.makeText(mContext, "Your rating has been recorded", Toast.LENGTH_SHORT).show();
                    Dataholder.orderHistoryList.get(position).setStatusCode(5);
                    orderHistoryAdapter.notifyDataSetChanged();
                    //Intent orderHistory = new Intent(mContext, OrderHistory.class);
                    //mContext.startActivity(orderHistory);
                }
            });

        }
        public void updateLocalOrderList(){

        }
        public void disappearDialog(){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismissDialog();
                header2.setVisibility(View.VISIBLE);
                header3.setVisibility(View.VISIBLE);
            }
        }, 500);
    }

    public void emptyState()
    {
        header2.setVisibility(View.GONE);
        header3.setVisibility(View.GONE);
        empty_orderhistory_view.setVisibility(View.VISIBLE);
        completedOrderText.setVisibility(View.GONE);

    }
    }








