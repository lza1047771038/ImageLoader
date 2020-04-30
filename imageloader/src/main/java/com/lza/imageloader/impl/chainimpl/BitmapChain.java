package com.lza.imageloader.impl.chainimpl;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
 * 传入Bitmap对象并设置
 */
public class BitmapChain implements Chain {

    private Chain next;

    @Override
    public void setNext(Chain chain) {
        this.next = chain;
    }

    @Override
    public boolean invoke(final Object src, final View view, Lock lock, Condition condition,
                          ImageCache mImageCache, SimpleTarget simpleTarget) {
        if (src instanceof Bitmap) {
            if (simpleTarget != null) {
                simpleTarget.onComplete(src);
            } else {
                final TransitionDrawable td =
                        new TransitionDrawable(new Drawable[]{new ColorDrawable(view.getContext().getResources().getColor(android.R.color.transparent)),
                                new BitmapDrawable(null, (Bitmap) src)});
                td.setCrossFadeEnabled(true);
                if (view instanceof ImageView) {
                    PhotoLoader.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
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
            }
            mImageCache.put(src.hashCode(), src);
            return true;
        }
        return next != null && next.invoke(src, view, lock, condition, mImageCache, simpleTarget);
    }

    @Override
    public Chain getNext() {
        return next;
    }

}
