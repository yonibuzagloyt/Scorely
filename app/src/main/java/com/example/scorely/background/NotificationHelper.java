package com.example.scorely.background;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import com.example.scorely.ui.ItemDetailsActivity;
import com.example.scorely.util.Constants;

/** יצירת ערוץ התראות והצגת התראת יום יציאה. */
public class NotificationHelper {

    public static void createChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(Constants.CHANNEL_RELEASES,
                    "Release reminders", NotificationManager.IMPORTANCE_DEFAULT);
            ctx.getSystemService(NotificationManager.class).createNotificationChannel(ch);
        }
    }

    public static void showReleaseNotification(Context ctx, String itemId, String title) {
        // אנדרואיד 13+: נדרשת הרשאת זמן-ריצה POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;

        Intent open = new Intent(ctx, ItemDetailsActivity.class);
        open.putExtra(Constants.EXTRA_ITEM_ID, itemId);
        PendingIntent pi = PendingIntent.getActivity(ctx, itemId.hashCode(), open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, Constants.CHANNEL_RELEASES)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText("יוצא היום! זה הזמן לפתוח את ה-Backlog")
                .setContentIntent(pi)
                .setAutoCancel(true);
        NotificationManagerCompat.from(ctx).notify(itemId.hashCode(), b.build());
    }
}
