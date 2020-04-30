package com.lza.imageloader.interfaces;

public interface SimpleTarget {
    /**
     * 执行图片加载回调接口
     * @param result 可以是任意类型的图片对象，Drawable, Bitmap等
     */
    void onComplete(Object result);
}
