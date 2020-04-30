package com.lza.imageloader.interfaces;

public interface ImageCache {

    Object get(Integer key);

    void put(Integer key, Object bmp);

    void release();
}
