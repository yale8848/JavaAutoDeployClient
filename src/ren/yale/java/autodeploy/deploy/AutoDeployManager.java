package ren.yale.java.autodeploy.deploy;

import ren.yale.java.autodeploy.util.LogUtils;
import ren.yale.java.autodeploy.util.XmlProcessor;

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
    private Timer timer = new Timer();
    private  List<AutoDeploy> autoDeployList;

    private long startTime = 0;

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
                        a.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        timer.cancel();
                    }
                }
            });
        }
        if (autoDeployList.size()>0){
            timer.schedule(new LogTask(),1000,2000);
        }
    }


    private class LogTask extends TimerTask{

        /**
         * The action to be performed by this timer task.
         */
        @Override
        public void run() {

            boolean allFinish = true;
            for (AutoDeploy a:autoDeployList) {
                if (!a.isFinished()){
                    allFinish = false;
                    break;
                }
            }
            if (allFinish){

                for (LogUtils log:LogUtils.logUtilsVector) {
                    log.finish();
                }
                LogUtils.d("");
                LogUtils.d("-------------------------------------");
                LogUtils.d("total time :"+LogUtils.timeFormat(System.currentTimeMillis()-startTime));
                LogUtils.d("total success :"+autoDeployList.size());
                LogUtils.d("-------------------------------------");
                LogUtils.d("");
                timer.cancel();
            }
        }
    }



}
