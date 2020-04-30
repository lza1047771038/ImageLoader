package com.lza.imageloader.interfaces;

import androidx.annotation.NonNull;

import java.io.InputStream;
import java.io.OutputStream;

public interface Closeable {
    void close(@NonNull InputStream inputStream);

    void close(@NonNull OutputStream outputStream);
}
