package ren.yale.java.autodeploy.deploy;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import ch.ethz.ssh2.util.SCPClientTransformListener;
import ren.yale.java.autodeploy.util.LogUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by Yale on 2016/12/17.
 */
public class AutoDeploy implements IAutoDeployAction {


    String host;
    String usrName;
    String password;
    Map<String,String> mapUpload;
    List<String> commandList;

    Connection conn;

    LogUtils logUtils = LogUtils.create();

    boolean isFinished = false;

    long startTime=0;

    public boolean isFinished() {
        return isFinished;
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
    public void start() throws Exception{
        startTime = System.currentTimeMillis();
        connect();
        upload();
        download();
        command();
        close();
        logUtils.setUsedTime((int) (System.currentTimeMillis()-startTime));
    }


    @Override
    public void connect() throws Exception {
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
    public void close()throws Exception {
        conn.close();
        isFinished = true;
    }
}
