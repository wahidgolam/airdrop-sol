package com.zingit.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RatingScreen extends AppCompatActivity {
    TextView group1text1;
    TextView group1text2;
    TextView group1text3;
    TextView group1text4;
    TextView group1text5;

    TextView group2text1;
    TextView group2text2;
    TextView group2text3;
    TextView group2text4;
    TextView group2text5;

    TextView group3text1;
    TextView group3text2;
    TextView group3text3;
    TextView group3text4;
    TextView group3text5;

    MaterialCardView group1card1;
    MaterialCardView group1card2;
    MaterialCardView group1card3;
    MaterialCardView group1card4;
    MaterialCardView group1card5;

    MaterialCardView group2card1;
    MaterialCardView group2card2;
    MaterialCardView group2card3;
    MaterialCardView group2card4;
    MaterialCardView group2card5;

    MaterialCardView group3card1;
    MaterialCardView group3card2;
    MaterialCardView group3card3;
    MaterialCardView group3card4;
    MaterialCardView group3card5;

    int score1 = 4;
    int score2 = 4;
    int score3 =5;

    FirebaseFirestore db;
    String orderID;
    int type;
    //specifies screen termination type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_screen);
        group1text1 = findViewById(R.id.group1text1);
        group1text2 = findViewById(R.id.group1text2);
        group1text3 = findViewById(R.id.group1text3);
        group1text4 = findViewById(R.id.group1text4);
        group1text5 = findViewById(R.id.group1text5);

        group2text1 = findViewById(R.id.group2text1);
        group2text2 = findViewById(R.id.group2text2);
        group2text3 = findViewById(R.id.group2text3);
        group2text4 = findViewById(R.id.group2text4);
        group2text5 = findViewById(R.id.group2text5);

        group3text1 = findViewById(R.id.group3text1);
        group3text2 = findViewById(R.id.group3text2);
        group3text3 = findViewById(R.id.group3text3);
        group3text4 = findViewById(R.id.group3text4);
        group3text5 = findViewById(R.id.group3text5);

        group1card1 = findViewById(R.id.group1card1);
        group1card2 = findViewById(R.id.group1card2);
        group1card3 = findViewById(R.id.group1card3);
        group1card4 = findViewById(R.id.group1card4);
        group1card5 = findViewById(R.id.group1card5);

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        orderID = intent.getStringExtra("orderID");
        type = intent.getIntExtra("type", 1);

        group1card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group1text1, group1card1);
                //change other's style
                defaultStyle(group1text2, group1card2);
                defaultStyle(group1text3, group1card3);
                defaultStyle(group1text4, group1card4);
                defaultStyle(group1text5, group1card5);
                //change status
                score1 = 1;
            }
        });
        group1card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group1text2, group1card2);
                //change other's style
                defaultStyle(group1text1, group1card1);
                defaultStyle(group1text3, group1card3);
                defaultStyle(group1text4, group1card4);
                defaultStyle(group1text5, group1card5);
                //change status
                score1 = 2;
            }
        });
        group1card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group1text3, group1card3);
                //change other's style
                defaultStyle(group1text2, group1card2);
                defaultStyle(group1text1, group1card1);
                defaultStyle(group1text4, group1card4);
                defaultStyle(group1text5, group1card5);
                //change status
                score1 = 3;
            }
        });
        group1card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group1text4, group1card4);
                //change other's style
                defaultStyle(group1text2, group1card2);
                defaultStyle(group1text3, group1card3);
                defaultStyle(group1text1, group1card1);
                defaultStyle(group1text5, group1card5);
                //change status
                score1 = 4;
            }
        });
        group1card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group1text5, group1card5);
                //change other's style
                defaultStyle(group1text2, group1card2);
                defaultStyle(group1text3, group1card3);
                defaultStyle(group1text4, group1card4);
                defaultStyle(group1text1, group1card1);
                //change status
                score1 = 5;
            }
        });

        group2card1 = findViewById(R.id.group2card1);
        group2card2 = findViewById(R.id.group2card2);
        group2card3 = findViewById(R.id.group2card3);
        group2card4 = findViewById(R.id.group2card4);
        group2card5 = findViewById(R.id.group2card5);

        group2card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group2text1, group2card1);
                //change other's style
                defaultStyle(group2text2, group2card2);
                defaultStyle(group2text3, group2card3);
                defaultStyle(group2text4, group2card4);
                defaultStyle(group2text5, group2card5);
                //change status
                score2 = 1;
            }
        });

        group2card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group2text2, group2card2);
                //change other's style
                defaultStyle(group2text1, group2card1);
                defaultStyle(group2text3, group2card3);
                defaultStyle(group2text4, group2card4);
                defaultStyle(group2text5, group2card5);
                //change status
                score2 = 2;
            }
        });
        group2card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group2text3, group2card3);
                //change other's style
                defaultStyle(group2text2, group2card2);
                defaultStyle(group2text1, group2card1);
                defaultStyle(group2text4, group2card4);
                defaultStyle(group2text5, group2card5);
                //change status
                score2 = 3;
            }
        });
        group2card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group2text4, group2card4);
                //change other's style
                defaultStyle(group2text2, group2card2);
                defaultStyle(group2text3, group2card3);
                defaultStyle(group2text1, group2card1);
                defaultStyle(group2text5, group2card5);
                //change status
                score2 = 4;
            }
        });
        group2card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group2text5, group2card5);
                //change other's style
                defaultStyle(group2text2, group2card2);
                defaultStyle(group2text3, group2card3);
                defaultStyle(group2text4, group2card4);
                defaultStyle(group2text1, group2card1);
                //change status
                score2 = 5;
            }
        });

        group3card1 = findViewById(R.id.group3card1);
        group3card2 = findViewById(R.id.group3card2);
        group3card3 = findViewById(R.id.group3card3);
        group3card4 = findViewById(R.id.group3card4);
        group3card5 = findViewById(R.id.group3card5);

        group3card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group3text1, group3card1);
                //change other's style
                defaultStyle(group3text2, group3card2);
                defaultStyle(group3text3, group3card3);
                defaultStyle(group3text4, group3card4);
                defaultStyle(group3text5, group3card5);
                //change status
                score3 = 1;
            }
        });
        group3card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group3text2, group3card2);
                //change other's style
                defaultStyle(group3text1, group3card1);
                defaultStyle(group3text3, group3card3);
                defaultStyle(group3text4, group3card4);
                defaultStyle(group3text5, group3card5);
                //change status
                score3 = 2;
            }
        });
        group3card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group3text3, group3card3);
                //change other's style
                defaultStyle(group3text2, group3card2);
                defaultStyle(group3text1, group3card1);
                defaultStyle(group3text4, group3card4);
                defaultStyle(group3text5, group3card5);
                //change status
                score3 = 3;
            }
        });
        group3card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group3text4, group3card4);
                //change other's style
                defaultStyle(group3text2, group3card2);
                defaultStyle(group3text3, group3card3);
                defaultStyle(group3text1, group3card1);
                defaultStyle(group3text5, group3card5);
                //change status
                score3 = 4;
            }
        });
        group3card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group3text5, group3card5);
                //change other's style
                defaultStyle(group3text2, group3card2);
                defaultStyle(group3text3, group3card3);
                defaultStyle(group3text4, group3card4);
                defaultStyle(group3text1, group3card1);
                //change status
                score3 = 5;
            }
        });
    }
    public void changeStyle(TextView text, MaterialCardView card){
        card.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_orange));
        card.setStrokeWidth(0);
        text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
    }
    public void defaultStyle(TextView text, MaterialCardView card){
        card.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        card.setStrokeWidth(2);
        text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
    }
    public void cancel(View view){
        if(type==0) {
            Intent intent2 = new Intent(getApplicationContext(), Homescreen.class);
            startActivity(intent2);
            finish();
        }
        else{
            finish();
        }
    }
    public void updateRating(View view){
        LoadingDialog loadingDialog = new LoadingDialog(RatingScreen.this, "Updating your rating");
        loadingDialog.startLoadingDialog();
        Rating rating = new Rating(orderID, score1,score2,score3);
        db.collection("rating").document().set(rating);
        DocumentReference documentReference2 = db.collection("order").document(orderID);
        documentReference2.update("rating", rating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(type==0) {
                    Intent intent2 = new Intent(getApplicationContext(), Homescreen.class);
                    loadingDialog.dismissDialog();
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent2);
                    finish();
                }
                else{
                    finish();
                }
            }
        });
    }
}