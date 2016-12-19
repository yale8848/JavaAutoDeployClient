package ren.yale.java.autodeploy.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ren.yale.java.autodeploy.deploy.AutoDeploy;
import ren.yale.java.autodeploy.deploy.AutoDeplyBuilder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yale on 2016/12/17.
 */
public enum XmlProcessor {

    SELF;

    private List<AutoDeploy> autoDeployList = new ArrayList<AutoDeploy>();
    private int threadPoolSize = 5;

    public int getThreadPoolSize() {
        return threadPoolSize;
    }


    public Map<String,String> getUploadInfo(Node uploads){

        Map<String,String> map = new HashMap<String,String>();

        NodeList uploadlistUp = uploads.getChildNodes();

        for (int k=0;k<uploadlistUp.getLength();k++){

            Node n = uploadlistUp.item(k);

            if (n.getNodeType()==Node.ELEMENT_NODE&&
                    n.getNodeName().equals("upload")){
                NodeList uploadlist = n.getChildNodes();


                String local ="";
                String remote="";

                for (int i =0;i<uploadlist.getLength();i++){
                    Node up =  uploadlist.item(i);


                    if(up.getNodeType()==Node.ELEMENT_NODE){

                        if (up.getNodeName().equals("local")){
                            local = up.getTextContent();
                        }else if (up.getNodeName().equals("remote")){
                            remote = up.getTextContent();
                        }
                    }

                }

                map.put(local,remote);
            }

        }
        return map;

    }

    private List<String> getCommandsList(Node commands){
        List<String> list = new ArrayList<String>();

        NodeList nodeList = commands.getChildNodes();

        for (int i =0;i<nodeList.getLength();i++){
            Node up =  nodeList.item(i);
            if(up.getNodeType()==Node.ELEMENT_NODE){

                if (up.getNodeName().equals("command")){
                    list.add(up.getTextContent());
                }
            }



        }

        return list;
    }

    private void getThreadPoolSize(Document document){
        NodeList threadPoolSizeList =  document.getElementsByTagName("threadPoolSize");
        for (int i =0;i<threadPoolSizeList.getLength();i++){
            if (threadPoolSizeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                if (threadPoolSizeList.item(i).getNodeName().equals("threadPoolSize")){
                    try{
                        threadPoolSize = Integer.parseInt(threadPoolSizeList.item(i).getTextContent());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public XmlProcessor parse(String xmlPath)throws Exception{


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(xmlPath);

        getThreadPoolSize(document);
        NodeList servers =  document.getElementsByTagName("server");

        for (int i =0;i<servers.getLength();i++) {
            NodeList server = servers.item(i).getChildNodes();

            String host = "";
            String userName ="";
            String password = "";
            Map<String,String> uploadMap = new HashMap<String,String>();
            List<String> commands = new ArrayList<String>();

            for (int j=0;j<server.getLength();j++){
                Node ns = server.item(j);

                if (ns.getNodeType() == Node.ELEMENT_NODE){
                    if(ns.getNodeName().equals("host")){

                        host = ns.getTextContent();
                    } else if(ns.getNodeName().equals("userName")){

                        userName = ns.getTextContent();
                    } else if(ns.getNodeName().equals("password")){

                        password = ns.getTextContent();
                    } else if(ns.getNodeName().equals("uploads")){

                        uploadMap = getUploadInfo(ns);

                    } else if(ns.getNodeName().equals("commands")){

                        commands = getCommandsList(ns);
                    }
                }

            }


            AutoDeploy autoDeploy = AutoDeplyBuilder.create().
                    setServerInfo(host,userName,password).
                    setUploadFileInfo(uploadMap).
                    setCommands(commands).build();

            autoDeployList.add(autoDeploy);

        }

        return this;

    }


    public List<AutoDeploy> getAutoDeployList(){
        return autoDeployList;
    }
}
