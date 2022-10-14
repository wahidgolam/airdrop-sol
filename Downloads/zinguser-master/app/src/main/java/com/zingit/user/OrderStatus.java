package com.zingit.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class OrderStatus extends AppCompatActivity {

    TextView orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        orderID = findViewById(R.id.orderID);
        Intent intent = getIntent();
        String id = intent.getStringExtra("orderID");
        orderID.setText(id);
    }
}