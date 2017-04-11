package com.richardlee.moviesoftheyear;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.ref.WeakReference;

public class DeviceStatusHandler {

    public static boolean isOnline(Context context) {

        final WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        ConnectivityManager cm =
                (ConnectivityManager) contextWeakReference.get().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
        /*try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }*/
    }
}
