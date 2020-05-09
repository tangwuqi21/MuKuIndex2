package com.rhdk.purchasingservice.common.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcleUtils {

  @Autowired
  private ImportParams importParams;
  private static Logger logger = LoggerFactory.getLogger(ExcleUtils.class);
  /**
   * apache poi 操作上传Excel
   * @param file
   * @return
   */
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

  /**
   *  根据指定模板来导出Excel的数据内容
   * @param response
   * @param fileName
   * @param list
   */
  public static void exportByTemplate(
          HttpServletResponse response, String fileName, List<Map<String, Object>> list) {
    try {
      Map<String, Object> map = new HashMap<>();
      // 获取模板
      TemplateExportParams params = new TemplateExportParams("doc/attachment.xlsx", 0);
      // 获取数据源
      logger.info("文件导出");
      map.put(fileName, list);
      // 写入模板
      Workbook workbook = ExcelExportUtil.exportExcel(params, map);
      response.setHeader(
              "Content-Disposition",
              "attachment; filename=" + URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
      ServletOutputStream out = response.getOutputStream();
      workbook.write(out);
      out.flush();
    } catch (Exception e) {
      e.getStackTrace();
      logger.error("文件导出错:{}", e.getMessage());
    }
  }
}
