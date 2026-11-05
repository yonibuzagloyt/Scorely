package com.example.scorely.background;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.scorely.ScorelyApp;
import com.example.scorely.util.Constants;
import com.example.scorely.util.ResultCallback;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** משימת רקע תקופתית: מעלה ל-Firestore ביקורות שנכתבו במצב לא מקוון. */
public class SyncWorker extends Worker {

    public SyncWorker(@NonNull Context ctx, @NonNull WorkerParameters params) { super(ctx, params); }

    @NonNull
    @Override
    public Result doWork() {
        ScorelyApp app = (ScorelyApp) getApplicationContext();
        CountDownLatch latch = new CountDownLatch(1);
        app.getRepository().syncPendingReviews(new ResultCallback<Integer>() {
            @Override public void onSuccess(Integer n) { latch.countDown(); }
            @Override public void onError(Exception e) { latch.countDown(); }
        });
        try {
            return latch.await(30, TimeUnit.SECONDS) ? Result.success() : Result.retry();
        } catch (InterruptedException e) {
            return Result.retry();
        }
    }

    /** קובע ריצה כל 6 שעות, רק כשיש רשת וסוללה לא חלשה. */
    public static void schedule(Context ctx) {
        Constraints c = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true).build();
        PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(SyncWorker.class, 6, TimeUnit.HOURS)
                .setConstraints(c).build();
        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                Constants.SYNC_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, req);
    }
}
