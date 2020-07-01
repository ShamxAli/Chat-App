package com.startup.chatapp.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.startup.chatapp.R;
import com.startup.chatapp.chat.ChatActivity;


public class MyFCMService extends FirebaseMessagingService {

    public static final String TAG = "MyFCMService";

    // Constructor ...
    public MyFCMService() {
    }


    /* ==================== RemoteMessage Method ... ==================== */


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Create notification channel if API > Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        /* --- Logic for receiving notification in different scenarios*/

        // if user is in the chat activity
        if (ChatActivity.flag) {
            if (remoteMessage.getData().get("number").equals(ChatActivity.user2_number)) {

            } else {
                displayNotification(getApplicationContext(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            }
        }
        // if user is outside the chat activity
        else {
            displayNotification(getApplicationContext(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }


    /* ==================== Notification Channel ==================== */
    private String CHANNEL_NAME = "High Priority Channel";
    private String CHANNEL_ID = "com.startup.notificationexample" + CHANNEL_NAME;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {

        NotificationChannel notificationChannel =
                new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("This is description");
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        // Create the channel
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }


    /* ==================== Display Notification ==================== */

    public void displayNotification(Context context, String title, String body) {
        Log.d("notifyme", "displayNotification: " + title + "   " + body);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentText(body)
                .setAutoCancel(false)
                .setOngoing(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, mBuilder.build());
    }

}
