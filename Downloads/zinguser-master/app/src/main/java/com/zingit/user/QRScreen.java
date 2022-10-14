package com.zingit.user;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscreen);
        Intent intent = getIntent();
        String text = intent.getStringExtra("text");
        ImageView qrimg = findViewById(R.id.qr_img);
        qrimg.setImageBitmap(createBitmap(text));
    }
    public void goHome(View view){
        Intent intent = new Intent(this, Homescreen.class);
        startActivity(intent);
        finish();
    }
    public Bitmap createBitmap(String text){
        BitMatrix result;
        try{
            result = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 300,300, null);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        int height = result.getHeight();
        int width = result.getWidth();
        int[] pixels = new int[width*height];
        for(int x=0;x<height;x++) {
            int offset = x * width;
            for (int k = 0; k < width; k++) {
                pixels[offset + k] = result.get(k, x) ? BLACK : WHITE;

            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0 , width, 0, 0, width, height);
        return bitmap;
    }
}