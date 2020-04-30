package com.lza.imageloader.impl.cacheimpl;

import android.content.Context;

import com.lza.imageloader.interfaces.DiskMemoryCheck;
import com.lza.imageloader.interfaces.ImageCache;

public class MixCache implements ImageCache, DiskMemoryCheck {

    private DiskCache diskCache;
    private MemoryCache memoryCache;

    private boolean skipMemoryCache = false;
    private boolean skipDiskCache = false;

    public MixCache(Context context) {
        memoryCache = new MemoryCache();
        diskCache = new DiskCache(context);
        onCheckDiskMemoryCheck();
    }

    public MixCache() {
        memoryCache = new MemoryCache();
        skipDiskCache = true;
    }

    public void setSkipMemoryCache(boolean skip) {
        this.skipMemoryCache = skip;
    }

    @Override
    public Object get(Integer key) {
        Object bitmap = null;
        if (!skipMemoryCache)
            bitmap = memoryCache.get(key);
        if (!skipDiskCache && bitmap == null) {
            bitmap = diskCache.get(key);
            if (bitmap != null)
                memoryCache.put(key, bitmap);
        }
        return bitmap;
    }

    @Override
    public void put(Integer key, Object bmp) {
        if (!skipMemoryCache) {
            memoryCache.put(key, bmp);
        }
        if (!skipDiskCache) {
            diskCache.put(key, bmp);
        }
    }

    @Override
    public void release() {
        memoryCache.release();
        diskCache.release();
    }

    @Override
    public void onCheckDiskMemoryCheck() {
        diskCache.onCheckDiskMemoryCheck();
    }
}
