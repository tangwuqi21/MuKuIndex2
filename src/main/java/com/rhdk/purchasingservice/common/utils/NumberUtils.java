package com.rhdk.purchasingservice.common.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
}
