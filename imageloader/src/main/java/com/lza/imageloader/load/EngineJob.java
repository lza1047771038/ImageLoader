package com.lza.imageloader.load;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.lza.imageloader.impl.lifecycle.LoadingLifecycle;
import com.lza.imageloader.interfaces.Chain;
import com.lza.imageloader.interfaces.ImageCache;
import com.lza.imageloader.interfaces.SimpleTarget;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

class EngineJob extends LoadingLifecycle implements Runnable, LifecycleObserver {
    private Object src;

    private View view;

    private Chain chain;

    /**
     * lock锁，唯一实例不可修改
     */
    private final Lock lock;

    /**
     * lock锁Condition实例，唯一不可修改
     * 网络请求阻塞时依据此对象
     */
    private final Condition condition;

    private WeakReference<LifecycleOwner> lifecycleOwnerWeakReference;

    /**
     * 图片缓存，用户可自定义自己的图片加载
     */
    private ImageCache mImageCache;

    private final ExecutorService mExecutorService;

    /**
     * 图片回调方法 {@link SimpleTarget}
     */
    private SimpleTarget simpleTarget;


    EngineJob(Object src, View view, Chain chain, Lock lock, Condition condition,
              WeakReference<LifecycleOwner> lifecycleOwnerWeakReference,
              ImageCache mImageCache, ExecutorService mExecutorService,
              SimpleTarget simpleTarget) {
        this.src = src;
        this.view = view;
        this.chain = chain;
        this.lock = lock;
        this.condition = condition;
        this.lifecycleOwnerWeakReference = lifecycleOwnerWeakReference;
        this.mImageCache = mImageCache;
        this.mExecutorService = mExecutorService;
        this.simpleTarget = simpleTarget;
    }

    EngineJob(Object src, View view, Chain chain, Lock lock, Condition condition,
              WeakReference<LifecycleOwner> lifecycleOwnerWeakReference,
              ImageCache mImageCache, ExecutorService mExecutorService) {
        this(src, view, chain, lock, condition, lifecycleOwnerWeakReference, mImageCache,
                mExecutorService, null);
    }

    @Override
    public void run() {
        if (lifecycleOwnerWeakReference != null && lifecycleOwnerWeakReference.get() != null) {
            this.lifecycleOwnerWeakReference.get().getLifecycle().addObserver(this);
        }
        if (!chain.invoke(src, view, lock, condition, mImageCache, simpleTarget)) {
            Log.e("PhotoLoader", "unknown source of src field!");
        }
        if (lifecycleOwnerWeakReference != null && lifecycleOwnerWeakReference.get() != null) {
            lifecycleOwnerWeakReference.get().getLifecycle().removeObserver(this);
        }
    }

    @Override
    protected void onStart(@NonNull LifecycleOwner owner) {
        super.onStart(owner);
    }

    @Override
    protected void onPause(@NonNull LifecycleOwner owner) {
        super.onPause(owner);
    }

    @Override
    protected void onDestroy(@NonNull LifecycleOwner owner) {
        super.onDestroy(owner);
        Log.e("tag123", "remove");
        if (mExecutorService instanceof ThreadPoolExecutor) {
            if (!((ThreadPoolExecutor) mExecutorService).remove(this)) {
            }
        }
    }
}
