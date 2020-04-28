package com.rhdk.purchasingservice.utils;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public class ExcleUtils {

  @Autowired private ImportParams importParams;

  public static Workbook importExcle(MultipartFile file) {

    Workbook workBook = null;
    try {
      InputStream is = file.getInputStream();
      if (file.getOriginalFilename().endsWith("xls")) {
        // 2003
        workBook = new HSSFWorkbook(is);
      } else if (file.getOriginalFilename().endsWith("xlsx")) {
        // 2007
        workBook = new XSSFWorkbook(is);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return workBook;
  }

  public static void main(String[] args) {}
}
