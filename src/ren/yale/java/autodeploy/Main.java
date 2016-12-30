package ren.yale.java.autodeploy;

import ren.yale.java.autodeploy.deploy.AutoDeployManager;
import ren.yale.java.autodeploy.util.FileUtils;
import ren.yale.java.autodeploy.util.LogUtils;

import java.io.File;

/**
 * Created by Yale on 2016/12/17.
 */
public class Main {

    public static void main(String args[]){


        String path = "G:\\tmp\\test\\aaa";

        String d = FileUtils.getZipName(path);

        String dest = path+".zip";
        try {
            FileUtils.zipDir(path,dest);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return;

/*        if (args.length == 0){
            LogUtils.d("try to load config.xml");
            try {
                AutoDeployManager.SELF.loadXML("config.xml").start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            try {
                AutoDeployManager.SELF.loadXML(args[0]).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

    }
}
