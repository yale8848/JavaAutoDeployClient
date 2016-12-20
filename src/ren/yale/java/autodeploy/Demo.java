package ren.yale.java.autodeploy;

import ren.yale.java.autodeploy.deploy.AutoDeploy;
import ren.yale.java.autodeploy.deploy.AutoDeplyBuilder;
import ren.yale.java.autodeploy.util.XmlProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yale on 2016/12/19.
 */
public class Demo {

    public static void main(String args[]){


        Map<String,String> uploadMap = new HashMap<String,String>();
        uploadMap.put("c:\\test.jar","/home");

        List<String> commands = new ArrayList<String>();
        commands.add("/home/restart.sh");

        AutoDeploy autoDeploy = AutoDeplyBuilder.create().
                setServerInfo("192.168.0.1","root","123456").
                setUploadFileInfo(uploadMap).
                setCommands(commands).build();

        try {
            autoDeploy.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
