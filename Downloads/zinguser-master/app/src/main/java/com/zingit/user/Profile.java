package com.zingit.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import club.cred.neopop.PopFrameLayout;

public class Profile extends AppCompatActivity {
    FirebaseAuth mAuth;
    PopFrameLayout tnc,privacy,reportBug,contact,unzing,take_our_survey;
    ImageView back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        tnc = findViewById(R.id.terms_and_condition);
        privacy = findViewById(R.id.privacy_policy);
        reportBug =findViewById(R.id.report_a_bug);
        contact = findViewById(R.id.contact_us);
        unzing = findViewById(R.id.Unzing);
        take_our_survey = findViewById(R.id.take_our_survey);
        back_btn = findViewById(R.id.back_btn);

        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTerms(view);
            }
        });
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPrivacy(view);
            }
        });
        reportBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReport(view);
            }
        });
        take_our_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSurvey(view);
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFeatureSuggest(view);
            }
        });
        unzing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut(view);
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack(view);
            }
        });



    }
    public void signOut(View view){
        mAuth.signOut();
        Toast.makeText(this, "Sad to see you leave :(", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    public void goBack(View view){
        super.onBackPressed();
    }
    public void openTerms(View view){
        Intent httpIntent = new Intent(Intent.ACTION_VIEW);
        httpIntent.setData(Uri.parse("https://zingnow.in/toc.html"));

        startActivity(httpIntent);
    }
    public void openPrivacy(View view){
        Intent httpIntent = new Intent(Intent.ACTION_VIEW);
        httpIntent.setData(Uri.parse("https://zingnow.in/privacy.html"));

        startActivity(httpIntent);
    }
    public void openReport(View view){
        Intent httpIntent = new Intent(Intent.ACTION_VIEW);
        httpIntent.setData(Uri.parse("https://forms.gle/PhnYfjhCuYzuD8Da6"));
        startActivity(httpIntent);
    }
    public void openSurvey(View view){
        Intent httpIntent = new Intent(Intent.ACTION_VIEW);
        httpIntent.setData(Uri.parse("https://forms.gle/YKB43q1EyXSNnFXj7"));

        startActivity(httpIntent);
    }
    public void openFeatureSuggest(View view){
        String contact = "+91 8637085750"; // use country code with your phone number
        String url = "https://api.whatsapp.com/send?phone=" + contact;
        try {
            PackageManager pm = this.getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}