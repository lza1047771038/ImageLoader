package com.lza.imageloader.interfaces;

import android.content.Context;
import android.net.NetworkRequest;

import com.lza.imageloader.impl.NetworkCallBack;

public interface NetworkListener {

    void register(Context context, NetworkRequest request, CallBack callBack);

    void unRegister(Context context, CallBack callBack);
}
