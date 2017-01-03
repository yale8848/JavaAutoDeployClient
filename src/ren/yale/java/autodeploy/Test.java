package ren.yale.java.autodeploy;

import ren.yale.java.autodeploy.deploy.AutoDeployManager;
import ren.yale.java.autodeploy.util.LogUtils;
import ren.yale.java.autodeploy.util.ZipFileUtils;

/**
 * Created by Yale on 2017/1/3.
 */
public class Test {

    public static void main(String args[]){


        String path = "G:\\tmp\\test\\aaa";

        try {
            ZipFileUtils.zipDir(path,ZipFileUtils.getZipName(path));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
