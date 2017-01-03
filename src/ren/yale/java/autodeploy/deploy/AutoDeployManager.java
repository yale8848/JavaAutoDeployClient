package ren.yale.java.autodeploy.deploy;

import ren.yale.java.autodeploy.util.LogUtils;
import ren.yale.java.autodeploy.util.XmlProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Yale on 2016/12/18.
 */
public enum  AutoDeployManager {
    SELF;
    private  ExecutorService executor;
    private  List<AutoDeploy> autoDeployList;

    private long startTime = 0;
    List<List<String>> verifyListLogs = new ArrayList<List<String>>();


    public AutoDeployManager loadXML(String xmlPath) throws Exception{
        startTime = System.currentTimeMillis();
        autoDeployList  = XmlProcessor.SELF.parse(xmlPath).getAutoDeployList();

        return this;
    }


    public void start(){


        executor = Executors.newFixedThreadPool(XmlProcessor.SELF.getThreadPoolSize());

        for (final AutoDeploy a:autoDeployList) {

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        a.start(new AutoDeploy.AutoDeployListener() {
                            @Override
                            public void finish() {
                                finishLog();
                            }

                            @Override
                            public void verifySucess(List<String> logs) {
                                verifyLog(logs);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
    private void verifyLog(List<String> log){
        verifyListLogs.add(log);
        if (verifyListLogs.size() == getVerifySize()){
            LogUtils.d("=====================================");
            LogUtils.d("all verify api finish");
            for (List<String> logs:verifyListLogs) {
                for (String l:logs) {
                    LogUtils.d(l);
                }
            }
            LogUtils.d("=====================================");

            quit();
        }

    }

    private void quit(){
        System.exit(0);
    }

    private void startVerify(){

        if (getVerifySize()==0){
            quit();
            return;
        }

        for (AutoDeploy a: autoDeployList) {
            try {
                a.verifyApi();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
    private int getVerifySize(){
        int logSize = 0;
        for (AutoDeploy a:autoDeployList) {

            if (a.apis!=null&&a.apis.size()>0){
                logSize++;
            }
        }
        return logSize;
    }
    private void finishLog(){

        boolean allFinish =true;
        for (AutoDeploy a:autoDeployList) {
            if (!a.isFinish()){
                allFinish = false;
                break;
            }
        }

        if (!allFinish){
            return;
        }

        for (LogUtils log:LogUtils.logUtilsVector) {
            log.finish();
        }
        LogUtils.d("=====================================");
        LogUtils.d("all deploy finish");
        LogUtils.d("total time :"+LogUtils.timeFormat(System.currentTimeMillis()-startTime));
        LogUtils.d("total success :"+autoDeployList.size());
        LogUtils.d("=====================================");

        startVerify();
    }



}
