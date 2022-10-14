package com.zingit.user;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Date;


public class OrderUpdateDialog {
    Activity activity;
    AlertDialog dialog;
    Order order;

    public OrderUpdateDialog(Activity activity, Order order) {
        this.activity = activity;
        this.order = order;
    }
    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.order_update_dialog, null);

        ImageView statusIcon = dialogView.findViewById(R.id.status_icon);
        TextView upperText = dialogView.findViewById(R.id.upper_status_text);
        TextView outletNameOrderID = dialogView.findViewById(R.id.outlet_name_id_dialog);
        TextView itemNameQuantity = dialogView.findViewById(R.id.item_name_quantity_dialog);
        TextView timer = dialogView.findViewById(R.id.timer_dialog);
        TextView statusText = dialogView.findViewById(R.id.status_text_dialog);
        ImageView qr = dialogView.findViewById(R.id.QR);
        LinearLayout right = dialogView.findViewById(R.id.right_side_stuff);

        String orderId = order.getOrderID();
        String outletDetails = findOutletName(order.getOutletID())+ " #"+orderId.substring(orderId.length()-5);
        String itemDetails = order.getItemName()+ " x"+ order.getQuantity();
        outletNameOrderID.setText(outletDetails);
        itemNameQuantity.setText(itemDetails);
        Date date = new Date();
        Timestamp nowTime = new Timestamp(date);
        long seconds = order.getZingTime().getSeconds() - nowTime.getSeconds();
        String timerDetails = ""+seconds/60+":"+((seconds%60<10)?"0"+seconds%60:seconds%60);


        switch(order.getStatusCode()) {
            case 0:
                upperText.setText("Order declined");
                statusIcon.setImageResource(R.drawable.alerticon);
                timer.setText("decl.");
                statusText.setText("processing refund");
                break;
            case 2:
                upperText.setText("Order accpeted");
                statusIcon.setImageResource(R.drawable.accepted_icon);
                timer.setText(timerDetails);
                statusText.setText("time remaining");
                break;
            case 3:
                qr.setVisibility(View.VISIBLE);
                qr.setImageBitmap(createBitmap(orderId));
                right.setVisibility(View.GONE);
                upperText.setText("Ready to collect");
                statusIcon.setImageResource(R.drawable.doneicon);
                timer.setText("done");
                statusText.setText("food is ready!");
                break;
        }
        builder.setView(dialogView);
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }
    public void dismissDialog(){
        dialog.dismiss();
    }
    public String findOutletName(String outletID){
        for(int i=0; i<Dataholder.outletList.size(); i++){
            if(Dataholder.outletList.get(i).getId().equals(outletID)){
                return Dataholder.outletList.get(i).getName();
            }
        }
        return "";
    }
    public Bitmap createBitmap(String text){
        BitMatrix result;
        try{
            result = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 108,108, null);
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
