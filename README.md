# JavaAutoDeployClient

java auto deploy client with ssh protocol


dowload [JavaAutoDeployClient-1.0.jar](https://github.com/yale8848/JavaAutoDeployClient/blob/master/release/1.0/JavaAutoDeployClient-1.0.jar?raw=true)

# usage

### use bat way

- create config.xml

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
                    <local>C:\test.jar</local>
                    <remote>/home</remote>
                </upload>
            </uploads>
            <commands>
                <command>/home/restart.sh</command>
            </commands>
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
        </server>

    </servers>

</config>

```

- bat command

```

java -jar JavaAutoDeployClient-1.0.jar config.xml


```

- upload war to tomcat demo config.xml

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
        </server>
    </servers>
</config>



```

- maven package demo


autodeploy.bat

```
call maven-package.bat
pause
java -jar JavaAutoDeployClient-1.0.jar config.xml

```

maven-package.bat

```
mvn clean package -Pprod

```


### use code way

- code demo

```
    public static void main(String args[]){

        Map<String,String> uploadMap = new HashMap<String,String>();
        uploadMap.put("c:\\test.jar","/home");

        List<String> commands = new ArrayList<String>();
        commands.add("/home/restart.sh");

        AutoDeploy autoDeploy = AutoDeplyBuilder.create().
                setServerInfo("192.168.0.1","root","123456").
                setUploadFileInfo(uploadMap).
                setCommands(commands).build();

        try {
            autoDeploy.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


```




