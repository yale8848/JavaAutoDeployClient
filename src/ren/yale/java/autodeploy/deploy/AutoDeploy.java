package ren.yale.java.autodeploy.deploy;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import ch.ethz.ssh2.util.SCPClientTransformListener;
import ren.yale.java.autodeploy.Main;
import ren.yale.java.autodeploy.http.HttpGet;
import ren.yale.java.autodeploy.http.HttpMethod;
import ren.yale.java.autodeploy.http.HttpPost;
import ren.yale.java.autodeploy.util.FileUtils;
import ren.yale.java.autodeploy.util.LogUtils;
import ren.yale.java.autodeploy.util.ZipFileUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Yale on 2016/12/17.
 */
public class AutoDeploy implements IAutoDeployAction{



    String host;
    String usrName;
    String password;
    Map<String,String> mapUpload;
    List<String> commandList;
    List<String> zipFileList;
    List<HttpMethod> apis;
    Connection conn;

    LogUtils logUtils = LogUtils.create();

    AutoDeployListener autoDeployListener;

    boolean isFinish=false;

    public boolean isFinish() {
        return isFinish;
    }


    public void setServerInfo(String host, String usrName, String password) {

        this.host = host;
        this.usrName = usrName;
        this.password = password;
    }

    public void setUploadFileInfo(Map<String,String> map) {
        this.mapUpload = map;
    }

    public void setCommands(List<String> commandList) {
        this.commandList=commandList;

    }
    public void setVerifyApi(List<HttpMethod> apis){
        this.apis = apis;
    }
    public void start(AutoDeploy.AutoDeployListener autoDeployListener) throws Exception{
        this.autoDeployListener =autoDeployListener;
        long  startTime = System.currentTimeMillis();
        zipDir();
        connect();
        upload();
        download();
        command();
        logUtils.setUsedTime((int) (System.currentTimeMillis()-startTime));
        close();

    }

    private String getRemoteParentPath(String remotePath){
        StringBuffer sbR  = new StringBuffer();
        sbR.append(remotePath);
        if (remotePath.endsWith("/")){
            sbR.deleteCharAt(remotePath.length()-1);
        }

        String zipPath =sbR.append(".zip").toString();

        int posR = sbR.toString().lastIndexOf('/');
        if (posR==-1){
            posR = sbR.toString().lastIndexOf('\\');
        }
        return sbR.substring(0,posR+1);
    }

    private List<String> getZipCommand(String remotePath){

        List<String> commandsList = new ArrayList<String>();

        StringBuffer sbR  = new StringBuffer();
        sbR.append(remotePath);
        if (remotePath.endsWith("/")){
            sbR.deleteCharAt(remotePath.length()-1);
        }

        String zipPath =sbR.append(".zip").toString();

        int posR = sbR.toString().lastIndexOf('/');
        if (posR==-1){
            posR = sbR.toString().lastIndexOf('\\');
        }
        String parentPath = sbR.substring(0,posR+1);

        commandsList.add("unzip -q -o "+zipPath +" -d "+parentPath);
        commandsList.add("rm -f "+zipPath);

        return commandsList;


    }
    private void zipDir() throws Exception{
        if (mapUpload==null)return;
        Map<String,String> dirMap = new HashMap<>();
        Map<String,String> tmp = new HashMap<>();
        tmp.putAll(mapUpload);
        for (Map.Entry<String,String> entry: tmp.entrySet()) {
            if (ZipFileUtils.isDir(entry.getKey())){

                mapUpload.remove(entry.getKey());
                logUtils.d(host+" :zip "+entry.getKey());
                String zn = ZipFileUtils.getZipName(entry.getKey(),entry.getValue());
                ZipFileUtils.zipDir(entry.getKey(),zn);
                dirMap.put(zn,getRemoteParentPath(entry.getValue()));

                if (zipFileList==null){
                    zipFileList = new ArrayList<String>();
                }
                zipFileList.add(zn);

                if (commandList==null){
                    commandList = new ArrayList<String>();
                }
                for (int i = getZipCommand(entry.getValue()).size() -1;i>=0;i--){
                    commandList.add(0,getZipCommand(entry.getValue()).get(i));
                }
            }

        }
        if (dirMap.size()>0){
            mapUpload.putAll(dirMap);
        }
    }


    @Override
    public void connect() throws Exception {
        logUtils.d(host+" : try to connect");
        conn = new Connection(host);
        conn.connect();
        boolean isAuthenticated = conn.authenticateWithPassword(usrName, password);
        if (isAuthenticated == false)
            throw new IOException(host+ "Authentication failed.");
        logUtils.d(host+" : connect success");
        logUtils.setConnectInfo(host+" : connect success");

    }

    @Override
    public void upload() throws Exception{
        if (mapUpload==null) return;

        SCPClient scpClient = conn.createSCPClient();

        for (Map.Entry<String, String> it :mapUpload.entrySet()) {
            final File f = new File(it.getKey());
            scpClient.put(it.getKey(), it.getValue(), new SCPClientTransformListener() {
                @Override
                public void transform(long uploadedSize, long total, int percent) {
                    String log = host+" : " +" upload "+f.getName()+" "+percent+"%";
                    logUtils.setUploadInfo(log);
                    logUtils.d(log);

                }
            });
        }
    }


    @Override
    public void download()throws Exception {

    }

    private void stoutInfo(Session session,String cmd) throws Exception{
        InputStream stdout = new StreamGobbler(session.getStdout());

        BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

        while (true)
        {
            String line = br.readLine();
            if (line == null)
                break;
            String log = host +" : "+" command : "+cmd +"; feedback : "+line;
            logUtils.d(log);
            logUtils.addCommandInfo(log);
        }
        String log = host +" : "+" command : "+cmd +"; ExitCode : "+session.getExitStatus();
        logUtils.d(log);
        logUtils.addCommandInfo(log);
        session.close();

    }
    @Override
    public void command()throws Exception {

        if (commandList==null)return;

        for (String c:commandList) {
            Session session = conn.openSession();
            String log = host +" : "+" command : "+c +" start";
            logUtils.d(log);
            logUtils.addCommandInfo(log);
            session.execCommand(c);
            stoutInfo(session,c);
        }
    }

    @Override
    public void verifyApi() throws Exception {
        if (apis==null||apis.size()==0)return;

        for (HttpMethod http:apis) {
            LogUtils.d("after "+http.timeDelay/1000+" seconds verify api :"+http.getUrl());
        }

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                verify();
            }
        },apis.get(0).timeDelay);

    }

    private void verify(){

        List<String> logList = new ArrayList<String>();
        for(int i = 0;i<apis.get(0).requestCount;i++){
            boolean success =true;
            logList.clear();
            for (HttpMethod http:apis) {
                String ret = http.execute();
                String method = "get";
                String parmas ="";
                if (http instanceof HttpPost){
                    method ="post";
                    HttpPost p = (HttpPost) http;

                    StringBuffer sb = new StringBuffer();
                    if (p.getParams()!=null){

                        for (Map.Entry<String,String> le:p.getParams().entrySet()){
                            sb.append(le.getKey());
                            sb.append("=");
                            sb.append(le.getValue());
                            sb.append("&");
                        }
                        if (sb.length()>0){
                            sb.deleteCharAt(sb.length()-1);
                        }
                        parmas = sb.toString();
                    }
                }
                String log = "verify : "+http.getUrl()+" "+method+" "+parmas+"  result : "+(ret.length() ==0?"失败":ret);
                if (ret.length() == 0){
                    success =false;
                }else{
                    logList.add(log);
                }
                LogUtils.d(log);
            }

            if (success){
                autoDeployListener.verifySucess(logList);
                break;
            }

            try {
                Thread.sleep(apis.get(0).timeGap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void deleteZipFiles(){
        if(zipFileList!=null){
            for (String zipF : zipFileList){
                FileUtils.deleteFile(zipF);
                logUtils.d("delete "+zipF);
            }
        }
    }
    @Override
    public void close()throws Exception {
        conn.close();

        deleteZipFiles();

        isFinish = true;
        autoDeployListener.finish();


    }

    public interface AutoDeployListener{
        void finish();
        void verifySucess(List<String> log);
    }
}
