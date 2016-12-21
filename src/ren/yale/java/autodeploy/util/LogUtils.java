package ren.yale.java.autodeploy.util;

import java.util.Vector;

/**
 * Created by Yale on 2016/12/17.
 */
public class  LogUtils {

    public static Vector<LogUtils> logUtilsVector = new Vector<LogUtils>();

    private String connectInfo = "";
    private String uploadInfo ="";
    private Vector<String> commandInfoList = new Vector<String>();


    private int usedTime = 0;


    public static LogUtils create(){
        LogUtils logUtils = new LogUtils();
        logUtilsVector.add(logUtils);
        return logUtils;
    }

    public void setConnectInfo(String connectInfo) {

        this.connectInfo = connectInfo;
    }


    public void setUsedTime(int usedTime) {
        this.usedTime = usedTime;
    }

    public static String timeFormat(long time){
        int s = (int) (time/1000);
        if (s/60>0){
            if (s/60/60>0){
                return s/60/60+"h:"+s%(60*60)/60+"m:"+s%(60*60)%60+"s";
            }else{
                return s/60+"m:"+s%60+"s";
            }
        }else{
            return s+"s";
        }
    }

    public void setUploadInfo(String uploadInfo) {
        this.uploadInfo = uploadInfo;
    }


    public void addCommandInfo(String command) {
        this.commandInfoList.add(command);
    }

    public void finish(){

        d("=====================================");
        d(connectInfo);
        d(uploadInfo);
        for (String c:commandInfoList) {
            d(c);
        }
        d("time used :"+timeFormat(usedTime));
        d("=====================================");

    }

    public static void d(String text){
        System.out.println(text);
    }
    public static void dr(String text){
        System.out.print("\r"+text+"             ");
    }
}
