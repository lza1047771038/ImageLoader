package com.lza.imageloader.impl.cacheimpl;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.lza.imageloader.interfaces.DiskMemoryCheck;
import com.lza.imageloader.interfaces.ImageCache;
import com.lza.imageloader.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class DiskCache implements ImageCache, DiskMemoryCheck {

    private String cacheDir;
    private long LIMITES = 40 * 1024 * 1024;
    private long SIZE_OF_FILES;

    DiskCache(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cacheDir = context.getExternalCacheDir().getPath();
        } else {
            cacheDir = context.getCacheDir().getPath();
        }
    }

    @Override
    public Object get(Integer key) {
        return BitmapFactory.decodeFile(cacheDir + File.separator + key.hashCode());
    }

    @Override
    public void put(Integer key, Object bmp) {
        FileOutputStream outputStream = null;
        try {
            File file = new File(cacheDir + File.separator + key.hashCode());
            outputStream = new FileOutputStream(file);
            if(!Util.writeIconIntoStorage(outputStream, bmp)){
                Log.e("DiskCache", "//...");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            Util.closeOutputStream(outputStream);
        }
    }

    @Override
    public void release() {

    }

    @Override
    public void onCheckDiskMemoryCheck() {
        File cacheFiles = new File(cacheDir);
        if (!cacheFiles.exists())
            Log.e("DiskCache", "cache Directory does not exist: " + cacheDir);
        if (cacheFiles.exists()) {
            File[] files = cacheFiles.listFiles();
            assert files != null;
            for (File file : files) {
                SIZE_OF_FILES += file.length();
            }
            if (SIZE_OF_FILES > LIMITES) {
                for (File file : files) {
                    if (file.delete()) {
                        //...
                    }
                }
            }
        }

    }
}
