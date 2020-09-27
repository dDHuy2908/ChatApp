package com.ddhuy4298.chatapp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ddhuy4298.chatapp.R;
import com.ddhuy4298.chatapp.activities.ChatActivity;
import com.ddhuy4298.chatapp.activities.MainActivity;
import com.ddhuy4298.chatapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService {

    private String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        ComponentName componentName = new ComponentName(this, FCMService.class);
        getPackageManager().setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentReceiver = preferences.getString("currentReceiver", "none");

        String receiver = remoteMessage.getData().get("receiver");
        String sender = remoteMessage.getData().get("sender");
        if (receiver.equals(currentUserId) && !currentReceiver.equals(sender)) {
            showNotification(remoteMessage);
        }
    }

    private void showNotification(RemoteMessage remoteMessage) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = "ChatApp";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelId,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);
        }

        String title = remoteMessage.getData().get("title");
        String text = remoteMessage.getData().get("body");
        String sender = remoteMessage.getData().get("sender");

        NotificationCompat.Builder notification = new NotificationCompat.Builder(
                this,
                channelId
        );
        notification.setContentTitle(title);
        notification.setContentText(text);
        notification.setSmallIcon(R.mipmap.ic_launcher_chatapp);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.setSound(notificationSound);
//        int j = Integer.parseInt(sender.replaceAll("[\\D]", ""));

        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userId", sender);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                0
        );
        notification.setContentIntent(pendingIntent);

        manager.notify(429899, notification.build());
    }
}
