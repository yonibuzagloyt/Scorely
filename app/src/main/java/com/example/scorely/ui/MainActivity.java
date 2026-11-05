package com.example.scorely.ui;

import android.Manifest;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.scorely.R;
import com.example.scorely.background.NetworkReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** המסך הראשי: Bottom Navigation שמחליף Fragments, באנר "אין חיבור" והרשאת התראות. */
public class MainActivity extends AppCompatActivity {

    private NetworkReceiver networkReceiver;
    private TextView offlineBanner;

    private final ActivityResultLauncher<String> notificationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> { });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        offlineBanner = findViewById(R.id.offlineBanner);

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            Fragment f;
            int id = item.getItemId();
            if (id == R.id.nav_search) f = new SearchFragment();
            else if (id == R.id.nav_lists) f = new MyListsFragment();
            else if (id == R.id.nav_profile) f = new ProfileFragment();
            else f = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, f).commit();
            return true;
        });
        if (savedInstanceState == null) nav.setSelectedItemId(R.id.nav_home);

        if (Build.VERSION.SDK_INT >= 33) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        networkReceiver = new NetworkReceiver(connected ->
                offlineBanner.setVisibility(connected ? View.GONE : View.VISIBLE));
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        offlineBanner.setVisibility(NetworkReceiver.isConnected(this) ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkReceiver);
    }
}
