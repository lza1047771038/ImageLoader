package com.lza.imageloader.impl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;

import com.lza.imageloader.interfaces.CallBack;
import com.lza.imageloader.interfaces.NetworkListener;

public class NetworkStateListener implements NetworkListener {
    private Context context;

    @Override
    public void register(Context context, NetworkRequest request, CallBack callBack) {
        if (this.context == null)
            this.context = context.getApplicationContext();
        ConnectivityManager cm =
                (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        cm.registerNetworkCallback(request, (NetworkCallBack) callBack);
    }

    @Override
    public void unRegister(Context context, CallBack callBack) {
        if (this.context == null)
            this.context = context.getApplicationContext();
        ConnectivityManager cm =
                (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        cm.unregisterNetworkCallback((NetworkCallBack) callBack);
    }
}
