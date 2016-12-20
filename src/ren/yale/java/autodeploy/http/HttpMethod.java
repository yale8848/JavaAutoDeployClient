package ren.yale.java.autodeploy.http;

/**
 * Created by Yale on 2016/12/20.
 */
public abstract class HttpMethod {
    protected String mUrl = "";
    abstract String execute();
    abstract void setUrl(String url);
}
