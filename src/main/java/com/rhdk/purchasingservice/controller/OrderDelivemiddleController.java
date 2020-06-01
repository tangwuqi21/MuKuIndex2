package com.rhdk.purchasingservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 送货记录明细中间表 前端控制器
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

  @Autowired private IOrderDelivemiddleService iOrderDelivemiddleService;

  @Autowired private AssetServiceFeign assetServiceFeign;

  /**
   * 送货记录下的明细列表查询，返回送货明细基本信息 关联的送货单基本信息
   *
   * @param dto
   * @return
   */
  @ApiOperation(value = "送货记录明细中间表列表分页查询", notes = "送货记录明细中间表API")
  @RequestMapping(value = "/searchOrderDelivemiddleListPage", method = RequestMethod.POST)
  public ResponseEnvelope<IPage<OrderDelivemiddleVO>> searchOrderDelivemiddleListPage(
      @RequestBody OrderDelivemiddleQuery dto) {
    return iOrderDelivemiddleService.searchOrderDelivemiddleListPage(dto);
  }

  /**
   * 根据送货记录下明细id来查询送货记录下单一明细的详情
   *
   * @param id
   * @return
   */
  @ApiOperation(value = "送货记录明细中间表详细查询", notes = "送货记录明细中间表API")
  @RequestMapping(value = "/searchOrderDelivemiddleOne", method = RequestMethod.POST)
  public ResponseEnvelope<OrderDelivemiddleVO> searchOrderDelivemiddleOne(Long id) {
    return iOrderDelivemiddleService.searchOrderDelivemiddleOne(id);
  }

  /**
   * 送货记录下，添加单一明细信息
   *
   * @param dto
   * @return
   * @throws Exception
   */
  @ApiOperation(value = "送货记录明细中间表添加", notes = "送货记录明细中间表API")
  @RequestMapping(value = "/addOrderDelivemiddle", method = RequestMethod.POST)
  public ResponseEnvelope addOrderDelivemiddle(@RequestBody OrderDelivemiddleDTO dto) {
    try {
      return ResultVOUtil.returnSuccess(iOrderDelivemiddleService.addOrderDelivemiddle(dto));
    } catch (Exception e) {
      return ResultVOUtil.returnFail(ResultEnum.FAIL.getCode(), e.getMessage());
    }
  }

  /**
   * 送货记录下，添加单一明细附件，对附件内容进行格式校验，数据校验。 并将校验玩的数据进行暂存入库
   *
   * @param file
   * @param moduleId
   * @return
   * @throws Exception
   */
  @ApiOperation(value = "上传明细附件并检查文件信息是否正确", notes = "上传明细附件并检查文件信息是否正确")
  @RequestMapping(value = "/uploadFileCheck", method = RequestMethod.POST)
  public ResponseEnvelope uploadFileCheck(@NotNull MultipartFile file, Long moduleId) {
    try {
      return iOrderDelivemiddleService.uploadFileCheck(file, moduleId);
    } catch (Exception e) {
      return ResultVOUtil.returnFail(ResultEnum.FAIL.getCode(), e.getMessage());
    }
  }

  /**
   * 送货记录下，更新单一明细记录基本信息，包括更换明细附件，更换明细资产模板
   *
   * @param dto
   * @return
   * @throws Exception
   */
  @ApiOperation(value = "送货单明细更新", notes = "送货记录明细中间表API")
  @RequestMapping(value = "/updateOrderMiddle", method = RequestMethod.POST)
  public ResponseEnvelope updateOrderMiddle(@RequestBody OrderDelivemiddleDTO dto) {
    try {
      return iOrderDelivemiddleService.updateOrderMiddle(dto);
    } catch (Exception e) {
      return ResultVOUtil.returnFail(ResultEnum.FAIL.getCode(), e.getMessage());
    }
  }

  /**
   * 根据送货明细id来删除送货记录下单一明细
   *
   * @param id
   * @return
   * @throws Exception
   */
  @ApiOperation(value = "送货明细删除", notes = "送货记录明细中间表API")
  @RequestMapping(value = "/deleteOrderDetailrecords", method = RequestMethod.POST)
  public ResponseEnvelope deleteOrderDetailrecords(Long id) {
    try {
      return iOrderDelivemiddleService.deleteOrderDetailrecords(id);
    } catch (Exception e) {
      return ResultVOUtil.returnFail(ResultEnum.FAIL.getCode(), e.getMessage());
    }
  }

  /**
   * 根据送货记录下明细资产id集合来删除送货记录下单一明细的附件信息
   *
   * @param assetIds
   * @return
   * @throws Exception
   */
  @ApiOperation(value = "送货明细附件删除", notes = "送货记录明细中间表API")
  @RequestMapping(value = "/deleteDetailFile", method = RequestMethod.POST)
  public ResponseEnvelope deleteDetailFile(String assetIds) {
    try {
      return iOrderDelivemiddleService.deleteDetailFile(assetIds);
    } catch (Exception e) {
      return ResultVOUtil.returnFail(ResultEnum.FAIL.getCode(), e.getMessage());
    }
  }

  /**
   * 导出送货单明细记录列表数据
   *
   * @param dto
   * @return
   */
  @ApiOperation(value = "导出送货单明细记录列表数据", notes = "送货记录明细中间表API")
  @RequestMapping(value = "/exportDeliveDetailList", method = RequestMethod.POST)
  public void exportDeliveDetailList(
      HttpServletResponse response, @RequestBody OrderDelivemiddleQuery dto) {
    log.info("根据条件导出送货单明细记录列表数据");
    // ResponseEntity<byte[]> result = null;
    Map<String, Object> map = new HashMap<>();
    // 获取数据源
    List<OrderDelivemiddleVO> data = iOrderDelivemiddleService.getDeliverDetailList(dto);
    map.put("tempName", "deliverDetail");
    map.put("tempData", data);
    try {
      byte[] byteArr = assetServiceFeign.exportDataList(map, TokenUtil.getToken());
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment;filename=送货单明细记录.xlsx");
      response.getOutputStream().write(byteArr);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
