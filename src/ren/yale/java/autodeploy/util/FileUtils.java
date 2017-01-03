package ren.yale.java.autodeploy.util;

import java.io.File;

/**
 * Created by Yale on 2017/1/3.
 */
public class FileUtils {

    public static void deleteFile(File  file) {

        try {
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void deleteFile(String filePath){

        try{
            File file = new File(filePath);
            deleteFile(file);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
