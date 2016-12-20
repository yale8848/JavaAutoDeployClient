package ren.yale.java.autodeploy.http;

/**
 * Created by Yale on 2016/12/20.
 */
public class HttpGet extends HttpMethod {


    @Override
    public String execute() {
        return HttpClient.get(mUrl);
    }

    @Override
    public void setUrl(String url) {

        mUrl = url;
    }
}

