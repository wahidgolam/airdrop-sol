package com.zingit.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zingit.user.model.CampusChooser;

public class Onboarding1 extends AppCompatActivity {
    public LinearLayout onboardImage;
    public TextView onboardHeading;
    public TextView onboardSubHeading;
    public ImageView nextButton;
    public Button backButton;
    RelativeLayout background_color;
    public TextView skip;
    public int onboardIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding1);
        onboardImage = findViewById(R.id.background_image);
        onboardHeading = findViewById(R.id.text1);
        onboardSubHeading = findViewById(R.id.text2);
        nextButton = findViewById(R.id.next);
        background_color = findViewById(R.id.background_color);

        skip = findViewById(R.id.skip);


        skip.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getApplicationContext(), ChooseCampus.class);
                                        startActivity(intent);
                                    }
                                });
               // viewBackButton();
        Dataholder.newUser = true;
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPressed(view);
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.onboard1));
        }
    }
    /*public void viewBackButton(){
        if(onboardIndex == 0){
            backButton.setVisibility(View.INVISIBLE);
        }
        else{
            backButton.setVisibility(View.VISIBLE);
        }
    }*/

    public void backPressed(View view){
        onboardIndex--;
        Log.i("Index Val", onboardIndex+"");
        updateContent();
        //viewBackButton();
    }

    public void nextPressed(View view){
        if(onboardIndex==2){
            //navigate to login
            Intent intent = new Intent(this, ChooseCampus.class);
            startActivity(intent);
        }
        else {
            onboardIndex++;
            Log.i("Index Val", onboardIndex+"");
            updateContent();
            //viewBackButton();
        }
    }

    public void updateContent(){
        switch (onboardIndex){
            case 0:
                Log.i("Case 0", "Reached");
                onboardImage.setBackgroundResource(R.drawable.onboarding1);
                onboardHeading.setText("Preorder food right when\n   you leave your room");
                onboardSubHeading.setText("Order from the best restaurants in your\n   campus, without standing in a line");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    Window window = this.getWindow();
                    window.setStatusBarColor(this.getResources().getColor(R.color.onboard1));
                }
                break;
            case 1:
                Log.i("Case 1", "Reached");
                onboardImage.setBackgroundResource(R.drawable.onboarding2);
                background_color.setBackgroundResource(R.color.onboard2);
                onboardHeading.setText("Reach there before your\n       Zing time expires");
                onboardSubHeading.setText("Your order will be ready at the Zing\ncounter, so no waiting for good food");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    Window window = this.getWindow();
                    window.setStatusBarColor(this.getResources().getColor(R.color.onboard2));
                }
                break;
            case 2:
                Log.i("Case 2", "Reached");
                onboardImage.setBackgroundResource(R.drawable.onboarding3);
                background_color.setBackgroundResource(R.color.onboard3);
                onboardHeading.setText("Collect your order by\n     showing the QR");
                onboardSubHeading.setText("Easy order. Easy food\n         Pro moves");
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    Window window = this.getWindow();
                    window.setStatusBarColor(this.getResources().getColor(R.color.onboard3));
                }
                break;
            default:
        }
    }
}