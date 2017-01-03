# JavaAutoDeployClient

java一键自动部署war包，jar包工具


dowload [JavaAutoDeployClient-1.3.jar](https://github.com/yale8848/JavaAutoDeployClient/blob/master/release/1.3/JavaAutoDeployClient-1.3.jar?raw=true)

# 用法

### 直接用批处理

- 创建 config.xml

```

<?xml version="1.0" encoding="utf-8" ?>
<config>
    <threadPoolSize default="5">3</threadPoolSize><!--线程池大小，如果上传服务器多的话可以调大，默认5个线程-->
    <servers>
        <server>
            <host>192.168.0.1,192.168.0.3</host><!--远程服务器地址,多个用逗号隔开，要求其它配置必须一样-->
            <userName>root</userName><!--ssh登录名称-->
            <password>123456</password><!--ssh登录密码-->
            <uploads>
                <upload>
                    <local>C:\test.jar</local><!--本地要上传至服务器的文件或目录-->
                    <remote>/home</remote><!--服务器目录-->
                </upload>
            </uploads>
            <commands>
                <command>/home/restart.sh</command><!--上传完完文件后要处理的命令，可以多个-->
                <command>/home/restart.sh2</command>
            </commands>

            <!--服务器应用启动后的验证接口，用于验证最新的代码是否更新成功，这个接口得自己定义，轮询验证直到成功-->
            <verify requestCount="51" timeDelay="20000" timeGap="2000"><!--requestCount：接口访问次数，默认50，timeDelay:
            服务器命令执行完后多长时间开始启动验证接口(单位毫秒，默认10000)，timeGap: 轮询时间间隔（单位毫秒，默认1000） -->
                <httpapi method="get" url="http://xxx/app/info"/><!--method: http 请求方法；url：http 接口 url-->
                
                <httpapi method="post" url="http://xxx/test/testPost">
                    <param key="aaa">000</param><!--post 参数 键值-->
                    <param key="bbb">111</param>
                </httpapi>
            </verify>

        </server>

        <server>
            <host>192.168.0.2</host>
            <userName>root</userName>
            <password>123456</password>
            <uploads>
                <upload>
                    <local>C:\test.jar</local>
                    <remote>/home</remote>
                </upload>
                <upload>
                    <local>C:\test2.jar</local>
                    <remote>/home</remote>
                </upload>
            </uploads>
            <commands>
                <command>/home/restart.sh</command>
                <command>/home/restart2.sh</command>
            </commands>

            <verify requestCount="51" timeDelay="20000" timeGap="2000">
                <httpapi method="get" url="http://xxx/app/info"/>

            </verify>

        </server>

    </servers>

</config>

```

- 一键调用命令

```

java -jar JavaAutoDeployClient-1.1.jar config.xml


```

- 上传war包config的例子

```
<?xml version="1.0" encoding="utf-8" ?>
<!--use ssh user password-->
<config>
    <threadPoolSize default="5">3</threadPoolSize>
    <servers>
        <server>
            <host>192.168.0.1</host>
            <userName>root</userName>
            <password>123456</password>
            <uploads>
                <upload>
                    <local>C:\javawebdeploy.war</local>
                    <remote>/coder/tomcat/apache-tomcat-7.0.55/webapps</remote>
                </upload>
            </uploads>
            <commands>
                    <command>sh /coder/tomcat/apache-tomcat-7.0.55/bin/shutdown.sh</command>
					<command>rm -rf /coder/tomcat/apache-tomcat-7.0.55/webapps/javawebdeploy</command>
					<command>sh /coder/tomcat/apache-tomcat-7.0.55/bin/startup.sh</command>
            </commands>
             <verify requestCount="51" timeDelay="20000" timeGap="2000">
               <httpapi method="get" url="http://xxx/app/info"/>
            </verify>
        </server>
    </servers>
</config>



```

- 用maven打包的命令例子


autodeploy.bat

```
call maven-package.bat
pause
java -jar JavaAutoDeployClient-1.1.jar config.xml

```

maven-package.bat

```
mvn clean package -Pprod

```


### 用代码自定义 加入lib JavaAutoDeployClient-1.1.jar

- 代码例子

```
    public static void main(String args[]){


        Map<String,String> uploadMap = new HashMap<String,String>();
        uploadMap.put("c:\\test.jar","/home");

        List<String> commands = new ArrayList<String>();
        commands.add("/home/restart.sh");

        List<HttpMethod> apis =new ArrayList<HttpMethod>();
        HttpGet httpGet = new HttpGet();
        httpGet.setUrl("http://xxxx/app/info");
        apis.add(httpGet);

        HttpPost httpPost = new HttpPost();
        httpPost.setUrl("http://xxxx/app/info");
        Map<String,String> params = new HashMap<String,String>();
        params.put("key","value");
        httpPost.setParams(params);

        apis.add(httpPost);

        AutoDeploy autoDeploy = AutoDeplyBuilder.create().
                setServerInfo("192.168.0.1","root","123456").
                setUploadFileInfo(uploadMap).
                setCommands(commands).
                setVerifyApi(apis).
                build();

        try {
            autoDeploy.start(new AutoDeploy.AutoDeployListener() {
                @Override
                public void finish() {

                }

                @Override
                public void verifySucess(List<String> log) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


```




