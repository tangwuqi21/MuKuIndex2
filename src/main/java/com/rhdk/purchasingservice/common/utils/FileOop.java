package com.rhdk.purchasingservice.common.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileOop {
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
}
