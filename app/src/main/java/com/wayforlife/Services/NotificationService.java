package com.wayforlife.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wayforlife.Activities.HomeActivity;
import com.wayforlife.Activities.LoginActivity;
import com.wayforlife.Models.MyNotification;
import com.wayforlife.Models.User;
import com.wayforlife.R;

import java.util.ArrayList;

public class NotificationService extends FirebaseMessagingService {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        /* There are two types of messages data messages and notification messages. Data messages are handled
        here in onMessageReceived whether the app is in the foreground or background. Data messages are the type traditionally
        used with GCM. Notification messages are only received here in onMessageReceived when the app is in the foreground.
        When the app is in the background an automatically generated notification is displayed. */

        String dataTitle= null,dataDescription= null;


        // Check if message contains a data payload.
        if(remoteMessage.getData().size()>0){
            dataTitle=remoteMessage.getData().get("title");
            dataDescription=remoteMessage.getData().get("description");
        }

        createAndSendNotification(dataTitle, dataDescription);

    }


    private  void createAndSendNotification(String dataTitle,String dataDescription){
        Intent intent=new Intent(this,LoginActivity.class);
        intent.putExtra("isNotification",true);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri  soundUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(dataTitle)
                .setSmallIcon(R.drawable.way_for_life_logo)
                .setContentTitle(dataTitle)
                .setContentIntent(pendingIntent)
                .setSound(soundUri)
                .setShowWhen(true)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);
//                .setLargeIcon(BitmapFactory.decodeResource(getResources().getDrawable(R.drawable.about_us_image)));
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(dataDescription));
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel(notificationManager,notificationBuilder);

        if(notificationManager!=null){
            notificationManager.notify(0,notificationBuilder.build());
        }
    }

    private void createNotificationChannel(NotificationManager notificationManager, NotificationCompat.Builder notificationBuilder) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            if(notificationManager!=null) {
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }
}
