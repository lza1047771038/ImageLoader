package com.lza.imageloader.impl.blur;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.lza.imageloader.interfaces.SimpleTarget;
import com.lza.imageloader.util.BlurUtil;

/**
 * 默认内嵌快速模糊函数
 * 如需事先更多功能请实现{@link SimpleTarget}接口
 */
public abstract class BlurSimpleTarget implements SimpleTarget {
    public void onComplete(Object result) {
        Drawable drawable = null;
        if (result instanceof Bitmap) {
            drawable = new BitmapDrawable(null, BlurUtil.fastblur((Bitmap) result, 25));
        } else if (result instanceof Drawable) {
            drawable = new BitmapDrawable(null,
                    BlurUtil.fastblur(((BitmapDrawable) result).getBitmap(), 25));
        } else {
            throw new ClassCastException("result is neither type of Bitmap nor Drawable");
        }
        onBlurComplete(drawable);
    }

    public abstract void onBlurComplete(Drawable blurResult);
}
