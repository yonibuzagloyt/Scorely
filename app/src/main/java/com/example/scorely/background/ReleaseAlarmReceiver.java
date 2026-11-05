package com.example.scorely.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.scorely.util.Constants;

/** מקבל את אירוע ה-AlarmManager ומציג התראה. */
public class ReleaseAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        String id = intent.getStringExtra(Constants.EXTRA_ITEM_ID);
        String title = intent.getStringExtra(Constants.EXTRA_ITEM_TITLE);
        if (id != null) NotificationHelper.showReleaseNotification(ctx, id, title == null ? "" : title);
    }
}
