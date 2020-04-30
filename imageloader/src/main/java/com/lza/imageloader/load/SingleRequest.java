package com.lza.imageloader.load;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.lza.imageloader.interfaces.Requestable;
import com.lza.imageloader.util.Util;

import java.net.HttpURLConnection;
import java.net.URL;

public class SingleRequest implements Requestable {
    @Override
    public Drawable request(String url) {
        Util.assertBackgroundThread();
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        try {
            URL urls = new URL(url);
            final HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream(), null, options);
            conn.disconnect();
            return new BitmapDrawable(null, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
