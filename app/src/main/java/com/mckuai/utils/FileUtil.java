package com.mckuai.utils;

import java.io.File;

/**
 * Created by kyly on 2015/7/9.
 */
public class FileUtil {

    public static String readFile(File file){
        String str = null;

        return  str;
    }

    public static long getFolderSize(File file){
        File[] subFile;
        long size = 0;
        if (file.isDirectory()){
            subFile =  file.listFiles();
            for (File curFile:subFile){
                if (curFile.isFile()){
                    size += curFile.length();
                }
                else {
                    size += getFolderSize(curFile);
                }
            }
        }
        else {
            size += file.length();
        }

        return  size;
    }
}
