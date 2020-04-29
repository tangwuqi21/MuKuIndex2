package com.rhdk.purchasingservice.common.utils;

import java.io.File;

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

    public static void main(String[] args) {
        renameFile("E:/attachments/document/MTU3OTI0NTAzNzc3OS0lRTYlQjUlOEIlRTglQUYlOTUlMjAlRTQlQkIlOTglRTYlQUMlQkUlRTclOTQlQjMlRTglQUYlQjclRTUlOEQlOTU=.doc","E:/attachments/document/1579245037779-测试 付款申请单.doc");
        }

    }
