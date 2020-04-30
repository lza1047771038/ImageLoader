package com.lza.imageloader.impl.cacheimpl;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

import com.lza.imageloader.interfaces.ImageCache;

public class MemoryCache implements ImageCache {
    private LruCache<Integer, Object> mImageLruCache;

    MemoryCache() {
        mImageLruCache =
                new LruCache<Integer, Object>(((int) Runtime.getRuntime().maxMemory())  >> 4) {
                    @Override
                    protected int sizeOf(@NonNull Integer key, @NonNull Object value) {
                        if (value instanceof Bitmap) {
                            return ((Bitmap) value).getAllocationByteCount() ;
                        } else if (value instanceof Drawable) {
                            return ((BitmapDrawable) value).getBitmap().getAllocationByteCount() ;
                        }
                        return 0;
                    }
                };
    }

    @Override
    public Object get(Integer key) {
        return mImageLruCache.get(key.hashCode());
    }

    @Override
    public void put(Integer key, Object bmp) {
        mImageLruCache.put(key.hashCode(), bmp);
    }

    @Override
    public void release() {
        mImageLruCache.evictAll();
    }
}