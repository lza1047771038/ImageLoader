package com.lza.imageloader.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Looper;

import java.io.InputStream;
import java.io.OutputStream;

public class Util {

    /**
     * 再重写自己的图片磁盘缓存时，如果重写了自己的磁盘缓存操作，请务必调用此处的写入操作
     * 来避免Drawable对象和Bitmap对象无法被缓存的现象发生
     * <p>
     * 此处在多线程中进行调用，为了保证线程安全和避免重复对磁盘数据进行写入，使用双重校验进行判断
     * 此处的file.exists()在文件创建之后就会返回true，所以在多线程中是安全的
     *
     * @param outputStream outputStream
     * @return boolean
     */
    public static boolean writeIconIntoStorage(OutputStream outputStream, Object bmp) {
        if (bmp instanceof Bitmap) {
            ((Bitmap) bmp).compress(Bitmap.CompressFormat.PNG, 95, outputStream);
            return true;
        } else if (bmp instanceof Drawable) {
            ((BitmapDrawable) bmp).getBitmap().compress(Bitmap.CompressFormat.PNG, 95,
                    outputStream);
            return true;
        }
        return false;
    }

    public static boolean isNetWorkConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getActiveNetworkInfo() != null) {
            return connectivityManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    public static void assertMainThread() {
        if (!isOnMainThread()) {
            throw new IllegalArgumentException("You must call this method on main thread");
        }
    }

    public static void assertBackgroundThread() {
        if (!isOnBackgroundThread()) {
            throw new IllegalArgumentException("You must call this method on background thread");
        }
    }

    private static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private static boolean isOnBackgroundThread() {
        return !isOnMainThread();
    }

    public static void closeInputStream(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeOutputStream(OutputStream outputStream) {

        try {
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
