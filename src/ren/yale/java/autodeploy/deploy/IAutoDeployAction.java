package ren.yale.java.autodeploy.deploy;

/**
 * Created by Yale on 2016/12/17.
 */
public interface IAutoDeployAction {
    void connect ()throws Exception;
    void upload()throws Exception;
    void download()throws Exception;
    void command()throws Exception;
    void verifyApi() throws Exception;
    void close()throws Exception;
}
