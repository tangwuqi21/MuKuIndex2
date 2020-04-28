package com.rhdk.purchasingservice.common.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Export {
  private static Logger logger = LoggerFactory.getLogger(Export.class);

  public static void expBoiler(
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
