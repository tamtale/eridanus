package com.week1.game;

import java.io.File;

public interface FileUtil {
    static String fixPath(String filePath) {
        return filePath.replace('\\', File.separatorChar).replace('/', File.separatorChar);
    }
}
