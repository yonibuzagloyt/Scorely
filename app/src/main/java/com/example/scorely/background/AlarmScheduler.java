package com.example.scorely.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.example.scorely.util.Constants;

/** קביעה וביטול של תזכורות יום יציאה באמצעות AlarmManager. */
public class AlarmScheduler {

    private static PendingIntent buildIntent(Context ctx, String itemId, String title) {
        Intent i = new Intent(ctx, ReleaseAlarmReceiver.class);
        i.putExtra(Constants.EXTRA_ITEM_ID, itemId);
        i.putExtra(Constants.EXTRA_ITEM_TITLE, title);
        return PendingIntent.getBroadcast(ctx, itemId.hashCode(), i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    public static void schedule(Context ctx, String itemId, String title, long whenMillis) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = buildIntent(ctx, itemId, title);
        // אנדרואיד 12+: התראה מדויקת דורשת הרשאה מיוחדת. בלעדיה - התראה לא מדויקת.
        boolean canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms();
        if (canExact) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, whenMillis, pi);
        else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, whenMillis, pi);
    }

    public static void cancel(Context ctx, String itemId) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.cancel(buildIntent(ctx, itemId, ""));
    }
}
