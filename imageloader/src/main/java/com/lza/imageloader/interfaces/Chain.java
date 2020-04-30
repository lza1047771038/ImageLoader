package com.lza.imageloader.interfaces;

import android.view.View;

import androidx.lifecycle.LifecycleObserver;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public interface Chain extends LifecycleObserver {

    void setNext(Chain chain);

    boolean invoke(Object src, View view, Lock lock, Condition condition,
                   ImageCache mImageCache, SimpleTarget simpleTarget);

    Chain getNext();

}
