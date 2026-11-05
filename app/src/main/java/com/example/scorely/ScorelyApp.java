package com.example.scorely;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.scorely.background.NotificationHelper;
import com.example.scorely.background.SyncWorker;
import com.example.scorely.data.repository.MediaRepository;
import com.example.scorely.data.repository.MediaRepositoryImpl;
import com.example.scorely.util.PrefsManager;

/** מחלקת האפליקציה: מאתחלת פעם אחת את המאגר, ערוץ ההתראות, הסנכרון וערכת הנושא. */
public class ScorelyApp extends Application {

    private MediaRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new MediaRepositoryImpl(this);
        NotificationHelper.createChannel(this);
        SyncWorker.schedule(this);
        boolean dark = new PrefsManager(this).isDarkMode();
        AppCompatDelegate.setDefaultNightMode(dark
                ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    public MediaRepository getRepository() { return repository; }
}
