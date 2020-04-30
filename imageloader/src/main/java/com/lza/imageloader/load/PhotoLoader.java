package com.lza.imageloader.load;

import android.content.Context;
import android.os.Looper;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.lza.imageloader.interfaces.Chain;
import com.lza.imageloader.interfaces.EngineService;
import com.lza.imageloader.interfaces.SimpleTarget;

import java.lang.ref.WeakReference;

public class PhotoLoader {

    private static int PLACE_HOLDER;
    private static PhotoLoader mInstance;
    private static WeakReference<LifecycleOwner> lifecycleRef;
    private static android.os.Handler mHandler;
    private static WeakReference<Context> contextWeakReference;

    public static void init(Context context) {
        contextWeakReference = new WeakReference<>(context.getApplicationContext());
        if (mInstance == null) {
            synchronized (PhotoLoader.class) {
                if (mInstance == null) {
                    mInstance = new PhotoLoader();
                }
            }
        }
        if (mHandler == null) {
            synchronized (PhotoLoader.class) {
                if (mHandler == null) {
                    mHandler = new android.os.Handler(Looper.getMainLooper());
                }
            }
        }
    }

    static Context getApplicationContext() {
        assert contextWeakReference != null;
        return contextWeakReference.get();
    }

    public static int getPlaceHolder() {
        return PLACE_HOLDER;
    }

    public static void setPlaceHolder(int resID) {
        PLACE_HOLDER = resID;
    }

    /**
     * 获得MainLooper对应的handler，这里是实现异步加载的核心
     *
     * @return Handler
     */
    public static android.os.Handler getHandler() {
        if (mHandler == null)
            throw new NullPointerException("PhotoLoader Instance was null, have you forget to " +
                    "call init(Context context) method before you Call getHandler() method?");
        return mHandler;
    }

    /**
     * 实现图片加载与生命周期绑定的方法
     *
     * @param lifecycle LifecycleOwner
     * @return PhotoLoader
     */
    public static PhotoLoader with(LifecycleOwner lifecycle) {
        if (mInstance == null)
            throw new NullPointerException("PhotoLoader Instance was null, have you forget to " +
                    "call init(Context context) method before you Call getInstance() method?");
        if (PhotoLoader.lifecycleRef != null)
            PhotoLoader.lifecycleRef.clear();
        PhotoLoader.lifecycleRef = new WeakReference<>(lifecycle);
        return mInstance;
    }

    /**
     * 获得全局唯一单例，在调用此方法前，请首先调用Init(Context context)
     * 来初始化对象
     *
     * @return PhotoLoader
     */
    public static PhotoLoader getInstance() {
        if (mInstance == null)
            throw new NullPointerException("PhotoLoader Instance was null, have you forget to " +
                    "call init(Context context) method before you Call getInstance() method?");
        return mInstance;
    }

    /**
     * 图片缓存
     * 默认使用内存和磁盘双缓存
     */
    private EngineService mEngine;

    private PhotoLoader() {
        this(new Engine());
    }

    private PhotoLoader(EngineService mEngine) {
        this.mEngine = mEngine;
    }

    public void setEngine(EngineService engineService) {
        this.mEngine = engineService;
    }

    /**
     * 添加图片加载器（默认包含网络Url加载和资源文件加载Drawable两种类型）
     * 如需自定义图片加载，请实现Chain接口并对其中方法进行重写
     *
     * @param chain Chain
     */
    public void addImageLoader(Chain chain) {
        mEngine.addImageChain(chain);
    }

    /**
     * 图片加载入口
     *
     * @param src       可以是Bitmap，Integer，Drawable， String四种类型之一，也可以自定义然后添加Chain的实现类自行处理
     * @param container 装载图片的容器，已经进行相应处理，若不是ImageView
     *                  则自动设置为{@link View.setBackground(Drawable drawable);}
     */
    public void load(Object src, View container) {
        container.setTag(container.getId(), src);
        mEngine.dispatchImageLoad(src, container, lifecycleRef);
    }

    /**
     * 配置回调接口入口，
     *
     * @param src          可以是Bitmap，Integer，Drawable， String四种类型之一，也可以自定义然后添加Chain的实现类自行处理
     * @param simpleTarget {@link SimpleTarget}
     */
    public void apply(Object src, SimpleTarget simpleTarget) {
        mEngine.dispatchImageLoad(src, lifecycleRef, simpleTarget);
    }

    /**
     * 释放所有对象
     */
    public static void release() {
        getInstance().releaseMemory();
    }

    private void releaseMemory(){
        mEngine.release();
    }
}
