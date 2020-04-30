package com.lza.imageloader.impl.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

public class LoadingLifecycle implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onStart(@NonNull LifecycleOwner owner) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onPause(@NonNull LifecycleOwner owner) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy(@NonNull LifecycleOwner owner) {
        owner.getLifecycle().removeObserver(this);
    }
}
