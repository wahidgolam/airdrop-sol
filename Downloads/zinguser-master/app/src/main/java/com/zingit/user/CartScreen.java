package com.zingit.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zingit.user.model.Coupon;
import com.zingit.user.model.Payments;

import java.text.DecimalFormat;
import java.util.ArrayList;

import club.cred.neopop.PopFrameLayout;

public class CartScreen extends AppCompatActivity{

    ArrayList<Item> itemList;
    TextView upperCartTotal;
    TextView cartOutletName;
    RecyclerView cartItemRV;
    TextView cartTotal;
    ItemAdapter adapter;
    FirebaseFirestore db;
    int status;
    LoadingDialog loadingDialog;
    final int UPI_PAYMENT=123;
    String approvalRefNo = "";
    ArrayList<Order> orderList;
    PopFrameLayout applyCoupons,grandTotal;
    RelativeLayout orange_strip,bottomCart;
    TextView completeOrderText;
    ImageView clearCart;
    ImageView back_btn;
    TextView grandTotalText;
    Dialog dialog;
    Coupon coupon;
    EditText couponCode;
    TextView totalAmount;
    ImageView applyBtn;
    ImageView crossSelected;
    int flag=1;
    TextView messageText;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    ImageView historyImage,cartImage,homeImage;
    LinearLayout cart,home,history;
    LinearLayout emptyState;
    TextView taxes;
    ScrollView scrollView;
    //Boolean isApplied=false;
    ArrayList<Payments> filteredCouponPerUser= new ArrayList<Payments>();


    //flag=1 then applied btn flag=2 remove btn


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_screen);

        //upperCartTotal = findViewById(R.id.upper_cart_total);
       // cartOutletName = findViewById(R.id.cart_outlet_name);
        cartItemRV = findViewById(R.id.recyclerView);
        applyCoupons = findViewById(R.id.applyCoupons);
        grandTotal = findViewById(R.id.grandTotal);
        orange_strip = findViewById(R.id.orange_strip);
        bottomCart = findViewById(R.id.bottomCart);
        completeOrderText = findViewById(R.id.completeOrderText);
        clearCart= findViewById(R.id.clear_cart);
        back_btn = findViewById(R.id.back_btn);
        grandTotalText = findViewById(R.id.grandTotalAmt);
        couponCode = findViewById(R.id.cheatcode);
        applyBtn = findViewById(R.id.applyBtn);
        //cartTotal = findViewById(R.id.cart_total2);
        crossSelected = findViewById(R.id.crossSelected);
        totalAmount = findViewById(R.id.actualPrice);
        messageText = findViewById(R.id.messageText);
        historyImage = findViewById(R.id.history_image);
        cartImage = findViewById(R.id.cart_image);
        homeImage = findViewById(R.id.home_image);
        cart = findViewById(R.id.cart_layout);
        home = findViewById(R.id.home_layout);
        history = findViewById(R.id.history_layout);
        emptyState = findViewById(R.id.empty_cart_view);
        taxes = findViewById(R.id.taxes);
        scrollView = findViewById(R.id.scrollView);



        status = 0;
        //Firebase variables
        db = FirebaseFirestore.getInstance();
        //setting up RV
        itemList = Dataholder.cart.returnItemList();

        cartImage.setImageResource(R.drawable.cart_white);
        cart.setBackgroundResource(R.drawable.rad8_roundedlayout);
        history.setBackgroundResource(0);
        home.setBackgroundResource(0);
        homeImage.setImageResource(R.drawable.zing);
        historyImage.setImageResource(R.drawable.history_black);
        Dataholder.path=3;


        //BottomNavbar setup
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
                Intent intent = new Intent(getApplicationContext(),OrderHistory.class);
                startActivity(intent);

            }});

        if(itemList.isEmpty()){
            setupUI();
            showEmptyCart();
        }
        else{
            adapter = new ItemAdapter(itemList, 1);
            cartItemRV.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            cartItemRV.setLayoutManager(new LinearLayoutManager(this));
            /*cartItemRV.addItemDecoration(
                    new DividerItemDecoration(this, layoutManager.getOrientation()) {
                        @Override
                        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                            int position = parent.getChildAdapterPosition(view);
                            // hide the divider for the last child
                            if (position == state.getItemCount() - 1) {
                                outRect.setEmpty();
                            } else {
                                super.getItemOffsets(outRect, view, parent, state);
                            }
                        }
                    }
            );*/





            //setup initial UI
            setupUI();
        }

        bottomCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPhoneNumber(view);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Homescreen.class);
                startActivity(intent);
            }
        });

        //clear Cart pressed
        clearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCart(view);
            }
        });

        //couppon Code clicked and keyboard shift

        couponCode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                /*scrollView.smoothScrollTo(0, couponCode.getTop());
                scrollView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        couponCode.requestFocus();
                                    }
                                }
                );*/
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                return false;
            }
        });


        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(flag==2)
                {
                    couponCode.setTextColor(Color.parseColor("#2b2b2b"));
                    couponCode.setText("");
                    applyBtn.setImageResource(R.drawable.apply);
                    flag=1;
                    messageText.setText("");
                    grandTotalText.setText("₹ "+df.format(Dataholder.cart.getCartTotal() + Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));
                    couponCode.setFocusableInTouchMode(true);
                    //isApplied = false;


                }
                else if(flag==1 ){
                String c = couponCode.getText().toString().trim();
                //isApplied = true;
                if(c.length()==0)
                {

                }
                else{
                c = c.toUpperCase();
                applyCoupon(c);
                couponCode.setFocusable(false);


                    flag=1;}}
            }
        });
        crossSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                couponCode.setFocusableInTouchMode(true);
                crossSelected.setVisibility(View.GONE);
                couponCode.setText("");
                totalAmount.setVisibility(View.GONE);
                applyBtn.setImageResource(R.drawable.apply);
                messageText.setText("");
                grandTotalText.setText("₹ "+df.format(Dataholder.cart.getCartTotal() + Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));
                couponCode.setFocusableInTouchMode(true);

            }
        });
    }
    public void setupUI(){
        String price = "₹ "+Dataholder.cart.getCartTotal()+".00";
        grandTotalText.setText("₹ "+df.format(Dataholder.cart.getCartTotal() + Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));
        taxes.setText("₹ " + df.format(Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100)+ "");
        //cartTotal.setText(price);
        //String outletName = findOutletName(Dataholder.cart.getCartOutletID());
        //cartOutletName.setText(outletName);

        dialog = new Dialog(CartScreen.this);
        dialog.setContentView(R.layout.getphoneno_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if(Dataholder.studentUser.getPhoneNumber()!=null){
            //LinearLayout phoneLinearLayout = findViewById(R.id.number_linear_layout);
            //TextInputLayout phoneInput = findViewById(R.id.number_input);
            //phoneInput.setVisibility(View.INVISIBLE);
            //phoneLinearLayout.setVisibility(View.GONE);
        }
        if(Dataholder.cart.isCouponPresent())
        {
            applyBtn.setImageResource(R.drawable.applied);
            couponCode.setFocusable(false);
            crossSelected.setVisibility(View.VISIBLE);
            messageText.setVisibility(View.VISIBLE);
            String savings = df.format(Dataholder.cart.getSavings());
            messageText.setText("you have saved ₹ "+savings);
            couponCode.setText(Dataholder.cart.getCoupon().getCode()+"");
            couponCode.setFocusable(false);
            String grandTotal = df.format(Dataholder.cart.getDiscountedCartTotal());
            grandTotalText.setText("₹ "+df.format(Dataholder.cart.getDiscountedCartTotal() + Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));


        }



    }
    public String findOutletName(String outletID){
        for(int i=0; i<Dataholder.outletList.size(); i++){
            if(Dataholder.outletList.get(i).getId().equals(outletID)){
                return Dataholder.outletList.get(i).getName();
            }
        }
        return "";
    }
    public void updateCartDetails(){
        //change total price
        //set price = discounted price, set orginal price, savings
        //String price = "₹ "+Dataholder.cart.getCartTotal()+".00";
        //grandTotalText.setText(price);
        String savings = df.format(Dataholder.cart.getSavings());
        messageText.setText("you have saved ₹ "+savings);
        if(Dataholder.cart.isCouponPresent()){
            grandTotalText.setText("₹ " +df.format(Dataholder.cart.getDiscountedCartTotal() + Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));}
        else{
                grandTotalText.setText("₹ " +df.format(Dataholder.cart.getCartTotal() + Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));}

        //grandTotalText.setText("₹ " +df.format(Dataholder.cart.getDiscountedCartTotal() + Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));
        taxes.setText("₹ " + df.format(Dataholder.cart.getCartTotal()*Dataholder.taxPercentage/100) +"");



        if(Dataholder.cart.getCartItems().isEmpty()){
            showEmptyCart();
        }
    }
    public void showEmptyCart(){

        applyCoupons.setVisibility(View.GONE);
        grandTotal.setVisibility(View.GONE);
        orange_strip.setVisibility(View.GONE);
        bottomCart.setVisibility(View.GONE);
        completeOrderText.setVisibility(View.GONE);
        clearCart.setVisibility(View.GONE);
        messageText.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);





    }
    public  void clearCart(View view){
        Dataholder.cart.flushCart();
        itemList.clear();
        adapter.notifyDataSetChanged();
        updateCartDetails();
        showEmptyCart();
    }
    public void goToHome(View view){
        Intent intent = new Intent(getApplicationContext(), Homescreen.class);
        startActivity(intent);
        finish();
    }
    public void payAndOrder(){
        dialog.dismiss();
        ArrayList<Item> newItems = new ArrayList<>();
        String outletID = Dataholder.cart.getCartOutletID();
        loadingDialog = new LoadingDialog(CartScreen.this, "Placing your order");
        loadingDialog.startLoadingDialog();
        db.collection("item").whereEqualTo("outletID", outletID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Item item = document.toObject(Item.class);
                        newItems.add(item);
                    }
                    int remPos = Dataholder.cart.removeNotAvailableItems(newItems);
                    if (remPos != -1) {
                        updateCartDetails();
                        Toast.makeText(CartScreen.this, "Some items aren't available. We have updated your cart", Toast.LENGTH_SHORT).show();
                        itemList = Dataholder.cart.returnItemList();
                        adapter.notifyDataSetChanged();
                        loadingDialog.dismissDialog();
                    } else {
                        //step1 -> verifying all items are available
                        //step2 -> verify outlet is open
                        checkOutletOpen();
                    }
                } else {
                    Log.d("New item data: cart", "Error getting documents: ", task.getException());
                }
            }
        });

    }
    String phoneNumber = null;
    public void getPhoneNumber(View view){
        if(Dataholder.studentUser.getPhoneNumber()==null) {
            PopFrameLayout submitNo = dialog.findViewById(R.id.no1);
            EditText phoneNo = dialog.findViewById(R.id.phoneNo);
            dialog.show();
            submitNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    phoneNumber = phoneNo.getText().toString();
                    if (phoneNumber.length() == 12) {
                        if (phoneNumber.substring(0, 2).equals("91")) {
                            phoneNumber = phoneNumber.substring(2);
                            updatePhoneNumber(phoneNumber);
                            payAndOrder();
                            //okay
                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter an Indian number", Toast.LENGTH_SHORT).show();
                            //not okay
                        }
                    } else if (phoneNumber.length() == 10) {
                        updatePhoneNumber(phoneNumber);
                        payAndOrder();
                        //okay
                    } else {
                        Toast.makeText(getApplicationContext(), "Phone number is not valid", Toast.LENGTH_SHORT).show();
                        //not okay
                    }
                }
            });

        }
        else{
            payAndOrder();
        }
    }
    public void updatePhoneNumber(String phoneNumber){
        Dataholder.studentUser.setPhoneNumber(phoneNumber);
        db.collection("studentUser").document(Dataholder.studentUser.getUserID()).update("phoneNumber", phoneNumber);
    }
    public void checkOutletOpen(){
        String outletId = Dataholder.cart.getCartOutletID();
        db.collection("outlet").document(outletId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String status = task.getResult().toObject(Outlet.class).getOpenStatus();
                if(status.equals("OPEN")){
                    moveToResult();
                }
                else{
                    loadingDialog.dismissDialog();
                    Toast.makeText(CartScreen.this, "Sorry, the restaurant is closed now. Cannot place order", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void moveToResult(){
        loadingDialog.dismissDialog();
        Intent intent = new Intent(getApplicationContext(), ResultScreen.class);
        startActivity(intent);
        finish();
    }
    public void applyCoupon(String code){
        db.collection("coupon").whereEqualTo("code", code).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                coupon = null;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        coupon = document.toObject(Coupon.class);
                    }
                    if(coupon==null){
                        messageText.setVisibility(View.VISIBLE);
                        messageText.setText("Sorry the coupon does not exist :(");

                        //clear coupon edittext
                        applyBtn.setImageResource(R.drawable.remove);
                        couponCode.setTextColor(Color.parseColor("#ED2E3F"));
                        String tt = couponCode.getText().toString();
                        couponCode.setText(tt + " (error)");
                        flag=2;
                        //isApplied = true;
                    }
                    else{
                        //perform checks if coupon is applicable
                        int applyOn = coupon.getApplyOn();
                        ArrayList<String> applyID = coupon.getApplyID();
                        //0 -> user
                        //1 -> items
                        //2 -> outlets
                        //3 -> campus
                        boolean couponPassed = false;
                        switch(applyOn){
                            case 0:
                                //user
                                if(applyID.contains(Dataholder.studentUser.getUserID())){
                                    couponPassed = true;
                                }
                                else{
                                    messageText.setVisibility(View.VISIBLE);
                                    messageText.setText("Sorry, the coupon code isn't applicable for this order");
                                    //ui changes
                                    applyBtn.setImageResource(R.drawable.remove);
                                    couponCode.setTextColor(Color.parseColor("#ED2E3F"));
                                    String tt = couponCode.getText().toString();
                                    couponCode.setText(tt + " (error)");
                                    flag=2;
                                }
                                break;
                            case 1:
                                //handle separately
                                for(int i=0; i<Dataholder.cart.returnItemList().size(); i++){
                                    if (applyID.contains(Dataholder.cart.returnItemList().get(i).getId())){
                                        couponPassed = true;
                                    }
                                    if(!couponPassed){
                                        messageText.setVisibility(View.VISIBLE);

                                        messageText.setText("Sorry, the coupon code isn't applicable for this order");

                                        //ui changes
                                        applyBtn.setImageResource(R.drawable.remove);
                                        couponCode.setTextColor(Color.parseColor("#ED2E3F"));
                                        String tt = couponCode.getText().toString();
                                        couponCode.setText(tt + " (error)");
                                        flag=2;
                                    }
                                }
                                break;
                            case 2:
                                if(applyID.contains(Dataholder.cart.getCartOutletID())){
                                    couponPassed = true;
                                }
                                else{
                                    messageText.setVisibility(View.VISIBLE);
                                    messageText.setText("Sorry, the coupon code isn't applicable for this order");
                                    applyBtn.setImageResource(R.drawable.remove);
                                    couponCode.setTextColor(Color.parseColor("#ED2E3F"));
                                    String tt = couponCode.getText().toString();
                                    couponCode.setText(tt + " (error)");
                                    flag=2;
                                    couponCode.setFocusable(false);

                                    //ui changes
                                }
                                break;
                            case 3:
                                if(applyID.contains(Dataholder.studentUser.getCampusID())){
                                    couponPassed = true;
                                }
                                else{
                                    messageText.setVisibility(View.VISIBLE);
                                    messageText.setText("Sorry, the coupon code isn't applicable for this order");
                                    //ui changes
                                    applyBtn.setImageResource(R.drawable.remove);
                                    applyBtn.setImageResource(R.drawable.remove);
                                    couponCode.setTextColor(Color.parseColor("#ED2E3F"));
                                    String tt = couponCode.getText().toString();
                                    couponCode.setText(tt + " (error)");
                                    flag=2;
                                    couponCode.setFocusable(false);


                                }
                                break;
                        }
                        if(couponPassed){
                            final int limit = coupon.getCouponLimitPerUser();
                            final int preReqValue = coupon.getPreReqOrderValue();




                            ArrayList<Payments> payments = new ArrayList<>();

                            db.collection("payments").whereEqualTo("couponID", coupon.getCode()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int m;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Payments payments1 = document.toObject(Payments.class);
                                        payments.add(payments1);

                                    }
                                    m = filteredCouponPerUser(payments,limit);

                                    if(m==0){
                                        messageText.setVisibility(View.VISIBLE);
                                        messageText.setText("You have reached the limit of coupon usage");
                                        applyBtn.setImageResource(R.drawable.remove);
                                        couponCode.setTextColor(Color.parseColor("#ED2E3F"));
                                        String tt = couponCode.getText().toString();
                                        couponCode.setText(tt + " (error)");
                                        flag=2;
                                        couponCode.setFocusable(false);
                                        //ui changes
                                    }
                                    else
                                    {
                                        if(coupon.getTotalLimit()<=payments.size())
                                        {
                                            messageText.setVisibility(View.VISIBLE);
                                            messageText.setText("This coupon is no longer valid");
                                            applyBtn.setImageResource(R.drawable.remove);
                                            couponCode.setTextColor(Color.parseColor("#ED2E3F"));
                                            String tt = couponCode.getText().toString();
                                            couponCode.setText(tt + " (error)");
                                            flag=2;
                                            couponCode.setFocusable(false);
                                            //error
                                        }
                                        else
                                        {
                                            if(Dataholder.cart.getCartTotal()>=preReqValue){
                                                //passed
                                                Dataholder.cart.setCoupon(coupon);
                                                String strikedText = "₹ "+df.format(Dataholder.cart.getCartTotal());
                                                totalAmount.setVisibility(View.VISIBLE);
                                                totalAmount.setText(strikedText);
                                                totalAmount.setPaintFlags(totalAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                                totalAmount.setVisibility(View.VISIBLE);
                                                String grandTotal = df.format(Dataholder.cart.getDiscountedCartTotal());
                                                grandTotalText.setText("₹ " +df.format(Dataholder.cart.getDiscountedCartTotal() + Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));
                                                applyBtn.setImageResource(R.drawable.applied);
                                                couponCode.setFocusable(false);
                                                crossSelected.setVisibility(View.VISIBLE);
                                                messageText.setVisibility(View.VISIBLE);
                                                String savings = df.format(Dataholder.cart.getSavings());
                                                messageText.setText("you have saved ₹ "+savings);

                                            }
                                            else{
                                                messageText.setVisibility(View.VISIBLE);
                                                messageText.setText("Coupon valid only for orders above ₹"+ preReqValue);
                                                applyBtn.setImageResource(R.drawable.remove);
                                                couponCode.setTextColor(Color.parseColor("#ED2E3F"));
                                                String tt = couponCode.getText().toString();
                                                couponCode.setText(tt + " (error)");
                                                flag=2;
                                                couponCode.setFocusable(false);



                                                //ui
                                            }
                                        }
                                    }

                                    /*else{
                                        if(Dataholder.cart.getCartTotal()>=preReqValue){
                                            //passed
                                            Dataholder.cart.setCoupon(coupon);
                                            String strikedText = "₹ "+df.format(Dataholder.cart.getCartTotal());
                                            totalAmount.setVisibility(View.VISIBLE);
                                            totalAmount.setText(strikedText);
                                            totalAmount.setPaintFlags(totalAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                            totalAmount.setVisibility(View.VISIBLE);
                                            String grandTotal = df.format(Dataholder.cart.getDiscountedCartTotal());
                                            grandTotalText.setText("₹ " +df.format(Dataholder.cart.getDiscountedCartTotal() + Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));
                                            applyBtn.setImageResource(R.drawable.applied);
                                            couponCode.setFocusable(false);
                                            crossSelected.setVisibility(View.VISIBLE);
                                            messageText.setVisibility(View.VISIBLE);
                                            String savings = df.format(Dataholder.cart.getSavings());
                                            messageText.setText("you have saved ₹ "+savings);

                                            //Toast.makeText(CartScreen.this, Dataholder.cart.getSavings()+"", Toast.LENGTH_SHORT).show();
                                            }
                                        else{
                                            messageText.setVisibility(View.VISIBLE);
                                            messageText.setText("Coupon valid only for orders above ₹"+ preReqValue);
                                            applyBtn.setImageResource(R.drawable.remove);
                                            couponCode.setTextColor(Color.parseColor("#ED2E3F"));
                                            String tt = couponCode.getText().toString();
                                            couponCode.setText(tt + " (error)");
                                            flag=2;
                                            couponCode.setFocusable(false);



                                            //ui
                                        }
                                    }*/
                                }
                            });
                        }
                    }
                }
                else{
                    messageText.setVisibility(View.VISIBLE);
                    messageText.setText("Sorry the coupon does not exist :(");
                    //clear coupon edittext
                    applyBtn.setImageResource(R.drawable.remove);
                    couponCode.setTextColor(Color.parseColor("#ED2E3F"));
                    String tt = couponCode.getText().toString();
                    couponCode.setText(tt + " (error)");
                    flag=2;
                }
            }
        });
    }
    public int filteredCouponPerUser(ArrayList<Payments> totalPayments,int limit)
    {
        int c=0;
        for(int i=0;i<totalPayments.size();i++)
        {
            if(totalPayments.get(i).getUserID().equals(Dataholder.userId))
                c++;
            if(c>=limit)
            {
                return 0;
            }
        }
        return 1;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),Homescreen.class);
        startActivity(intent);
    }

}