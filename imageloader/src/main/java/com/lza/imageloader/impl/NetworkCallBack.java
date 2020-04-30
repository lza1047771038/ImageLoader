package com.lza.imageloader.impl;

import android.net.ConnectivityManager;
import android.net.Network;

import androidx.annotation.NonNull;

import com.lza.imageloader.interfaces.CallBack;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class NetworkCallBack extends ConnectivityManager.NetworkCallback implements CallBack {
    private Condition requestCondition;
    private Lock mLock;

    public void setRequestCondition(Lock mLock, Condition condition) {
        this.mLock = mLock;
        this.requestCondition = condition;
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        onResume();
    }

    @Override
    public void onLosing(@NonNull Network network, int maxMsToLive) {
        super.onLosing(network, maxMsToLive);
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
    }

    @Override
    public void onUnavailable() {
        super.onUnavailable();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {
        mLock.lock();
        if (requestCondition != null) {
            requestCondition.signalAll();
        }
        mLock.unlock();
    }
}
