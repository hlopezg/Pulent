package com.example.pulent;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.pulent.utils.Constants;

public class CustomApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            registerConnectivityNetworkMonitorForAPI21AndUp();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void registerConnectivityNetworkMonitorForAPI21AndUp() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        if (connectivityManager != null) {
            connectivityManager.registerNetworkCallback(
                    builder.build(),
                    new ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(Network network) {
                            LocalBroadcastManager.getInstance(CustomApplication.this).sendBroadcast(getConnectivityIntent(true));
                        }

                        @Override
                        public void onLost(Network network) {
                            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                            if(networkInfo == null || !networkInfo.isConnected())
                                LocalBroadcastManager.getInstance(CustomApplication.this).sendBroadcast(getConnectivityIntent(false));
                        }
                    }
            );
        }
    }

    private Intent getConnectivityIntent(boolean isConnected) {
        Intent intent = new Intent("MyData");
        intent.putExtra("type", Constants.TYPE_NETWORK_CONECTION);
        intent.putExtra("connection", isConnected);

        return intent;
    }
}
