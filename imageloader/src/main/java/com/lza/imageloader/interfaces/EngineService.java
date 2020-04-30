package com.lza.imageloader.interfaces;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import java.lang.ref.WeakReference;

public interface EngineService {
    void addImageChain(Chain chain);

    void dispatchImageLoad(Object src,
                           @NonNull final View container,
                           @Nullable final WeakReference<LifecycleOwner> lifecycle);

    void dispatchImageLoad(final Object src,
                           @Nullable final WeakReference<LifecycleOwner> lifecycle,
                           @NonNull final SimpleTarget simpleTarget);

    void execute(@NonNull Runnable runnable);

    void release();
}
