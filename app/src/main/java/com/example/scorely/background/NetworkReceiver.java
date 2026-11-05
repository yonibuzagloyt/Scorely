package com.example.scorely.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

/** אירוע מערכת: שינוי בחיבור הרשת. נרשם דינמית ב-MainActivity ומדווח ל-Listener. */
public class NetworkReceiver extends BroadcastReceiver {

    public interface Listener { void onNetworkChanged(boolean connected); }

    private final Listener listener;

    public NetworkReceiver(Listener listener) { this.listener = listener; }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        listener.onNetworkChanged(isConnected(ctx));
    }

    public static boolean isConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities caps = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}
