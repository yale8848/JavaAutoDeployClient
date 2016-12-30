package ren.yale.java.autodeploy.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Yale on 2016/12/30.
 */
public class FileUtils {

    private static void writeZip(File file, String parentPath, ZipOutputStream zos) throws Exception{
        if(file.exists()){
            if(file.isDirectory()){//处理文件夹
                parentPath+=file.getName()+File.separator;
                File [] files=file.listFiles();
                for(File f:files){
                    writeZip(f, parentPath, zos);
                }
            }else{
                FileInputStream fis=null;


                fis=new FileInputStream(file);
                ZipEntry ze = new ZipEntry(parentPath + file.getName());
                zos.putNextEntry(ze);
                byte [] content=new byte[1024];
                int len;
                while((len=fis.read(content))!=-1){
                    zos.write(content,0,len);
                    zos.flush();
                }

                if(fis!=null){
                    fis.close();
                }
            }
        }
    }


    public static String getZipName(String filePath){

        StringBuffer sb  = new StringBuffer();
        sb.append(filePath);
        if (filePath.endsWith("\\")||filePath.endsWith("/")){
            sb.deleteCharAt(filePath.length()-1);
        }
        sb.append(".zip");
        return sb.toString();

    }
    public static boolean isDir(String filePath){
        File file = new File(filePath);
        return file.isDirectory();
    }
    public static boolean isFileExsit(String filePath){
        File file = new File(filePath);
        return file.exists();
    }
    public static void zipDir(String sourcePath,String destPath) throws Exception{
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        fos = new FileOutputStream(destPath);
        zos = new ZipOutputStream(fos);
        writeZip(new File(sourcePath), "", zos);
        if (zos != null) {
            zos.close();
        }
    }

}
