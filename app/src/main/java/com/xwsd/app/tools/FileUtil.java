package com.xwsd.app.tools;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtil {
    //app根目录
    public static final String ROOT_PATH = "/data/data/com.xwsd.app";
    /**
     * 临时文件夹
     */
    public static final String PATH_TEMP = ROOT_PATH + "/xwsd_temp/";

    /**
     * 生成文件夹
     */
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 保存Bitmap
     */
    public static boolean saveBitmap(Bitmap bm, String path, String picName) {
        makeRootDirectory(path);
        File f = new File(path, picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return true;
    }

}
