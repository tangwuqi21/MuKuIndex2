package com.rhdk.purchasingservice.common.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Description: 扩展名称操作
 * @Author:
 * @CreateDate: 2020/01/16
 */
public class FileUtil {

    /**
     * @Description: Java文件操作 获取文件扩展名
     * @Author:
     * @CreateDate: 2020/01/16
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * @Description: Java文件操作 获取不带扩展名的文件名
     * @Author:
     * @CreateDate: 2020/01/16
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static void delFolder(String folderPath) {
        try {
            // 删除完里面所有内容
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            // 删除空文件夹
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }

        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {

            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }

        return dirFile.delete();
    }


    /***
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                // 先删除文件夹里面的文件
                delAllFile(path + "/" + tempList[i]);
                // 再删除空文件夹
                delFolder(path + "/" + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }


    /**
    * @Description:   重命名
    * @Author:
    * @CreateDate:     2020/01/17
    */
    public static void renameFile(String oldFileName, String newFileName) {
        File oldFile = new File(oldFileName);
        File newFile = new File(newFileName);
        oldFile.renameTo(newFile);

    }

    /**
     * @Description: 更改扩展名
     * @Author:
     * @CreateDate: 2020/01/13
     */
    public static String getPdfName(String fi) {
        String pdfName = null;
        if (fi.endsWith(".docx")) {
            pdfName = fi.replace("docx", "pdf");
        } else if (fi.endsWith(".doc")) {
            pdfName = fi.substring(0, fi.length() - 3) + "pdf";
        } else if (fi.endsWith(".xlsx")) {
            pdfName = fi.replace("xlsx", "pdf");
        } else if (fi.endsWith(".xls")) {
            pdfName = fi.replace("xls", "pdf");
        } else {
            return fi;
        }
        return pdfName;
    }


    /**
     * 返回文件类型
     * @param fileName 传入文件名。
     * @return 返回类型
     */
    public static String subType(String fileName){
        try{
            String type=fileName.substring(fileName.lastIndexOf("."));
            return type;
        }catch(Exception e){
            return fileName;
        }
    }
    /**
     * 创建文件目录
     * @param filePath 文件对象
     * @param SystemType 文件系统类型，可选值有windows和linux;
     */
    public static void createFile(File filePath,String SystemType){
        try{
            String absPath = filePath.getAbsolutePath();
            String mkdir = "";
            if("windows".equals(SystemType)) {
                mkdir = absPath.substring(0,absPath.lastIndexOf("\\"));
            }
            else if("linux".equals(SystemType)) {
                mkdir = absPath.substring(0,absPath.lastIndexOf("/"));
            }else {
                throw new IllegalArgumentException("SystemType可选择值为:windows和linux请检查配置文件中SystemName的取值是否正确");
            }
            File fileDir=new File(mkdir);
            if(!fileDir.exists()){
                fileDir.mkdirs();
            }
        }catch(Exception e){

        }
    }

    /**
     * @Description: 判断系统
     * @Author:
     * @CreateDate: 2020/03/03
     */
    public static String getSysName(String osName) {
        if (File.separator.equals("/")) {
            return "linux";
        } else {
            return "windows";
        }
    }


    /**
     * 用于创建文件及数据库文件存储
     * @param fileObj Controller或者是SpringMVC接收到的文件对象
     * @param pathHead 该文件存储的磁盘为位置如：/、/test或者/test/test 通常来源与配置文件 使用时最后一级目录不要存在'/'
     * 目录分层时切记不要使用'\'
     * @param id 该文件的唯一表示符号
     * @param belongTo 该文件属于那一个对象
     * @param systemType 系统类型,取值只能是windows或者linux 通常来源为配置文件
     * @return 返回一个路径
     * @throws Exception 文件不能写入，发生该异常，在参数正确传值情况下，通常是因为配置文件配置错误了或者使用了'\'
     */
    public static String createAndGetPath(MultipartFile fileObj, String pathHead, String id, String belongTo, String systemType){
        try {
            String currTime=System.currentTimeMillis()+"";
            String fileType = subType(fileObj.getOriginalFilename());
            File file_b=new File(pathHead+"/"+id+"_"+belongTo+"_"+currTime+fileType);
            createFile(file_b,systemType);
            fileObj.transferTo(file_b);
            return pathHead+"/"+id+"_"+belongTo+"_"+currTime+fileType;
        }catch(Exception e) {
            return null;
        }
    }


    /**
     *  前端输入的文件转换为io文件
     * @param  file
     * @return
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            try {
                InputStream ins = null;
                ins = file.getInputStream();
                toFile = new File(file.getOriginalFilename());
                inputStreamToFile(ins, toFile);
                ins.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toFile;
    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        renameFile("E:/attachments/document/MTU3OTI0NTAzNzc3OS0lRTYlQjUlOEIlRTglQUYlOTUlMjAlRTQlQkIlOTglRTYlQUMlQkUlRTclOTQlQjMlRTglQUYlQjclRTUlOEQlOTU=.doc","E:/attachments/document/1579245037779-测试 付款申请单.doc");
        }

    }
