package ren.yale.java.autodeploy.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Yale on 2016/12/20.
 */
public class HttpClient {


    public static String get(String url) {

        StringBuffer sb = new StringBuffer();
        try{
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);

            connection.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
            String line ="";
            while ((line = br.readLine())!=null){
                sb.append(line);
            }
            br.close();

        }catch (Exception e){

        }
        return sb.toString();
    }

    public static String post(String url,Map<String,String> map){

        StringBuffer sb = new StringBuffer();
        try{
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();

            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            StringBuffer params = new StringBuffer();

            if (map!=null){
                for (Map.Entry<String,String> element : map.entrySet()) {
                    params.append(element.getKey());
                    params.append("=");
                    params.append(element.getValue());
                    params.append("&");
                }
                if (params.length()>0){
                    params.deleteCharAt(params.length()-1);
                }
            }

            connection.setRequestProperty("Content-Length",
                    String.valueOf(params.length()));

            connection.connect();
            PrintWriter printWriter = new PrintWriter(connection.getOutputStream());;
            printWriter.write(params.toString());
            printWriter.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader responseReader;
                responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String line ="";
                while ((line = responseReader.readLine()) != null) {
                    sb.append(line);
                }
                responseReader.close();
            }

        }catch (Exception e){

        }
        return sb.toString();

    }
}
