package com.zingit.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Instructions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
    }
    public void backPressed(View view){
        super.onBackPressed();
    }
    public void nextPressed(View view){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}