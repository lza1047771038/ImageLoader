package com.lza.imageloader.impl.chainimpl;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.widget.ImageView;

import com.lza.imageloader.interfaces.Chain;
import com.lza.imageloader.interfaces.ImageCache;
import com.lza.imageloader.interfaces.SimpleTarget;
import com.lza.imageloader.load.PhotoLoader;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 传入Drawable对象并进行设置
 */
public class DrawableChain implements Chain {
    private Chain next;

    @Override
    public void setNext(Chain chain) {
        next = chain;
    }

    @Override
    public boolean invoke(Object src, final View view, Lock lock, Condition condition,
                          ImageCache mImageCache, SimpleTarget simpleTarget) {
        if (src instanceof Drawable) {
            if (simpleTarget != null) {
                simpleTarget.onComplete(src);
            } else {
                final TransitionDrawable td =
                        new TransitionDrawable(new Drawable[]{new ColorDrawable(Color.parseColor(
                                "#00000000")),
                                (Drawable) src});
                td.setCrossFadeEnabled(true);
                if (view instanceof ImageView) {
                    PhotoLoader.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView) view).setScaleType(ImageView.ScaleType.CENTER_CROP);
                            ((ImageView) view).setImageDrawable(td);
                            td.startTransition(300);
                        }
                    });
                } else {
                    PhotoLoader.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            view.setBackground(td);
                            td.startTransition(300);
                        }
                    });
                }
                mImageCache.put(src.hashCode(), src);
            }
            return true;
        }
        return next != null && next.invoke(src, view, lock, condition, mImageCache, simpleTarget);
    }

    @Override
    public Chain getNext() {
        return next;
    }
}
