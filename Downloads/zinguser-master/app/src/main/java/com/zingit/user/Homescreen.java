package com.zingit.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.api.ResourceDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zingit.user.model.CampusChooser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.DoubleAdder;

import club.cred.neopop.PopFrameLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public class Homescreen extends AppCompatActivity{
    public int backcount = 0;
    FirebaseAuth mAuth;
    String userID;
    String campusID;
    String userNameText;
    String campusAddress1;
    String campusAddress2;
    ArrayList<Outlet> outletList;
    ArrayList<Outlet> filteredList;
    FirebaseFirestore db;
    RecyclerView outletRV;
    RecyclerView orderRV;
    OutletAdapter outletAdapter;
    static OrderItemAdapter orderItemAdapter;
    TextView userName;
    TextView campusName;
    TextView campusSubName;
    TextView showOrderHistory;
    LinearLayout orderView;
    View dividorindo;
    LoadingDialog loadingDialog;
    LinearLayout profileLayout;
    TextView order_details;
    LinearLayout cart,home,history;
    ImageView cartImage,homeImage,historyImage;
    LinearLayout campusSelection;
    CircleImageView profileImage;
    LinearLayout bottomMargin;
    EditText search_outlet;
    Dialog dialog;
    ImageView cross;
    TextView zingText;
    Dialog qrDialog;
    TextView orderOTP;
    ImageView crossQR;
    Dialog ratings;
    RatingBar ratingBar;
   static Context mContext;
   static Outlet outlet;
   PopFrameLayout i_understand;
    AppUpdateManager appUpdateManager;
    private  final int MY_REQUEST_CODE = 144;

    int status;

    //current order management system
    //name and profile clickable
    //campus

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        //initialising firebase variables
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mContext = getApplicationContext();

        //creating notification channel
        createNotificationChannel();
        //log FCM token
        logFCMToken();
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        //util variables
        status = 0;
        outletList = new ArrayList<>();
        outletRV = findViewById(R.id.recyclerView);
        orderRV = findViewById(R.id.orderRV);
        zingText = findViewById(R.id.zingText);

        campusName = findViewById(R.id.collegeName);
        bottomMargin = findViewById(R.id.bottomMargin);




        cart = findViewById(R.id.cart_layout);
        home = findViewById(R.id.home_layout);
        history = findViewById(R.id.history_layout);
        cartImage = findViewById(R.id.cart_image);
        homeImage = findViewById(R.id.home_image);
        historyImage = findViewById(R.id.history_image);
        userName = findViewById(R.id.userName);
        search_outlet = findViewById(R.id.search_outlet);



        StudentUser studentUser = Dataholder.studentUser;




        dialog = new Dialog(Homescreen.this);
        dialog.setContentView(R.layout.instructions_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        i_understand = dialog.findViewById(R.id.save_btn);






        if(Dataholder.newUser && Dataholder.firstTimeLogin)
        {
            dialog.show();
            Dataholder.firstTimeLogin = false;
        }

        i_understand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });




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
            }
        });

        historyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyImage.setImageResource(R.drawable.history_white);
                history.setBackgroundResource(R.drawable.rad8_roundedlayout);
                home.setBackgroundResource(0);
                cart.setBackgroundResource(0);
                homeImage.setImageResource(R.drawable.zing);
                cartImage.setImageResource(R.drawable.cart_black);
                Dataholder.path=1;
                Intent intent = new Intent(getApplicationContext(), OrderHistory.class);
                startActivity(intent);
            }});

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


        search_outlet.addTextChangedListener(new TextWatcher() {
                                                 @Override
                                                 public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                 }

                                                 @Override
                                                 public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                 }

                                                 @Override
                                                 public void afterTextChanged(Editable editable) {
                                                     filter(editable.toString());

                                                 }

                                              private void filter(String toString) {
                                                     ArrayList<Outlet> filteredOutlets = new ArrayList<>();
                                                     for(Outlet outlet : outletList)
                                                     {
                                                         if(outlet.getName().toLowerCase().contains(toString.toLowerCase()))
                                                         {
                                                             filteredOutlets.add(outlet);

                                                         }
                                                     }
                                                     outletAdapter.filterList(filteredOutlets);

                                               }
                                                });






       /* campusSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChooseCampus.class);
                startActivity(intent);
            }
        });*/




        //loading dialog
        loadingDialog = new LoadingDialog(Homescreen.this, "Fetching best restaurants for you");
        loadingDialog.startLoadingDialog();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                status = status^1;
                disappearDialog();
            }
        }, 3000);


        //retrieving user and campus id
        userID = Dataholder.studentUser.getUserID();
        campusID = Dataholder.studentUser.getCampusID();

        userNameText = Dataholder.studentUser.getFirstName();
        //userID = mAuth.getCurrentUser().getUid();
        //SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        //campusID= sh.getString("campusID", "Ow8TzDsGnMHqGFUP1KyW");
        Dataholder.orderList = new ArrayList<>();

        zingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Profile.class);
                startActivity(intent);
            }
        });

        setupUI();
        fetchCampus();

        campusName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChooseCampus.class);
                startActivity(intent);
            }
        });

        //setting up outlet recycler view
        outletAdapter = new OutletAdapter(outletList);
        outletRV.setAdapter(outletAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        outletRV.setLayoutManager(new LinearLayoutManager(this));

        //setting up order recycler view
        orderItemAdapter = new OrderItemAdapter(Dataholder.orderList);

        orderRV.setAdapter((orderItemAdapter));
        orderRV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(orderRV);
        //updating list of outlets
        fetchOutlets();
        //fetchOrders();
    }
    public void logFCMToken(){
        Log.d("Bhaiya","here");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM Error", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        pushFCMToServer(token);
                        // Log and toast
                        Log.d("FCM Token", token);
                    }
                });
        //Toast.makeText(this, Dataholder.studentUser.getFCMToken(), Toast.LENGTH_SHORT).show();
    }
    public void pushFCMToServer(String token){
        db.collection("studentUser").document(Dataholder.studentUser.getUserID()).update(
                "FCMToken", token
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("FCM token", "Logging and Update Complete");
            }
        });

    }
    public void fetchCampus(){
        String campusId = Dataholder.studentUser.getCampusID();
        db.collection("campus").document(campusId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Campus campus = task.getResult().toObject(Campus.class);
                    if(campus!=null) {
                        //campusName.setText(campus.getName());
                        //campusSubName.setText(campus.getSubName());
                        campusName.setText(campus.getName());
                        Dataholder.contactNumber = campus.getContactNumber();
                    }
                    else{
                        Toast.makeText(Homescreen.this, "Error getting location details", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public void setupUI(){
        userName.setText("Hello " +  userNameText+"!");
        bottomMargin.setVisibility(View.GONE);

        //collect the percentage of taxes
        db.collection("configuration").document("XSOzJ7xPfZZsAFm3NNdc").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete())
                {
                    Configuration configuration = task.getResult().toObject(Configuration.class);
                    Dataholder.taxPercentage = configuration.getTax();


                }
            }
        });

        //handle campus
        //handle student image
    }

    public void fetchOutlets(){
        Dataholder.outletList = new ArrayList<Outlet>();
        db.collection("outlet")
                .whereEqualTo("campusID", campusID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Outlet outlet = document.toObject(Outlet.class);
                                Log.e("Outlet Names",outlet.getName());
                                outletList.add(outlet);
                            }


                            //handle outletListEmpty
                            Dataholder.outletList = outletList;
                            if(outletList.isEmpty())
                            Toast.makeText(Homescreen.this, "Error fetching outlets. Please report bug from the profile section", Toast.LENGTH_LONG).show();
                            else {
                                outletList.sort(Comparator.comparing(Outlet::getOpenStatus));
                                Collections.reverse(outletList);
                            }
                            outletAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("Outlet Data", "Error getting documents: ", task.getException());
                        }
                        fetchOrders();
                    }
                });
    }
   /* public void sendNotification(String title, String subtitle, int type, String text){
        Intent intent = null;
        if(type==2){
            //prepared
            //intent = new Intent(this, QRScreen.class);
            //intent.putExtra("text", text);
        }
        else if(type==3){
            //collected
            //intent = new Intent(this, RatingScreen.class);
            //intent.putExtra("orderID", text);
           // intent.putExtra("type", 1);
        }
        else if(type==1){
            //accepted and denied
            intent = new Intent(this, Splashscreen.class);
            intent.putExtra("text", text);
        }
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "007")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(alarmSound)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(007, builder.build());
    }*/
    public void goToCart(View view){
        Intent intent = new Intent(getApplicationContext(), CartScreen.class);
        startActivity(intent);
    }
    public void goToProfile(View view){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
        //Intent intent = new Intent(getApplicationContext(), Profile.class);
        //startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                int index = -1;
                for(int i=0; i< Dataholder.outletList.size(); i++){
                    if(outletList.get((i)).getId().equals(intentResult.getContents())){
                        index=i;
                        break;
                    }
                }
                if(index!=-1) {
                    Dataholder.currentOutlet = Dataholder.outletList.get(index);
                    Intent intent = new Intent(getApplicationContext(), Menuscreen.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(this, "You are viewing Outlet from a different campus", Toast.LENGTH_SHORT).show();
                }
                //finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public static void sort(ArrayList<Order> list) {

        list.sort((o1, o2)
                -> o1.getPlacedTime().compareTo(
                o2.getPlacedTime()));
        Collections.reverse(list);
    }
    public void fetchOrders(){
        Query query = db.collection("order").whereEqualTo("studentID", userID).whereGreaterThan("statusCode", -1).whereLessThan("statusCode", 5);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("FETCH2ERROR", "listen:error" + error.getLocalizedMessage());
                    return;
                }
                if(!snapshots.isEmpty()) {
                    bottomMargin.setVisibility(View.VISIBLE);
                    Log.d("SNAPSHOT", String.valueOf(snapshots.isEmpty()));
                   // dividorindo.setVisibility(View.VISIBLE);
                   // orderView.setVisibility(View.VISIBLE);
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d("Added", "New order: " + dc.getDocument().getData());
                                Order order = dc.getDocument().toObject(Order.class);
                                Dataholder.orderList.add(order);
                                sort(Dataholder.orderList);
                                orderItemAdapter.notifyDataSetChanged();
                                //loadingDialog.dismissDialog();
                                status = status ^ 1;
                                disappearDialog();
                                break;
                            case MODIFIED:
                                Log.d("REM", "is it real?");
                                Log.d("Modified", "Modified order: " + dc.getDocument().getData());
                                Order updated_order = dc.getDocument().toObject(Order.class);
                                int statusCode = updated_order.getStatusCode();
                                //statusCode 2,3 -> Show alert
                                Log.d("Modified", String.valueOf(statusCode));
                                updateOrder(updated_order);//updates UI
                                orderItemAdapter.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                Log.d("Removed", "Removed order: " + dc.getDocument().getData());
                                Order removed_order = dc.getDocument().toObject(Order.class);
                                removeOrder(removed_order);
                                orderItemAdapter.notifyDataSetChanged();
                                break;
                                //Notification about rating?
                        }
                    }
                }
                else{
                    hideOrdersView();
                }
            }
        });
    }
    public void hideOrdersView(){
        //hide shit
       // dividorindo.setVisibility(View.GONE);
       // orderView.setVisibility(View.GONE);
    }
    public void disappearDialog(){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismissDialog();
            }
        }, 500);
    }
    public void updateOrder(Order order){
        for(int i=0; i<Dataholder.orderList.size(); i++){
            if(Dataholder.orderList.get(i).getOrderID().equals(order.getOrderID())){
                Dataholder.orderList.set(i, order);
            }
        }
    }
    public void removeOrder(Order order){
        for(int i=0; i<Dataholder.orderList.size(); i++){
            if(Dataholder.orderList.get(i).getOrderID().equals(order.getOrderID())){
                Dataholder.orderList.remove(i);
            }
        }
    }
    private void createNotificationChannel() {

        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext()
                .getPackageName() + "/" + R.raw.notifsound);

        NotificationManager mNotificationManager = getSystemService(NotificationManager.class);

        List<NotificationChannel> channelList = mNotificationManager.getNotificationChannels();
        for(int i =0; i<channelList.size();i++){
            mNotificationManager.deleteNotificationChannel(channelList.get(i).getId());
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("012", "Zing User", importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            if(soundUri != null){
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                notificationChannel.setSound(soundUri,audioAttributes);
            }

            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            AppUpdateType.IMMEDIATE,
                                            this,
                                            MY_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        });*/

        homeImage.setImageResource(R.drawable.zing_white);
        home.setBackgroundResource(R.drawable.rad8_roundedlayout);
        history.setBackgroundResource(0);
        cart.setBackgroundResource(0);
        cartImage.setImageResource(R.drawable.cart_black);
        historyImage.setImageResource(R.drawable.history_black);
        //fetchOutlets();
    }

    void orderDismiss(int pos)
    {
        //local changes
        Dataholder.orderList.get(pos).setStatusCode(-1);
        String orderId = Dataholder.orderList.get(pos).getOrderID();
        //global changes
        db.collection("order").document(orderId).update("statusCode",-1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                orderItemAdapter.notifyDataSetChanged();
            }
        });
        //notify dataset changed
    }
    public static void upload(String orderID,double ratings, int position){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> map = new HashMap<String,Object>();
        Log.e("OrderIsHere",orderID);
        map.put("orderID",orderID);
        map.put("rating",ratings);
        db.collection("rating").document().set(map);


        DocumentReference documentReference2 = db.collection("order").document(orderID);
        documentReference2.update("rating", ratings,"statusCode",5).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e("Ratings updated", "Ratings Updated");
                Dataholder.orderList.get(position).setStatusCode(5);
                orderItemAdapter.notifyDataSetChanged();


                /*Intent login = new Intent(mContext, Homescreen.class);
                mContext.startActivity(login);*/
            }



        });
       // Intent login = new Intent(mContext, OrderHistory.class);
      //  mContext.startActivity(login);






    }
    public static void uploadWithoutRating(String orderID)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentReference2 = db.collection("order").document(orderID);
        documentReference2.update("statusCode",5).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e("Ratings updated", "Ratings Updated");
                orderItemAdapter.notifyDataSetChanged();


                /*Intent login = new Intent(mContext, Homescreen.class);
                mContext.startActivity(login);*/
            }
    });
}

    public static void RefundAndDenied(String orderID)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentReference2 = db.collection("order").document(orderID);
        documentReference2.update("statusCode",-2).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                orderItemAdapter.notifyDataSetChanged();


                /*Intent login = new Intent(mContext, Homescreen.class);
                mContext.startActivity(login);*/
            }
        });
    }
    public void findOutletName(String outletId)
    {

        db.collection("outlet").document(outletId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete())
                {
                    outlet = task.getResult().toObject(Outlet.class);
                    Dataholder.outletList.add(outlet);
                }
            }
        });

    }
    public String returnOutletName(Outlet outlet)
    {
        return outlet.getName();
    }
    public void checkUpdates(){
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
