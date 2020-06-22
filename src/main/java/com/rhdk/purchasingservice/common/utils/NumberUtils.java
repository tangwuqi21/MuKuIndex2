package com.rhdk.purchasingservice.common.utils;

import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class NumberUtils {
  /**
   * @Description: @Author: YYF @CreateDate: 2020/4/21 13:41 求百分比，保留一位小数
   *
   * @param num: 除数
   * @param totle：被除数
   */
  public static String getPercentage(Double num, Double totle) {
    DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
    // 可以设置精确几位小数
    df.setMaximumFractionDigits(1);
    Double accuracy_num = num / totle * 100;
    return df.format(accuracy_num) + "%";
  }

  /**
   * 生成业务编码
   *
   * @param type
   * @return
   */
  public static String createCode(String type) {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    return type + sdf.format(date) + String.format("%03d", new Random().nextInt(999));
  }

  /**
   * 千分位方法
   *
   * @param text
   * @return
   */
  public static String fmtMicrometer(String text) {
    DecimalFormat df = null;
    if (!StringUtils.isEmpty(text) && text.indexOf(".") > 0) {
      if (text.length() - text.indexOf(".") - 1 == 0) {
        df = new DecimalFormat("###,##0.");
      } else if (text.length() - text.indexOf(".") - 1 == 1) {
        df = new DecimalFormat("###,##0.0");
      } else {
        df = new DecimalFormat("###,##0.00");
      }
    } else {
      df = new DecimalFormat("###,##0.00");
    }
    double number = 0.0;
    try {
      number = Double.parseDouble(text);
    } catch (Exception e) {
      number = 0.0;
    }
    return df.format(number);
  }

  /**
   * 保留两位小数方法
   *
   * @param text
   * @return
   */
  public static String fmtTwo(String text) {
    DecimalFormat df = new DecimalFormat("##0.00");
    double number = 0.00;
    try {
      if (!StringUtils.isEmpty(text)) {
        number = Double.parseDouble(text);
      }
    } catch (Exception e) {
      number = 0.00;
    }
    return df.format(number);
  }
}
