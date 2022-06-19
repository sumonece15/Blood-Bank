package com.sumon.bloodbank.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.sumon.bloodbank.MainActivity;
import com.sumon.bloodbank.R;

public class NotificationUtil {

    // Notification handler singleton
    private static NotificationUtil nHandler;
    private static NotificationManager mNotificationManager;


    private NotificationUtil () {}


    /**
     * Singleton pattern implementation
     * @return
     */
    public static  NotificationUtil getInstance(Context context) {
        if(nHandler == null) {
            nHandler = new NotificationUtil();
            mNotificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return nHandler;
    }


    /**
     * Shows a simple notification
     * @param context aplication context
     */
    public void createSimpleNotification(Context context, String title, String body) {

        System.out.println(title);

        // Creates an explicit intent for an Activity
        Intent resultIntent = new Intent(context, MainActivity.class);

        // Creating a artifical activity stack for the notification activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        // Pending intent to the notification manager
        PendingIntent resultPending = stackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Building the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_ic_notification) // notification icon
                .setContentTitle(title) // main title of the notification
                .setContentText(body) // notification text
                .setContentIntent(resultPending); // notification intent

        createNotificationChannel();

        // mId allows you to update the notification later on.
        mNotificationManager.notify(1000, mBuilder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "BloodBank";
            String description = "BloodBank App";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("BloodBank", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            mNotificationManager.createNotificationChannel(channel);
        }
    }
}
