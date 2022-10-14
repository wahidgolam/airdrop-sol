package com.zingit.user;

import static java.util.concurrent.TimeUnit.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cashfree.pg.api.CFPaymentGatewayService;
import com.cashfree.pg.core.api.CFSession;
import com.cashfree.pg.core.api.CFTheme;
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback;
import com.cashfree.pg.core.api.exception.CFException;
import com.cashfree.pg.core.api.utils.CFErrorResponse;
import com.cashfree.pg.ui.api.CFDropCheckoutPayment;
import com.cashfree.pg.ui.api.CFPaymentComponent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firestore.v1.DocumentTransform;
import com.zingit.user.model.CashFreeToken;
import com.zingit.user.model.Payments;
import com.zingit.user.model.RefundToken;
import com.zingit.user.remote.ICloudFunction;
import com.zingit.user.remote.RefundCloudFunction;
import com.zingit.user.remote.RefundRetrofitClient;
import com.zingit.user.remote.RetroFitClient;
import com.zingit.user.remote.VerifyCloudFunction;
import com.zingit.user.remote.VerifyRetrofitClient;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import club.cred.neopop.PopFrameLayout;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ResultScreen extends AppCompatActivity implements CFCheckoutResponseCallback {

    String paymentOrderID = "";
    String orderAmount = "";
    String customerID = "";
    String customerEmail = "";
    String customerPhoneNumber = "";

    String cfOrderID = "ORDER_ID";
    String cfToken = "TOKEN";
    String orderStatus = "UNINITIATED";
    ArrayList<Order> orderList;
    CFSession.Environment cfEnvironment = CFSession.Environment.PRODUCTION;
    ICloudFunction iCloudFunction;
    VerifyCloudFunction verifyCloudFunction;
    FirebaseFirestore db;
    TextView date;
    TextView viewMap;
    TextView restaurantName;
    int flag=0;
    private static final DecimalFormat df = new DecimalFormat("0.00");



    String subHead = "Tap on your order to reveal dispatch QR in the Homescreen";
    String months[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};


    TextView heading;
    TextView subHeading;
    ImageView resultImage;
    LinearLayout resultScreen;
    LoadingDialog paymentDialog1;
    LoadingDialog paymentDialog2;
    LoadingDialog orderDialog;
    CompositeDisposable compositeDisposable;
    RecyclerView recyclerView;
    ResultScreenAdapter resultScreenAdapter;
    ArrayList<CartItem> cartArrayList;
    TextView itemPrice;
    PopFrameLayout close,orders;
    TextView tryAgainText;
    Outlet outlet;


    int ordersPlaced = 0;
    RefundCloudFunction refundCloudFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);

        heading = findViewById(R.id.head_text);
        resultImage = findViewById(R.id.result_image);
        recyclerView = findViewById(R.id.recyclerView);
        itemPrice = findViewById(R.id.item_price);
        close = findViewById(R.id.close_btn);
        cartArrayList = new ArrayList<CartItem>();
        date = findViewById(R.id.date);
        tryAgainText = findViewById(R.id.try_againtext);
        orders = findViewById(R.id.orders);
        viewMap = findViewById(R.id.viewMap);
        restaurantName = findViewById(R.id.restaurant_name);

        compositeDisposable = new CompositeDisposable();
        refundCloudFunction = RefundRetrofitClient.getInstance().create(RefundCloudFunction.class);


        //setting up recyclerView
        resultScreenAdapter = new ResultScreenAdapter(cartArrayList);
        recyclerView.setAdapter(resultScreenAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Homescreen.class);
                startActivity(intent);
            }
        });

        tryAgainText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==1)
                {
                    openWhatsapp(view);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), CartScreen.class);
                    startActivity(intent);
                }
            }
        });
        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ViewMap();
            }
        });
        //setting UI
        setUI();


        compositeDisposable = new CompositeDisposable();

        db = FirebaseFirestore.getInstance();

        paymentDialog1 = new LoadingDialog(ResultScreen.this, "Initiating payment. Do not close app");
        paymentDialog2 = new LoadingDialog(ResultScreen.this, "Verifying payment");
        orderDialog = new LoadingDialog(ResultScreen.this, "Placing your order. Do not close app");

        paymentDialog1.startLoadingDialog();
        iCloudFunction = RetroFitClient.getInstance().create(ICloudFunction.class);
        verifyCloudFunction = VerifyRetrofitClient.getInstance().create(VerifyCloudFunction.class);
        makePaymentRequest();
        try {
            CFPaymentGatewayService.getInstance().setCheckoutCallback(this);
        } catch (CFException e) {
            e.printStackTrace();
        }

    }

    private void setUI() {
        outlet = new Outlet();
        String outletId = Dataholder.cart.getCartOutletID();
        findOutlet(outletId);
        for(int i=0;i<Dataholder.cart.getCartItems().size();i++)
        {
            cartArrayList.add(Dataholder.cart.getCartItems().get(i));
        }
        //cartArrayList = Dataholder.cart.getCartItems();
        Log.e("Name",cartArrayList.get(0).getItem().getName());
        resultScreenAdapter.notifyDataSetChanged();
        if(Dataholder.cart.isCouponPresent())
        {
            itemPrice.setText("₹ "+df.format(Dataholder.cart.getDiscountedCartTotal())+"");
        }
        else
        {
            itemPrice.setText("₹ "+df.format(Dataholder.cart.getCartTotal())+"");

        }
        restaurantName.setText(outlet.getName());

        heading.setVisibility(View.INVISIBLE);
        resultImage.setVisibility(View.INVISIBLE);
        orders.setVisibility(View.INVISIBLE);
        close.setVisibility(View.GONE);

        SimpleDateFormat dnt = new SimpleDateFormat("dd-mm-yy");
        Date date1 = new Date();
        date.setText(dnt.format(date1));

    }

    public void initiate(){
        //get variables
        orderList = Dataholder.cart.createOrdersFromCart();
        paymentOrderID = UUID.randomUUID().toString();
        for(int i=0; i<orderList.size(); i++){
            orderList.get(i).setPaymentOrderID(paymentOrderID);
        }

        if(Dataholder.cart.isCouponPresent())
        {
            orderAmount = String.valueOf(df.format(Dataholder.cart.getDiscountedCartTotal() + Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));
        }
        else
        {
            orderAmount = String.valueOf(df.format(Dataholder.cart.getCartTotal() + Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));
        }
        customerID = Dataholder.studentUser.getUserID();
        customerEmail = Dataholder.studentUser.getEmail();
        customerPhoneNumber = Dataholder.studentUser.getPhoneNumber();
    }
    private void makePaymentRequest(){
        Log.d("makePaymentRequest", "Function initiated");
        initiate();
        compositeDisposable.add(iCloudFunction.getToken(paymentOrderID, orderAmount, customerID, customerEmail, customerPhoneNumber).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CashFreeToken>() {
                    @Override
                    public void accept(CashFreeToken cashFreeToken) throws Exception {
                        if(cashFreeToken.getOrder_token()!=null) {
                            cfToken = cashFreeToken.getOrder_token();
                            cfOrderID = cashFreeToken.getCf_order_id();
                            orderStatus = cashFreeToken.getOrder_status();
                            paymentDialog1.dismissDialog();
                            doDropCheckoutPayment();
                        }
                        else{
                            subHead = "Failed to initiate the payment. Contact support";
                            paymentDialog1.dismissDialog();
                            showPaymentFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(ResultScreen.this, "error: "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("Initiating Payment: Error", throwable.getMessage());
                        subHead = "Failed to initiate the payment. Contact support";
                        paymentDialog1.dismissDialog();
                        showPaymentFailure();

                    }
                }));

    }
    public void goToHome2(View view){
        Intent intent = new Intent(this, Homescreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Homescreen.class);
        startActivity(intent);
        finish();
    }
    int paymentVerifyTrials = 0;

    @Override
    public void onPaymentVerify(String s) {
        if(paymentVerifyTrials>0){
            paymentDialog2.text = "Please check your network connection";
        }
        paymentDialog2.startLoadingDialog();
        Log.e("onPaymentVerify", "verifyPayment triggered");
        compositeDisposable.add(verifyCloudFunction.getToken(paymentOrderID).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CashFreeToken>() {
                    @Override
                    public void accept(CashFreeToken cashFreeToken) throws Exception {
                        if(cashFreeToken.getOrder_token()!=null) {
                            orderStatus = cashFreeToken.getOrder_status();
                            if(orderStatus.equals("PAID")){
                                paymentDialog2.dismissDialog();
                                //payment successful
                                placeOrder();
                            }
                            else{
                                paymentDialog2.dismissDialog();
                                //not successful
                                triggerRefund();
                                //subHead = "Failed to verify payment. Contact support if money has been deducted from your account.";
                                //showPaymentFailure();
                                //show failure screen
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //2
                        Log.d("ERROR VERIFICATION", throwable.getMessage());
                        //not successful
                        paymentVerifyTrials++;
                        if(paymentVerifyTrials<10){
                            try {
                                Thread.sleep(5000L *paymentVerifyTrials);
                                paymentDialog2.dismissDialog();
                                Log.e("Repeat Payment Verify", "Place Order thread me hai");
                                onPaymentVerify(s);

                            } catch (InterruptedException e) {
                                paymentDialog2.dismissDialog();
                                e.printStackTrace();
                            }

                        }
                        else{
                            paymentDialog2.dismissDialog();
                            triggerRefund();
                        }
                        //subHead = "Failed to verify payment. Contact support if money has been deducted from your account.";
                        //showPaymentFailure();
                    }
                }));
    }
    @Override
    public void onPaymentFailure(CFErrorResponse cfErrorResponse, String s) {
        //3
        Log.e("onPaymentFailure " + cfOrderID, cfErrorResponse.getMessage());
        //show failure screen
        subHead = "Failed to verify payment. Contact support if money has been deducted from your account.";
        showPaymentFailure();

    }
    public void doDropCheckoutPayment() {
        try {
            CFSession cfSession = new CFSession.CFSessionBuilder()
                    .setEnvironment(cfEnvironment)
                    .setOrderToken(cfToken)
                    .setOrderId(paymentOrderID)
                    .build();
            CFPaymentComponent cfPaymentComponent = new CFPaymentComponent.CFPaymentComponentBuilder()
                    // Shows only UPI mode
                    .add(CFPaymentComponent.CFPaymentModes.UPI)
                    .build();
            // Replace with your application's theme colors
            CFTheme cfTheme = new CFTheme.CFThemeBuilder()
                    .setNavigationBarBackgroundColor("#F5765B")
                    .setNavigationBarTextColor("#ffffff")
                    .setButtonBackgroundColor("#F5765B")
                    .setButtonTextColor("#ffffff")
                    .setPrimaryTextColor("#000000")
                    .setSecondaryTextColor("#000000")
                    .build();
            CFDropCheckoutPayment cfDropCheckoutPayment = new CFDropCheckoutPayment.CFDropCheckoutPaymentBuilder()
                    .setSession(cfSession)
                    .setCFUIPaymentModes(cfPaymentComponent)
                    .setCFNativeCheckoutUITheme(cfTheme)
                    .build();
            CFPaymentGatewayService gatewayService = CFPaymentGatewayService.getInstance();
            gatewayService.doPayment(ResultScreen.this, cfDropCheckoutPayment);
        } catch (CFException exception) {
            exception.printStackTrace();
        }
    }
    public void showPaymentFailure(){
        //resultScreen.setVisibility(View.VISIBLE);
        heading.setVisibility(View.VISIBLE);
        resultImage.setVisibility(View.VISIBLE);
        heading.setText("Order Unsuccessful");
        //subHeading.setText(subHead);
        resultImage.setImageResource(R.drawable.unsuccessful);
        //Dataholder.cart.flushCart();
        tryAgainText.setVisibility(View.VISIBLE);
        if(flag==0)
        {
            tryAgainText.setText("Try Again");
        }
        else
        {
            tryAgainText.setText("Money has been debited");
        }
        orders.setVisibility(View.GONE);
        close.setVisibility(View.VISIBLE);
    }
    public void showPaymentSuccessful(){
        if(ordersPlaced==orderList.size()){
            Dataholder.cart.flushCart();
            //resultScreen.setVisibility(View.VISIBLE);
            heading.setVisibility(View.VISIBLE);
            resultImage.setVisibility(View.VISIBLE);
            heading.setText("Order Successful");
            orders.setVisibility(View.VISIBLE);
            close.setVisibility(View.VISIBLE);

            //
            resultImage.setImageResource(R.drawable.successful);
        }
    }
    public void storePayments()
    {
        String id="";
        String coupon="";
        String tempPaymentOrderId;
        String userId;
        double couponDiscount=0.0;
        double totalAmountPaid=0.0;
        double taxesAndCharges=0.0;
        double basePrice=0.0;
        Timestamp timestamp;
        if(Dataholder.cart.isCouponPresent()) {
            coupon = Dataholder.cart.getCoupon().getCode();
            totalAmountPaid = Double.parseDouble(df.format(Dataholder.cart.getDiscountedCartTotal() + Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));
            couponDiscount =  Double.parseDouble(df.format(Dataholder.cart.getCartTotal() - Dataholder.cart.getDiscountedCartTotal()));
        }
        else
        {
            totalAmountPaid = Double.parseDouble(df.format(Dataholder.cart.getCartTotal() + Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));
        }
        basePrice = Dataholder.cart.getCartTotal();
        taxesAndCharges = Double.parseDouble(df.format(Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));
        tempPaymentOrderId = paymentOrderID;
        userId = Dataholder.userId;
        Date date = new Date();
        timestamp = new Timestamp(date);

        DocumentReference documentReference = db.collection("payments").document();
        id = documentReference.getId();
        Payments payments = new Payments(id,coupon,tempPaymentOrderId,userId,totalAmountPaid,taxesAndCharges,couponDiscount,basePrice,timestamp);

        db.collection("payments").document(id).set(payments).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               showPaymentSuccessful();
            }
        });

    }
    int placeOrderTrials = 0;
    public void placeOrder(){
        if(placeOrderTrials>0){
            orderDialog.text = "Please check your network";
        }
        orderDialog.startLoadingDialog();
        Log.e("Repeated Order Placed", "Place Order me hai");
        if(orderList!=null) {
            for (int i = 0; i < orderList.size(); i++) {
                Order order = orderList.get(i);
                order.setPaymentStatus(1);
                DocumentReference orderRef = db.collection("order").document();
                String id = orderRef.getId();
                order.setOrderID(id);
                db.collection("order").document(id).set(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            orderDialog.dismissDialog();
                            ordersPlaced++;
                            storePayments();
                            //showPaymentSuccessful();

                        }
                        else{
                            //4
                            placeOrderTrials++;
                            if(placeOrderTrials<3){
                                try {
                                    Thread.sleep(3000L *placeOrderTrials);
                                    orderDialog.dismissDialog();
                                    Log.e("Repeated Order Placed", "Place Order thread me hai");
                                    placeOrder();


                                } catch (InterruptedException e) {
                                    orderDialog.dismissDialog();
                                    e.printStackTrace();
                                }

                            }
                            else{
                                orderDialog.dismissDialog();
                                triggerRefund();
                            }
                            //subHead = "Failed to verify payment. Contact support if money has been deducted from your account.";
                            //showPaymentFailure();
                        }
                    }
                });

            }
        }
    }
    public void triggerRefund(){
        orderAmount = String.valueOf(df.format(Dataholder.cart.getDiscountedCartTotal() + Dataholder.taxPercentage*Dataholder.cart.getCartTotal()/100));
        LoadingDialog loadingDialog2 = new LoadingDialog(ResultScreen.this, "Order unsuccessful. Initiating refund");
        loadingDialog2.startLoadingDialog();
        Log.e("Trigger Refund", "Trigger Refund");
        compositeDisposable.add(refundCloudFunction.getToken(paymentOrderID, orderAmount).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RefundToken>() {
                    @Override
                    public void accept(RefundToken refundToken) throws Exception {
                        if(refundToken.getCf_refund_id()!=null) {
                            //refund successful
                            loadingDialog2.dismissDialog();
                            subHead = "Order unsuccessful. Refund has been initiated";
                            showPaymentFailure();
                        }
                        else{
                            subHead = "Order unsuccessful. Contact support if any amount is debited";
                            flag=1;
                            showPaymentFailure();
                            loadingDialog2.dismissDialog();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("Initiating Payment: Error", throwable.getMessage());
                        loadingDialog2.dismissDialog();
                        subHead = "Order unsuccessful. Contact support if any amount is debited";
                        flag=1;
                        showPaymentFailure();
                    }
                }));

    }
    public void openWhatsapp(View view){

        String text = "Payment Order ID: "+ paymentOrderID+", Order Amount: "+orderAmount + "\n" + "Hi, I am "+Dataholder.studentUser.getFirstName()+" and My order was unsuccessful...";
        String url = "https://api.whatsapp.com/send?phone=" + Dataholder.contactNumber+ "&text=" + text;
        try {
            PackageManager pm = this.getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.putExtra(Intent.EXTRA_TEXT, text);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    public void ViewMap()
    {
        if(outlet.getLongitude()==null || outlet.getLongitude().equals(""))
        {
            Toast.makeText(getApplicationContext(), "Map data isn't currently available :(", Toast.LENGTH_SHORT).show();
        }
        else {
            String lat = outlet.getLatitude();
            String longitude = outlet.getLongitude();

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + outlet.getLatitude() + "," + outlet.getLongitude() + "&mode=w"));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                startActivity(intent);
            }
        }
    }

    public void findOutlet(String outletId) {
      int i;
      for(i = 0;i<Dataholder.outletList.size();i++) {
          if(Dataholder.outletList.get(i).getId().equals(outletId))
              break;
      }
      outlet = Dataholder.outletList.get(i);

    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }
}