package ren.yale.java.autodeploy.http;

/**
 * Created by Yale on 2016/12/20.
 */
public abstract class HttpMethod {
    protected String mUrl = "";
    public int requestCount = 50;
    public int timeDelay = 10*1000;
    public int timeGap = 1*1000;

    public abstract String execute();
    abstract void setUrl(String url);

    public String getUrl(){
        return mUrl;
    }
}
