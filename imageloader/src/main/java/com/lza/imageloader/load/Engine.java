package com.lza.imageloader.load;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.lza.imageloader.impl.NetworkCallBack;
import com.lza.imageloader.impl.NetworkStateListener;
import com.lza.imageloader.impl.cacheimpl.MixCache;
import com.lza.imageloader.impl.chainimpl.BitmapChain;
import com.lza.imageloader.impl.chainimpl.DrawableChain;
import com.lza.imageloader.impl.chainimpl.IntegerChain;
import com.lza.imageloader.impl.chainimpl.UrlChain;
import com.lza.imageloader.interfaces.CallBack;
import com.lza.imageloader.interfaces.Chain;
import com.lza.imageloader.interfaces.EngineService;
import com.lza.imageloader.interfaces.ErrorCallBack;
import com.lza.imageloader.interfaces.ImageCache;
import com.lza.imageloader.interfaces.NetworkListener;
import com.lza.imageloader.interfaces.SimpleTarget;

import java.lang.ref.WeakReference;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Engine implements EngineService, ErrorCallBack {
    /**
     * 网络异步加载线程池
     */
    private ThreadPoolExecutor mExecutorService;
    /**
     * 异步对象处理
     */
    private Handler mHandler;
    /**
     * 图片缓存
     */
    private volatile ImageCache mImageCache;
    /**
     * 网络状态改变监听
     * 用来处理线程的唤醒
     */
    private volatile NetworkListener networkListener;
    private Lock mLock;
    private CallBack mCallBack;

    private volatile Condition mLoadCondition;
    private volatile Chain chain;

    Engine() {
        this(new ThreadPoolExecutor(8, 12, 10000, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>()), new Handler(Looper.getMainLooper()),
                new MixCache(PhotoLoader.getApplicationContext()), new NetworkStateListener(),
                new ReentrantLock(),
                new NetworkCallBack(), null, null);
    }

    private Engine(ThreadPoolExecutor mExecutorService, Handler mHandler, ImageCache mImageCache,
                   NetworkListener networkListener, Lock mLock, CallBack mCallBack,
                   Condition mLoadCondition, Chain chain) {
        this.mExecutorService = mExecutorService;
        this.mHandler = mHandler;
        this.mImageCache = mImageCache;
        this.networkListener = networkListener;
        this.mLock = mLock;
        this.mCallBack = mCallBack;
        this.mLoadCondition = mLoadCondition;
        this.chain = chain;

        if (this.mLoadCondition == null) {
            this.mLoadCondition = mLock.newCondition();
        }

        if (chain == null) {
            addImageChain(new IntegerChain());
            addImageChain(new DrawableChain());
            addImageChain(new BitmapChain());
            addImageChain(new UrlChain());
        }

        ((NetworkCallBack) this.mCallBack).setRequestCondition(this.mLock, this.mLoadCondition);
        networkListener.register(PhotoLoader.getApplicationContext(),
                new NetworkRequest.Builder().build(), mCallBack);
    }

    @Override
    public void addImageChain(Chain chain) {
        chain.setNext(this.chain);
        this.chain = chain;
    }

    /**
     * 分发图片加载
     *
     * @param src       资源对象（Bitmap，Integer，Drawable）
     * @param container 加载图片容器
     */
    public void dispatchImageLoad(final Object src,
                                  final View container,
                                  final WeakReference<LifecycleOwner> lifecycle) {
        final Object bmp = mImageCache.get(src.hashCode());
        if (container instanceof ImageView) {
            ((ImageView) container).setImageDrawable(null);
        } else {
            container.setBackground(null);
        }
        if (bmp == null) {  //缓存资源对象为空则根据对象类型加载
            execute(new EngineJob(src, container, chain, mLock, this.mLoadCondition, lifecycle,
                    Engine.this.mImageCache, mExecutorService));
        } else {
            execute(new Runnable() {
                @Override
                public void run() {
                    loadImage(bmp, container);
                }
            });
        }
    }

    /**
     * 通过接口返回图片资源对象
     *
     * @param src          src，可能是Integer，Drawable，String，Bitmap中的一种
     * @param lifecycle    与activity生命周期绑定
     * @param simpleTarget 回调接口
     */
    @Override
    public void dispatchImageLoad(final Object src,
                                  final WeakReference<LifecycleOwner> lifecycle,
                                  final SimpleTarget simpleTarget) {
        final Object bmp = mImageCache.get(src.hashCode());
        if (bmp == null) {
            execute(new EngineJob(src, null, chain, mLock, mLoadCondition, lifecycle, mImageCache,
                    mExecutorService, simpleTarget));
        } else {
            execute(new Runnable() {
                @Override
                public void run() {
                    simpleTarget.onComplete(bmp);
                }
            });
        }
    }

    /**
     * 加载图片
     *
     * @param bmp       可能为Drawable，Bitmap类型
     * @param container 需为ImageView
     */
    private void loadImage(final Object bmp, final View container) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (container instanceof ImageView) {
                    if (bmp instanceof Bitmap)
                        ((ImageView) container).setImageBitmap((Bitmap) bmp);
                    else if (bmp instanceof Drawable)
                        ((ImageView) container).setImageDrawable((Drawable) bmp);
                }
            }
        });
    }

    @Override
    public void execute(@NonNull Runnable runnable) {
        mExecutorService.execute(runnable);
    }

    @Override
    public void release() {
        mImageCache.release();
    }

    @Override
    public void onError(final Context context, final String message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
