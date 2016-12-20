package ren.yale.java.autodeploy.deploy;

import ren.yale.java.autodeploy.http.HttpMethod;

import java.util.List;
import java.util.Map;

/**
 * Created by Yale on 2016/12/17.
 */
public interface IAutoDeployBuilder {
    void setServerInfo(String host,String usrName,String password);
    void setUploadFileInfo(Map<String,String> mapInfo);
    void setCommands(List<String> commandList);
    void setVerifyApi(List<HttpMethod> apis);
}
