package ren.yale.java.autodeploy;

import ren.yale.java.autodeploy.deploy.AutoDeploy;
import ren.yale.java.autodeploy.deploy.AutoDeplyBuilder;
import ren.yale.java.autodeploy.http.HttpGet;
import ren.yale.java.autodeploy.http.HttpMethod;
import ren.yale.java.autodeploy.http.HttpPost;
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

        List<HttpMethod> apis =new ArrayList<HttpMethod>();
        HttpGet httpGet = new HttpGet();
        httpGet.setUrl("http://xxxx/app/info");
        apis.add(httpGet);

        HttpPost httpPost = new HttpPost();
        httpPost.setUrl("http://xxxx/app/info");
        Map<String,String> params = new HashMap<String,String>();
        params.put("key","value");
        httpPost.setParams(params);

        apis.add(httpPost);

        AutoDeploy autoDeploy = AutoDeplyBuilder.create().
                setServerInfo("192.168.0.1","root","123456").
                setUploadFileInfo(uploadMap).
                setCommands(commands).
                setVerifyApi(apis).
                build();

        try {
            autoDeploy.start(new AutoDeploy.AutoDeployListener() {
                @Override
                public void finish() {

                }

                @Override
                public void verifySucess(List<String> log) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
