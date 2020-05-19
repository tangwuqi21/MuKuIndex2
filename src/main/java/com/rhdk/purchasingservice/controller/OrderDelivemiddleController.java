package com.rhdk.purchasingservice.controller;


import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.*;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.AssetServiceFeign;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import com.rhdk.purchasingservice.service.IOrderDelivemiddleService;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;


/**
 * <p>
 * 送货记录明细中间表 前端控制器
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-13
 */
@Slf4j
@Api(tags = {"送货记录明细中间表API"})
@Controller
@RequestMapping("/purchasingservice/orderDelivemiddle")
@ResponseBody
public class OrderDelivemiddleController {

    @Autowired
    private IOrderDelivemiddleService iOrderDelivemiddleService;

    @Autowired
    private AssetServiceFeign assetServiceFeign;


    @ApiOperation(value = "送货记录明细中间表列表分页查询", notes = "送货记录明细中间表API")
    @RequestMapping(value = "/searchOrderDelivemiddleListPage", method = RequestMethod.POST)
    public ResponseEnvelope searchOrderDelivemiddleListPage(@RequestBody OrderDelivemiddleQuery dto) {
        return iOrderDelivemiddleService.searchOrderDelivemiddleListPage(dto);
    }

    @ApiOperation(value = "送货记录明细中间表详细查询", notes = "送货记录明细中间表API")
    @RequestMapping(value = "/searchOrderDelivemiddleOne", method = RequestMethod.POST)
    public ResponseEnvelope searchOrderDelivemiddleOne(Long id) {
        return iOrderDelivemiddleService.searchOrderDelivemiddleOne(id);
    }

    @ApiOperation(value = "送货记录明细中间表添加", notes = "送货记录明细中间表API")
    @RequestMapping(value = "/addOrderDelivemiddle", method = RequestMethod.POST)
    public ResponseEnvelope addOrderDelivemiddle(@RequestBody OrderDelivemiddleDTO dto) throws Exception {
        return iOrderDelivemiddleService.addOrderDelivemiddle(dto);
    }


    @ApiOperation(value = "上传明细附件并检查文件信息是否正确", notes = "上传明细附件并检查文件信息是否正确")
    @RequestMapping(value = "/uploadFileCheck", method = RequestMethod.POST)
    public ResponseEnvelope uploadFileCheck(@NotNull MultipartFile file, Long moduleId) {
        //解析上传的Excel，并检查文件格式和内容是否正确
        Map<String,Object> resultMap=new HashMap<>();
        String fileUrl = null;
        try {
            // 创建excel工作簿对象
            Workbook workbook = null;
            FormulaEvaluator formulaEvaluator = null;
            File excelFile = FileUtil.multipartFileToFile(file);
            InputStream is = new FileInputStream(excelFile);
            // 判断文件是xlsx还是xls
            if (excelFile.getName().endsWith("xlsx")) {
                workbook = new XSSFWorkbook(is);
                formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
            } else {
                workbook = new HSSFWorkbook(is);
                formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
            }
            //判断excel文件打开是否正确
            if (workbook == null) {
                return ResultVOUtil.returnFail(ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
            }
            //获取资产模板对应的个性表头信息
            List<Map<String, Object>> titleMap = iOrderDelivemiddleService.getTitleMap(moduleId);
            //获取表头的下标
            Map<Integer, String> titleNameM = new HashMap<>();
            Map<String, Integer> titleNameI = new HashMap<>();
            List<Integer> collList=new ArrayList<>();
            for (int conum=0;conum<titleMap.size();conum++) {
                titleNameM.put(conum, titleMap.get(conum).get("NAME").toString());
                titleNameI.put(titleMap.get(conum).get("NAME").toString(),conum);
                if(titleMap.get(conum).get("PK_FLAG")!=null && Integer.valueOf(titleMap.get(conum).get("PK_FLAG").toString())==1){
                    collList.add(conum);
                }
            }
            Sheet sheet = workbook.getSheetAt(0);
            //当前sheet页面为空,继续遍历
            if (sheet == null) {
                return ResultVOUtil.returnFail(ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
            }
            //1.读取Excel的表头数据,比较模板是否一致
            Row row1 = sheet.getRow(0);
            boolean isExcel = true;
            //比较表头大小是否一致
            if(titleNameM.size()!=row1.getLastCellNum()){
                return ResultVOUtil.returnFail(ResultEnum.TEMPLATE_NOTFORMAT.getCode(), ResultEnum.TEMPLATE_NOTFORMAT.getMessage());
            }
            //比较表头内容是否一致
            for (int columnNum = 0; columnNum < row1.getLastCellNum(); columnNum++) {
                if (!titleNameM.get(columnNum).equals(ExcleUtils.getValue(row1.getCell(columnNum), formulaEvaluator))) {
                    isExcel = false;
                    break;
                }
            }
            if (!isExcel) {
                return ResultVOUtil.returnFail(ResultEnum.TEMPLATE_NOTFORMAT.getCode(), ResultEnum.TEMPLATE_NOTFORMAT.getMessage());
            }
            // 2.检查是否存在空列值
            // 对于每个sheet，读取其中的每一行,从第二行开始读取
            resultMap.put("totalRow",sheet.getLastRowNum());
            Set<String> collSet=new HashSet<String>();
            boolean isRowNull = true;
            boolean isCellNull = true;
            boolean isDataT = true;
            int rowNo=0;
            String cellMsg="";
            String unitValue="";
            String priceValue="";
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    rowNo=rowNum+1;
                    isRowNull = false;
                    break;
                }
                //入库每条资产实体对应的属性值（个性化的，不是共有的,从第二列开始）
                String collStr="";
                for (int columnNum = 0; columnNum < row.getLastCellNum(); columnNum++) {
                  String cellValue=ExcleUtils.getValue(row.getCell(columnNum), formulaEvaluator);
                    if (StringUtils.isEmpty(cellValue)) {
                        isCellNull = false;
                        cellMsg="第"+(rowNum+1)+"行，第"+(columnNum+1)+"列";
                        break;
                    }
                    // 3.检查Excel中是否存在重复行，根据数据库中模板属性pk_flag取值
                    if(collList.contains(columnNum)){
                        collStr +=cellValue;
                    }
                }

                if(!collSet.add(collStr)){
                    isDataT = false;
                    rowNo=rowNum+1;
                    break;
                }

                //判断模板上传是否包含单位和单价两个属性，如果有则以模板中的单位和单价值为准，如果无，则以库中的模板单位单价值为准
                if(titleNameI.get("单位")!=null){
                    unitValue=ExcleUtils.getValue(row.getCell(titleNameI.get("单位")), formulaEvaluator);
                }
                if(titleNameI.get("单价")!=null){
                    priceValue=ExcleUtils.getValue(row.getCell(titleNameI.get("单价")), formulaEvaluator);
                }
            }
            if (!isRowNull) {
                return ResultVOUtil.returnFail(ResultEnum.TEMPLATE_CELLNULL.getCode(), "附件第"+rowNo+"行数据内容为空");
            }
            if (!isCellNull) {
                return ResultVOUtil.returnFail(ResultEnum.TEMPLATE_CELLNULL.getCode(), "附件"+cellMsg+"数据内容为空");
            }
            if (!isDataT) {
                return ResultVOUtil.returnFail(ResultEnum.TEMPLATE_ROWTWO.getCode(), "附件第"+rowNo+"行数据内容有重复");
            }
            //远程调用附件上传接口
            fileUrl = assetServiceFeign.uploadSingleFile(file, TokenUtil.getToken());
            resultMap.put("unitValue",unitValue);
            resultMap.put("priceValue",priceValue);
            resultMap.put("fileUrl",fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultVOUtil.returnSuccess(resultMap);
    }


    @ApiOperation(value = "送货单明细更新", notes = "送货记录明细中间表API")
    @RequestMapping(value = "/updateOrderMiddle", method = RequestMethod.POST)
    public ResponseEnvelope updateOrderMiddle(@RequestBody OrderDelivemiddleDTO dto) throws Exception{
        return iOrderDelivemiddleService.updateOrderMiddle(dto);
    }

    @ApiOperation(value = "送货明细删除", notes = "送货记录明细中间表API")
    @RequestMapping(value = "/deleteOrderDetailrecords", method = RequestMethod.POST)
    public ResponseEnvelope deleteOrderDetailrecords(Long id) throws Exception{
        return iOrderDelivemiddleService.deleteOrderDetailrecords(id);
    }
}
