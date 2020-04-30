package com.lza.imageloader.interfaces;

import java.io.FileNotFoundException;

public interface DiskMemoryCheck {

    void onCheckDiskMemoryCheck() throws FileNotFoundException;
}
