package com.example.scorely.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.scorely.data.local.AppDatabase;
import com.example.scorely.data.local.ListEntryEntity;
import java.util.List;
import java.util.concurrent.Executors;

/** אחרי אתחול המכשיר AlarmManager מאבד את התזכורות - כאן הן נקבעות מחדש מתוך Room. */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;
        PendingResult pending = goAsync();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                AppDatabase db = AppDatabase.get(ctx);
                List<ListEntryEntity> list = db.listEntryDao().upcomingReminders(System.currentTimeMillis());
                for (ListEntryEntity e : list) {
                    String title = db.itemDao().getTitle(e.itemId);
                    AlarmScheduler.schedule(ctx, e.itemId, title == null ? "" : title, e.reminderAt);
                }
            } finally {
                pending.finish();
            }
        });
    }
}
