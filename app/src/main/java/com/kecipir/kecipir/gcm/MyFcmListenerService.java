package com.kecipir.kecipir.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kecipir.kecipir.NotificationActivity;
import com.kecipir.kecipir.R;
import com.kecipir.kecipir.SplashScreen;

import java.util.Map;

import io.smooch.core.FcmService;

public class MyFcmListenerService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map data = message.getData();

        Log.d("NOtif", from);
        Log.d("data", data.toString());
//        Log.d("Message", (String) data.get("smoochNotification"));
        String smooch = (String) data.get("smoochNotification");
        if (smooch.equalsIgnoreCase("true")){
            FcmService.triggerSmoochNotification(data, this);
        }
        else {
            Log.d("Message", message.getNotification().getTitle());
            Intent intent;
            if (data.get("type").equals("notification")){
                intent = new Intent(this, NotificationActivity.class);
            }
            else{
                intent = new Intent(this, SplashScreen.class);
            }

            PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle(message.getNotification().getTitle())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message.getNotification().getBody()))
                    .setContentText(message.getNotification().getBody())
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setDefaults(Notification.DEFAULT_ALL);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(1234, builder.build());
        }
    }
}