package ren.yale.java.autodeploy.deploy;

import ren.yale.java.autodeploy.http.HttpMethod;

import java.util.List;
import java.util.Map;

/**
 * Created by Yale on 2016/12/17.
 */
public class AutoDeplyBuilder {

    private Builder builder;
    public static AutoDeplyBuilder create(){
        return new AutoDeplyBuilder();
    }
    public AutoDeplyBuilder(){
        builder = new Builder();
    }

    public AutoDeplyBuilder setServerInfo(String host, String usrName, String password) {
        builder.setServerInfo(host,usrName,password);

        return this;
    }

    public AutoDeplyBuilder setUploadFileInfo(Map<String,String> mapInfo) {
        builder.setUploadFileInfo(mapInfo);
        return this;
    }

    public AutoDeplyBuilder setCommands(List<String> commandList) {
        builder.setCommands(commandList);
        return this;
    }
    public AutoDeplyBuilder setVerifyApi(List<HttpMethod> apis) {
        builder.setVerifyApi(apis);
        return this;
    }
    public AutoDeploy build(){

        return builder.getAutoDeploy();
    }

    private static class Builder implements IAutoDeployBuilder {
        private AutoDeploy autoDeploy;

        public Builder(){
            autoDeploy = new AutoDeploy();
        }
        @Override
        public void setServerInfo(String host,String usrName, String password) {
            autoDeploy.setServerInfo(host,usrName,password);
        }

        @Override
        public void setUploadFileInfo(Map<String,String> mapInfo) {
            autoDeploy.setUploadFileInfo(mapInfo);
        }

        @Override
        public void setCommands(List<String> commandList) {
            autoDeploy.setCommands(commandList);
        }

        @Override
        public void setVerifyApi(List<HttpMethod> apis) {
            autoDeploy.setVerifyApi(apis);
        }

        public AutoDeploy getAutoDeploy(){
            return autoDeploy;
        }
    }


}
