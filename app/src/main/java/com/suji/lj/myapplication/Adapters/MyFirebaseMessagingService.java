package com.suji.lj.myapplication.Adapters;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.suji.lj.myapplication.MainActivity;
import com.suji.lj.myapplication.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        Log.d("뉴토큰",token);
        sendRegistrationToServer(token);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d("여기왜안돼from", "Message Notification Body: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.

            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }
        Log.d("여기왜안돼data", "Message Notification Body: " + remoteMessage.getData());
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("여기왜안돼body", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
        }




    }

    private void sendNotification(String body,String title) {

        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =

                new NotificationCompat.Builder(this,channelId)

                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0,notificationBuilder.build());




    }
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }
}
