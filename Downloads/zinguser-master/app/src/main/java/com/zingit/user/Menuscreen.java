package com.zingit.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.internal.TextWatcherAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class Menuscreen extends AppCompatActivity {
    TextView outletName;
    TextView outletDesc;
    TextView outletStatus;
    TextView outletZingTime;
    CardView cartCard;
    TextView itemNumber;
    TextView cartTotal;
    TextView noOfItems;
    RecyclerView itemRV;
    Outlet currentOutlet;
    FirebaseFirestore db;
    ArrayList<Item> itemList = new ArrayList<>();
    MenuAdapter adapter;
    LoadingDialog loadingDialog;
    EditText search;
    Switch veg,nonVeg;
    TextView zingTime;
    RelativeLayout goToCart;
    RelativeLayout bottomCart;
    RelativeLayout orangeStrip;
    ImageView back_img;
    LinearLayout cart,home,history;
    ImageView cartImage,homeImage,historyImage;
    ArrayList<Item> searchItems = new ArrayList<>();

    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuscreen);

        //initialising firebase variables
        db = FirebaseFirestore.getInstance();

        currentOutlet = Dataholder.currentOutlet;
        outletName = findViewById(R.id.outlet_name);
        outletDesc = findViewById(R.id.outlet_desc);
        //outletStatus = findViewById(R.id.outlet_status);
        outletZingTime = findViewById(R.id.outlet_zing_time);
        itemRV = findViewById(R.id.itemRV);
        bottomCart = findViewById(R.id.bottomCart);
        orangeStrip = findViewById(R.id.orange_strip);
        //cartCard = findViewById(R.id.cart_deails);
        //itemNumber = findViewById(R.id.item_number);
        //cartTotal = findViewById(R.id.cart_total);
        search = findViewById(R.id.search_id);
        veg = findViewById(R.id.veg);
        nonVeg = findViewById(R.id.nonveg);
        zingTime = findViewById(R.id.zing_time);
        goToCart = findViewById(R.id.bottomCart);
        noOfItems = findViewById(R.id.no_of_items);
        back_img = findViewById(R.id.back_img);
        cart = findViewById(R.id.cart_layout);
        home = findViewById(R.id.home_layout);
        history = findViewById(R.id.history_layout);
        cartImage = findViewById(R.id.cart_image);
        homeImage = findViewById(R.id.home_image);
        historyImage = findViewById(R.id.history_image);


        loadingDialog = new LoadingDialog(Menuscreen.this, "Fetching restaurant menu");
        loadingDialog.startLoadingDialog();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                status++;
                Log.d("Disappear", String.valueOf(status));
                disappearDialog();
            }
        }, 1000);

        goToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CartScreen.class);
                startActivity(intent);
            }
        });

        //Veg and NonVeg selection

        veg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Item> bufferedItem = (ArrayList<Item>) searchItems.clone();
                nonVeg.setChecked(false);
                ArrayList<Item> vegItems = new ArrayList<Item>();
                if(bufferedItem.size()>0){
                    for(Item item : bufferedItem)
                    {
                        if(item.isVegOrNot())
                            vegItems.add(item);
                    }
                }
                else {
                    for(Item item : itemList)
                    {
                        if(item.isVegOrNot())
                            vegItems.add(item);
                    }
                }

                if(veg.isChecked()==false)
                    adapter.filterVegList(bufferedItem);
                else
                    adapter.filterlist(vegItems);

            }
        });

        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        nonVeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Item> bufferedItem = (ArrayList<Item>) searchItems.clone();

                veg.setChecked(false);
                ArrayList<Item> nonVegItems = new ArrayList<Item>();

                if(bufferedItem.size()>0)
                {
                    for(Item item : bufferedItem)
                    {
                        if(!item.isVegOrNot())
                            nonVegItems.add(item);
                    }
                }
                else
                {
                    for(Item item : itemList)
                    {
                        if(!item.isVegOrNot())
                            nonVegItems.add(item);
                    }
                }



                if(nonVeg.isChecked()==false)
                    adapter.filterVegList(bufferedItem);
                else
                    adapter.filterlist(nonVegItems);

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
                Intent intent = new Intent(getApplicationContext(),Homescreen.class);
                startActivity(intent);
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


        //setting up recycler view
        adapter = new MenuAdapter(itemList);
        itemRV.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itemRV.setLayoutManager(new LinearLayoutManager(this));

        setupUI();




        search.addTextChangedListener(new TextWatcher() {
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
            // Filtering the menu
            private void filter(String toString) {
                ArrayList<Item> filteredItems = new ArrayList<Item>();

                if(veg.isChecked())
                {
                    for(Item item : itemList)
                    {
                        if(item.isVegOrNot()){
                        if(item.getName().toLowerCase().contains(toString.toLowerCase()))
                        {
                            filteredItems.add(item);

                        }}

                    }
                }

                else if(nonVeg.isChecked())
                {
                    for(Item item : itemList)
                    {
                        if(!item.isVegOrNot()){
                            if(item.getName().toLowerCase().contains(toString.toLowerCase()))
                            {
                                filteredItems.add(item);

                            }}

                    }
                }
                else {
                    for (Item item : itemList) {
                        if (item.getName().toLowerCase().contains(toString.toLowerCase())) {
                            filteredItems.add(item);

                        }

                    }
                }
                adapter.filterlist(filteredItems);
                searchItems = filteredItems;
            }
        });

        setupUI();
        //updateCartDetails();

        //updating list of items
        updateItemList(currentOutlet.getId());
    }
    public void setupUI(){
        outletName.setText(currentOutlet.getName());
        outletDesc.setText(currentOutlet.getDescription());
        //outletStatus.setText(currentOutlet.getOpenStatus());
        String outletZingDisplayText = currentOutlet.getZingTime()+" mins";
        outletZingTime.setText(outletZingDisplayText);
        zingTime.setText(currentOutlet.getZingTime());
        setupCartView();


    }
    public void setupCartView(){
        Log.e("Eedhar", Dataholder.cart.getCartItems().size()+"");
        if(Dataholder.cart.getCartItems().size()>0){
            String noOfItemsText = Dataholder.cart.getCartItems().size()==1?Dataholder.cart.getCartItems().size()+" item":Dataholder.cart.getCartItems().size()+" items";

            noOfItems.setText(noOfItemsText);
            bottomCart.setVisibility(View.VISIBLE);
            orangeStrip.setVisibility(View.VISIBLE);
        }
        else{
            bottomCart.setVisibility(View.GONE);
            orangeStrip.setVisibility(View.GONE);
        }
    }
    /*public void updateCartDetails(){
        if(Dataholder.cart.getCartItems().isEmpty()){
            //cartCard.setVisibility(View.INVISIBLE);
        }
        else{
            cartCard.setVisibility(View.VISIBLE);
            String outletName = findOutletName(Dataholder.cart.getCartOutletID()).toUpperCase(Locale.ROOT);
            String item = Dataholder.cart.getTotalCount()==1? " ITEM":" ITEMS";
            String number = Dataholder.cart.getTotalCount()+item+" FROM "+outletName;
            String price = "₹ "+Dataholder.cart.getCartTotal()+".00";
            itemNumber.setText(number);
            cartTotal.setText(price);
        }
    }*/
    public String findOutletName(String outletID){
        for(int i=0; i<Dataholder.outletList.size(); i++){
            if(Dataholder.outletList.get(i).getId().equals(outletID)){
                return Dataholder.outletList.get(i).getName();
            }
        }
        return "";
    }
    public void updateItemList(String outletID){
        db.collection("item")
                .whereEqualTo("outletID", outletID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Item item = document.toObject(Item.class);
                                itemList.add(item);
                            }
                            itemList.sort(Comparator.comparing(Item::getCategory));
                            //TODO handle items not available
                            //handle itemListEmpty
                            //add item image
                            adapter.notifyDataSetChanged();
                            Log.d("Disappear", "CALL1");
                            status++;
                            Log.d("Disappear", String.valueOf(status));
                            disappearDialog();
                        } else {
                            Log.d("Outlet Data", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void moveToCart(View view){
        Intent intent = new Intent(this, CartScreen.class);
        startActivity(intent);
        finish();
    }
    public void disappearDialog(){
        if(status==2){
            loadingDialog.dismissDialog();
        }
    }
}