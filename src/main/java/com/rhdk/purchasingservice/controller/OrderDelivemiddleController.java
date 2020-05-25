package com.rhdk.purchasingservice.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.ExcleUtils;
import com.rhdk.purchasingservice.common.utils.FileUtil;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.TokenUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.AssetServiceFeign;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderDelivemiddleVO;
import com.rhdk.purchasingservice.service.IOrderDelivemiddleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(OrderDelivemiddleController.class);

    @Autowired
    private AssetServiceFeign assetServiceFeign;


    @ApiOperation(value = "送货记录明细中间表列表分页查询", notes = "送货记录明细中间表API")
    @RequestMapping(value = "/searchOrderDelivemiddleListPage", method = RequestMethod.POST)
    public ResponseEnvelope<IPage<OrderDelivemiddleVO>> searchOrderDelivemiddleListPage(@RequestBody OrderDelivemiddleQuery dto) {
        return iOrderDelivemiddleService.searchOrderDelivemiddleListPage(dto);
    }

    @ApiOperation(value = "送货记录明细中间表详细查询", notes = "送货记录明细中间表API")
    @RequestMapping(value = "/searchOrderDelivemiddleOne", method = RequestMethod.POST)
    public ResponseEnvelope<OrderDelivemiddleVO> searchOrderDelivemiddleOne(Long id) {
        return iOrderDelivemiddleService.searchOrderDelivemiddleOne(id);
    }

    @ApiOperation(value = "送货记录明细中间表添加", notes = "送货记录明细中间表API")
    @RequestMapping(value = "/addOrderDelivemiddle", method = RequestMethod.POST)
    public ResponseEnvelope addOrderDelivemiddle(@RequestBody OrderDelivemiddleDTO dto) throws Exception {
        return iOrderDelivemiddleService.addOrderDelivemiddle(dto);
    }


    @ApiOperation(value = "上传明细附件并检查文件信息是否正确", notes = "上传明细附件并检查文件信息是否正确")
    @RequestMapping(value = "/uploadFileCheck", method = RequestMethod.POST)
    public ResponseEnvelope uploadFileCheck(@NotNull MultipartFile file, Long moduleId) throws Exception{
        return iOrderDelivemiddleService.uploadFileCheck(file,moduleId);
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


    @ApiOperation(value = "送货明细附件删除", notes = "送货记录明细中间表API")
    @RequestMapping(value = "/deleteDetailFile", method = RequestMethod.POST)
    public ResponseEnvelope deleteDetailFile(@RequestBody OrderDelivemiddleDTO dto) throws Exception{
        return iOrderDelivemiddleService.deleteDetailFile(dto);
    }

}
