package com.wormoscorp.ieaapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    NotificationManager mNotificationManager;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.setLooping(false);
        }

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300, 300, 300};
        v.vibrate(pattern, -1);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "ieaapp");

        Intent resultIntent;

        if(Objects.requireNonNull(remoteMessage.getData().get("activity")).matches("userChatSession")){
            resultIntent = new Intent(this,ChatSession.class).putExtra("chatKey",remoteMessage.getData().get("chatKey"))
                    .putExtra("ownerEmail",remoteMessage.getData().get("ownerKey"));

        } else if(Objects.requireNonNull(remoteMessage.getData().get("activity")).matches("eventChatSession")){
            resultIntent = new Intent(this,EventChatSession.class).putExtra("chatKey",remoteMessage.getData().get("chatKey"))
                    .putExtra("eventItemKey",remoteMessage.getData().get("ownerKey"))
                    .putExtra("eventType",remoteMessage.getData().get("eventType"));

        }else if(Objects.requireNonNull(remoteMessage.getData().get("activity")).matches("grievance")){
            resultIntent = new Intent(this,MyGrievances.class);

        } else{
            resultIntent = new Intent(this, MembersNotification.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "ieaapp";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setSmallIcon(R.drawable.iea_logo);
            builder.setChannelId(channelId);
            builder.setContentIntent(pendingIntent);
            builder.setContentTitle(Objects.requireNonNull(remoteMessage.getNotification()).getTitle());
            builder.setContentText(remoteMessage.getNotification().getBody());
            builder.setAutoCancel(true);
            builder.setPriority(Notification.PRIORITY_MAX);
        } else{
            builder.setContentTitle(Objects.requireNonNull(remoteMessage.getNotification()).getTitle());
            builder.setContentText(remoteMessage.getNotification().getBody());
            builder.setChannelId("ieaapp");
            builder.setSmallIcon(R.drawable.iea_logo);
            builder.setAutoCancel(true);
            builder.setPriority(Notification.PRIORITY_MAX);
            builder.setContentIntent(pendingIntent);

        }

        mNotificationManager.notify(0, builder.build());
    }

}


