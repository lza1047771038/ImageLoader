package com.lza.imageloader.impl.chainimpl;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.widget.ImageView;

import com.lza.imageloader.interfaces.Chain;
import com.lza.imageloader.interfaces.ImageCache;
import com.lza.imageloader.interfaces.Requestable;
import com.lza.imageloader.interfaces.SimpleTarget;
import com.lza.imageloader.load.PhotoLoader;
import com.lza.imageloader.load.RequestBuilder;
import com.lza.imageloader.util.Util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 传入Url通过网络加载图片
 */
public class UrlChain implements Chain {
    private Chain next;

    @Override
    public void setNext(Chain next) {
        this.next = next;
    }

    @Override
    public boolean invoke(final Object src, final View view, Lock lock, Condition condition,
                          ImageCache mImageCache, SimpleTarget simpleTarget) {
        if (src instanceof String) {
            /*
              执行前判断设备网络是否可用，不可用则则阻塞
              后续如果网络状态切换则通过同一个实例进行signal唤醒阻塞的线程，进行无限循环判断是为了保证安全
              如果切换网络之后依旧没有网络可用，那么继续阻塞
             */
            try {
                lock.lock();
                while (simpleTarget == null && !Util.isNetWorkConnected(view.getContext())) {
                    condition.await();
                }
                lock.unlock();
                if (simpleTarget != null) {
                    final Drawable drawable = getImageFromUrl((String) src);
                    simpleTarget.onComplete(drawable);
                    assert drawable != null;
                    mImageCache.put(src.hashCode(), drawable);
                } else {
                    final Drawable drawable = getImageFromUrl((String) src);
                    final TransitionDrawable td =
                            new TransitionDrawable(
                                    new Drawable[]{
                                            new ColorDrawable(
                                                    view.getContext().getResources().getColor(android.R.color.transparent)),
                                            drawable});
                    td.setCrossFadeEnabled(true);
                    if (view instanceof ImageView) {
                        PhotoLoader.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                        /*
                          异步执行时会出现图片加载错乱的现象，我们通过向View写入tag来保证同时只有一个线程能对图片进行设置
                         */
                                if (view.getTag(view.getId()).equals(src)) {
                                    ((ImageView) view).setImageDrawable(td);
                                    td.startTransition(300);
                                }
                            }
                        });
                    } else {
                        PhotoLoader.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                        /*
                          异步执行时会出现图片加载错乱的现象，我们通过向View写入tag来保证同时只有一个线程能对图片进行设置
                         */
                                if (view.getTag(view.getId()).equals(src)) {
                                    view.setBackground(td);
                                    td.startTransition(300);
                                }
                            }
                        });
                    }
                    assert drawable != null;
                    mImageCache.put(src.hashCode(), drawable);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return next != null && next.invoke(src, view, lock, condition, mImageCache,
                simpleTarget);
    }

    private Drawable getImageFromUrl(String url) {
        final Requestable requestable = RequestBuilder.createSingleRequest();
        return requestable.request(url);
    }

    @Override
    public Chain getNext() {
        return next;
    }
}
