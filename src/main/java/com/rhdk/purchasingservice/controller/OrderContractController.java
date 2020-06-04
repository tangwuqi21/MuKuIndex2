package com.rhdk.purchasingservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.TokenUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.AssetServiceFeign;
import com.rhdk.purchasingservice.pojo.dto.OrderContractDTO;
import com.rhdk.purchasingservice.pojo.query.OrderContractQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;
import com.rhdk.purchasingservice.service.IOrderContractService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 合同表 前端控制器
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Slf4j
@Api(tags = {"合同表API"})
@Controller
@RequestMapping("/purchasingservice/orderContract")
@ResponseBody
public class OrderContractController {

  @Autowired private IOrderContractService iOrderContractService;

  @Autowired private AssetServiceFeign assetServiceFeign;

  /**
   * 采购管理-合同列表查询
   *
   * @param dto
   * @return
   */
  @ApiOperation(value = "合同表列表分页查询", notes = "合同表API")
  @RequestMapping(value = "/searchOrderContractListPage", method = RequestMethod.POST)
  public ResponseEnvelope<IPage<OrderContractVO>> searchOrderContractListPage(
      @RequestBody OrderContractQuery dto) {
    dto.setToken(TokenUtil.getToken());
    try {
      return ResultVOUtil.returnSuccess(
          iOrderContractService
              .searchOrderContractListPage(dto, TokenUtil.getUserInfo().getOrganizationId())
              .get(5, TimeUnit.SECONDS));
    } catch (Exception e) {
      return ResultVOUtil.returnFail(ResultEnum.FAIL.getCode(), e.getMessage());
    }
  }

  /**
   * 采购管理-合同明细查询
   *
   * @param id
   * @return
   */
  @ApiOperation(value = "合同表详细查询", notes = "合同表API")
  @RequestMapping(value = "/searchOrderContractOne", method = RequestMethod.POST)
  public ResponseEnvelope<OrderContractVO> searchOrderContractOne(Long id) {
    try {
      return iOrderContractService.searchOrderContractOne(id);
    } catch (Exception e) {
      return ResultVOUtil.returnFail(ResultEnum.FAIL.getCode(), e.getMessage());
    }
  }

  /**
   * 采购管理-合同添加
   *
   * @param dto
   * @return
   */
  @ApiOperation(value = "合同表添加", notes = "合同表API")
  @RequestMapping(value = "/addOrderContract", method = RequestMethod.POST)
  public ResponseEnvelope addOrderContract(@RequestBody @Valid OrderContractDTO dto) {
    return iOrderContractService.addOrderContract(dto);
  }

  /**
   * 采购管理-合同更新
   *
   * @param dto
   * @return
   */
  @ApiOperation(value = "合同表更新", notes = "合同表API")
  @RequestMapping(value = "/updateOrderContract", method = RequestMethod.POST)
  public ResponseEnvelope updateOrderContract(@RequestBody @Valid OrderContractDTO dto) {
    return iOrderContractService.updateOrderContract(dto);
  }

  /**
   * 导出合同列表数据
   *
   * @param dto
   * @return
   */
  @ApiOperation(value = "导出合同列表数据", notes = "合同表API")
  @RequestMapping(value = "/exportContractList", method = RequestMethod.POST)
  public void exportContractList(
      HttpServletResponse response, @RequestBody OrderContractQuery dto) {
    log.info("根据条件导出合同列表数据");
    Map<String, Object> map = new HashMap<>();
    // 获取数据源
    List<OrderContractVO> data = iOrderContractService.getContractInforList(dto);
    map.put("tempName", "contractInfo");
    map.put("tempData", data);
    try {
      byte[] byteArr = assetServiceFeign.exportDataList(map, TokenUtil.getToken());
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment;filename=采购合同.xlsx");
      response.getOutputStream().write(byteArr);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 根据采购合同id删除采购合同
   *
   * @param id
   * @return
   * @throws Exception
   */
  @ApiOperation(value = "合同明细删除", notes = "合同表API")
  @RequestMapping(value = "/deleteOrderContract", method = RequestMethod.POST)
  public ResponseEnvelope deleteOrderContract(Long id) {
    try {
      return iOrderContractService.deleteOrderContract(id);
    } catch (Exception e) {
      return ResultVOUtil.returnFail(ResultEnum.FAIL.getCode(), e.getMessage());
    }
  }

  /**
   * 根据采购合同ids来批量删除采购合同
   *
   * @param ids
   * @return
   * @throws Exception
   */
  @ApiOperation(value = "合同批量删除", notes = "合同表API")
  @RequestMapping(value = "/deleteContractList", method = RequestMethod.POST)
  public ResponseEnvelope deleteContractList(@RequestParam("ids") List<Long> ids) {
    try {
      return iOrderContractService.deleteContractList(ids);
    } catch (Exception e) {
      return ResultVOUtil.returnFail(ResultEnum.FAIL.getCode(), e.getMessage());
    }
  }
}
