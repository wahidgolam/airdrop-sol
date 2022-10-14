package com.zingit.user;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    FirebaseFirestore db;

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            try {
                handleNow(remoteMessage);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        try {
            handleNow(remoteMessage);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }
    private void handleNow(RemoteMessage remoteMessage) throws IOException, ExecutionException, InterruptedException {
        Log.d(TAG, "Short lived task is done.");
        Uri imgUrl = null;
        try {
            imgUrl = Objects.requireNonNull(remoteMessage.getNotification()).getImageUrl();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();
        if(imgUrl==null) {
            sendNotification(title, message);
        }
        else {
            sendNotification(title, message, imgUrl);
        }
        //get my title and body and stuff
        //show my notification
    }
    private void sendRegistrationToServer(String token) {
        db = FirebaseFirestore.getInstance();
        db.collection("studentUser").document(Dataholder.studentUser.getUserID()).update(
                "FCMToken", token
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("FCM token", "Logging and Update Complete");
            }
        });
    }
    private void sendNotification(String title, String message) throws IOException {
        Intent intent = new Intent(this, Splashscreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "012";

        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext()
                .getPackageName() + "/" + R.raw.notifsound);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notif)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setVibrate(new long[]{500, 500})
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSound(soundUri)
                        .setFullScreenIntent(pendingIntent, true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setChannelId("012");

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Since android Oreo notification channel is needed.
        createNotificationChannel();

        //pushing notification
        notificationManager.notify(getNextUniqueRandomNumber() /* ID of notification */, notificationBuilder.build());
    }
    private void sendNotification(String title, String message, Uri imgUrl) throws IOException, ExecutionException, InterruptedException {
        Intent intent = new Intent(this, Splashscreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //notification channel
        String channelId = "011";

        //playing sound
        /*final MediaPlayer mp = MediaPlayer.create(this, R.raw.notifsound);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });*/

        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext()
                .getPackageName() + "/" + R.raw.notifsound);

        Bitmap futureTarget = Glide.with(this)
                .asBitmap()
                .load(imgUrl)
                .submit().get();

        //building notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notif)
                        .setLargeIcon(futureTarget)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setVibrate(new long[]{500, 500})
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSound(soundUri)
                        .setChannelId("012")
                        .setFullScreenIntent(pendingIntent, true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Since android Oreo notification channel is needed.
        createNotificationChannel();

        //pushing notification
        notificationManager.notify(getNextUniqueRandomNumber() /* ID of notification */, notificationBuilder.build());
    }
    private static int number = 10000;
    public int getNextUniqueRandomNumber() {
        return number++;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext()
                .getPackageName() + "/" + R.raw.notifsound);

        NotificationManager mNotificationManager = getSystemService(NotificationManager.class);

        List<NotificationChannel> channelList = mNotificationManager.getNotificationChannels();
        for(int i =0; i<channelList.size();i++){
            mNotificationManager.deleteNotificationChannel(channelList.get(i).getId());
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("012", "Zing User", importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            if(soundUri != null){
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                notificationChannel.setSound(soundUri,audioAttributes);
            }

            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
