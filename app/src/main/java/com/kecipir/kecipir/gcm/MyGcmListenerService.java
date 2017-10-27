package com.kecipir.kecipir.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.kecipir.kecipir.R;
import com.kecipir.kecipir.SplashScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.smooch.core.FcmService
        ;

public class MyGcmListenerService extends GcmListenerService {

    String message;
    String title;
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String m = data.getString("m");
        Log.d("RESPONSE GCM :", data.toString());
        if (m != null) {
        Log.d("RESPONSE GCM :", m);
            try {
                JSONObject jsonObject = new JSONObject(m);
                message = jsonObject.getString("message");
                title = jsonObject.getString("title");
                Intent intent = new Intent(this, SplashScreen.class);

                PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder.setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                ;


                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(1234, builder.build());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            final String smoochNotification = data.getString("smoochNotification");
//            Serializable result = dataBundle.getSerializable("data");
//            HashMap<String,String> output = (HashMap<String, String>) result;
//            Map<String, Objects> map = new HashMap<>();
//            map.put("smoochNotification", data.getString("smoochNotification"));
//            if (smoochNotification != null && smoochNotification.equals("true")) {
//                FcmService.triggerSmoochNotification(map, this);
//            }
        }

    }

}