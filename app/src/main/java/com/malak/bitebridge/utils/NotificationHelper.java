package com.malak.bitebridge.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.malak.bitebridge.R;
import com.malak.bitebridge.activities.HomeActivity;

public class NotificationHelper {

    private static final String CHANNEL_ID = "bitebridge_orders";
    private static final String CHANNEL_NAME = "Order Updates";
    private static final String CHANNEL_DESC =
            "Notifications for order status updates";
    private static final int NOTIFICATION_ID = 1001;

    private Context context;
    private NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        notificationManager = (NotificationManager)
                context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            channel.enableVibration(true);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendOrderPlacedNotification(long orderId,
                                            double total) {
        // Tapping notification opens HomeActivity
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Order Placed! 🎉")
                        .setContentText("Order #" + orderId +
                                " confirmed — $" +
                                String.format("%.2f", total))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Your order #" + orderId +
                                        " has been placed successfully!\n" +
                                        "Total: $" +
                                        String.format("%.2f", total) +
                                        "\nWe're preparing your food! 🍕"))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{0, 500, 200, 500});

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void sendOrderStatusNotification(long orderId,
                                            String status) {
        String emoji = status.equals("Preparing") ? "👨‍🍳" :
                status.equals("Ready") ? "✅" :
                        status.equals("Delivered") ? "🚗" : "📋";

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle(emoji + " Order Update")
                        .setContentText("Order #" + orderId +
                                " is now: " + status)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        notificationManager.notify(
                (int) orderId, builder.build());
    }
}