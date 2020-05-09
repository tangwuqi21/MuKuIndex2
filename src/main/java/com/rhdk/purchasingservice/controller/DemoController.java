package com.rhdk.purchasingservice.controller;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.rhdk.purchasingservice.common.exception.RequestEmptyException;
import com.rhdk.purchasingservice.common.utils.FileUtil;
import com.rhdk.purchasingservice.common.utils.MsgClient;
import com.rhdk.purchasingservice.common.utils.TokenUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.IDemoFeign;
import com.rhdk.purchasingservice.pojo.vo.DemoVo;
import com.rhdk.purchasingservice.service.DemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.*;

/**
 * 测试 前端控制器
 *
 * @author LMYOU
 * @since 2020-04-27
 */
@Api(tags = {"测试API"})
@Controller
@RequestMapping("/demoService")
@ResponseBody
public class DemoController {

  @Autowired
  private DemoService demoService;

  @Autowired
  private IDemoFeign demoFeign;

  private static org.slf4j.Logger logger = LoggerFactory.getLogger(DemoController.class);

  @Resource
  private Environment env;

  private String folder;

  @ApiOperation(value = "Web端巡查主题列表分页查询", notes = "巡查主题API")
  @RequestMapping(value = "/searchList", method = RequestMethod.POST)
  public ResponseEnvelope searchTPatrolThemeListPageWeb(@RequestBody DemoVo dto) {
    return demoService.searchTPatrolThemeListPage(dto);
  }


  /**
   * fegin调用远程RPC服务用例
   * @param dto
   * @return
   */
  @ApiOperation(value = "fegin调用远程RPC服务用例", notes = "fegin调用远程RPC服务用例")
  @RequestMapping(value = "/getFeginRpcTestData", method = RequestMethod.POST)
  public ResponseEnvelope getFeginRpcTestData(@RequestBody DemoVo dto) {
    return demoFeign.searchDemoListPage(dto, TokenUtil.getToken());
  }

  @ApiOperation(value = "上传附件", notes = "上传附件")
  @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
  public String uploadFile(HttpServletRequest request, @NotNull MultipartFile file) {
    logger.info("上传附件名: {}", file.getOriginalFilename());
   // Long userId = GetUser.getUser(request, env.getProperty("authorUrl.accountUrl")).getId();
    Long userId =588889L;
    //上传附件到指定的文件地址
    folder = env.getProperty("upload.path") + userId;
    if (ObjectUtils.isEmpty(file)) {
      throw new RequestEmptyException("上传的附件为空");
    }
    String certificateFile = null;
    try {
      certificateFile = file.getOriginalFilename();
      String cert = FileUtil.getExtensionName(certificateFile);
      //获取文件扩展名
      certificateFile = folder + '/' + System.currentTimeMillis() + "." + cert;
      File localFile = new File(certificateFile);
      FileUtil.createFile(localFile, FileUtil.getSysName(System.getProperties().getProperty("os.name")));
      file.transferTo(localFile);
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("上传附件错误");
    }
    return certificateFile;
  }



  @ApiOperation(value = "上传Excel并读取内容", notes = "上传Excel并读取内容")
  @RequestMapping(value = "/readImportExcel", method = RequestMethod.POST)
  public String readImportExcel(HttpServletRequest request, @NotNull MultipartFile file) {
    String certificateFile=null;
    File toFile =null;
    try {
      toFile = FileUtil.multipartFileToFile(file);
      Date start = new Date();
      logger.debug("start");
      ImportParams params = new ImportParams();
      params.setTitleRows(1);
      List<MsgClient> result = ExcelImportUtil.importExcel(toFile, MsgClient.class,params);
      logger.debug("end,time is {}", ((new Date().getTime() - start.getTime()) / 1000));
      testMerge(result);
     // System.out.println(result.get(0).getClientName()+result.get(0).getClientPhone());
    } catch (Exception e) {
    }finally {
      //删除产生的多余文件
      if(toFile!=null){
        toFile.delete();
      }
    }
    return certificateFile;
  }


  /**
   * 根据结果来生成指定内容的Excel
   * @param result
   */
  public void testMerge( List<MsgClient> result) {
    try {
      List<ExcelExportEntity> entity = new ArrayList<ExcelExportEntity>();
      ExcelExportEntity excelentity = new ExcelExportEntity("姓名", "name");
      excelentity.setMergeVertical(true);
      entity.add(excelentity);
      excelentity = new ExcelExportEntity("电话", "phone");
      excelentity.setMergeVertical(true);
      excelentity.setMergeRely(new int[] { 1 });
      entity.add(excelentity);
      List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
      Map<String, Object> map;
      for (MsgClient model:result) {
        map = new HashMap<String, Object>();
        map.put("name",model.getClientName());
        map.put("phone", model.getClientPhone());
        list.add(map);
      }
      Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("员工通讯录", "通讯录"),
              entity, list);
      FileOutputStream fos = new FileOutputStream("D:/ExcelExportForMap.xls");
      workbook.write(fos);
      fos.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
