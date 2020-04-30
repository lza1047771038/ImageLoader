package com.lza.imageloader.interfaces;

import android.graphics.drawable.Drawable;

public interface Requestable {
    Drawable request(String url);
}
