package com.rhdk.purchasingservice.common.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
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
import java.text.SimpleDateFormat;
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


  /**
   * 获取单元格的内容
   * @param cell
   * @param formulaEvaluator
   * @return
   */
  public static String getValue(Cell cell, FormulaEvaluator formulaEvaluator) {
    if (cell == null) {
      return null;
    }
    switch (cell.getCellType()) {
      case Cell.CELL_TYPE_STRING:
        return cell.getRichStringCellValue().getString();
      case Cell.CELL_TYPE_NUMERIC:
        // 判断是日期时间类型还是数值类型
        if (DateUtil.isCellDateFormatted(cell)) {
          short format = cell.getCellStyle().getDataFormat();
          SimpleDateFormat sdf = null;
          /* 所有日期格式都可以通过getDataFormat()值来判断
           *     yyyy-MM-dd----- 14
           *    yyyy年m月d日----- 31
           *    yyyy年m月--------57
           *    m月d日  --------- 58
           *    HH:mm---------- 20
           *    h时mm分  --------- 32
           */
          if (format == 14 || format == 31 || format == 57 || format == 58) {
            //日期
            sdf = new SimpleDateFormat("yyyy-MM-dd");
          } else if (format == 20 || format == 32) {
            //时间
            sdf = new SimpleDateFormat("HH:mm");
          }
          return sdf.format(cell.getDateCellValue());
        } else {
          // 对整数进行判断处理
          double cur = cell.getNumericCellValue();
          long longVal = Math.round(cur);
          Object inputValue = null;
          if (Double.parseDouble(longVal + ".0") == cur) {
            inputValue = longVal;
          } else {
            inputValue = cur;
          }
          return String.valueOf(inputValue);
        }
      case Cell.CELL_TYPE_BOOLEAN:
        return String.valueOf(cell.getBooleanCellValue());
      case Cell.CELL_TYPE_FORMULA:
        //对公式进行处理,返回公式计算后的值,使用cell.getCellFormula()只会返回公式
        return String.valueOf(formulaEvaluator.evaluate(cell).getNumberValue());
      //Cell.CELL_TYPE_BLANK || Cell.CELL_TYPE_ERROR
      default:
        return null;
    }
  }
}
