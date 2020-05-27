package com.rhdk.purchasingservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderDeliverecordsDTO;
import com.rhdk.purchasingservice.pojo.query.OrderDeliverecordsQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderDeliverecordsVO;
import com.rhdk.purchasingservice.service.IOrderDeliverecordsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * 送货单 前端控制器
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Slf4j
@Api(tags = {"送货单API"})
@Controller
@RequestMapping("/purchasingservice/orderDeliverecords")
@ResponseBody
public class OrderDeliverecordsController {

  @Autowired private IOrderDeliverecordsService iOrderDeliverecordsService;

  /**
   * 送货记录列表查询 返回送货记录的签收状态、送货记录名称、合同相关信息 不包括送货记录明细
   *
   * @param dto
   * @return
   */
  @ApiOperation(value = "送货单列表分页查询", notes = "送货单API")
  @RequestMapping(value = "/searchOrderDeliverecordsListPage", method = RequestMethod.POST)
  public ResponseEnvelope<IPage<OrderDeliverecordsVO>> searchOrderDeliverecordsListPage(
      @RequestBody OrderDeliverecordsQuery dto) {
    return iOrderDeliverecordsService.searchOrderDeliverecordsListPage(dto);
  }

  /**
   * 根据送货记录id查询送货记录详情信息，返回送货记录基本信息，合同基本信息 送货记录下的明细记录基本信息
   *
   * @param id
   * @return
   */
  @ApiOperation(value = "送货单详细查询", notes = "送货单API")
  @RequestMapping(value = "/searchOrderDeliverecordsOne", method = RequestMethod.POST)
  public ResponseEnvelope<OrderDeliverecordsVO> searchOrderDeliverecordsOne(Long id) {
    return iOrderDeliverecordsService.searchOrderDeliverecordsOne(id);
  }

  /**
   * 添加送货记录信息，需要填写送货记录基本信息、送货明细基本信息，还有相关附件信息
   *
   * @param dto
   * @return
   * @throws Exception
   */
  @ApiOperation(value = "送货单添加", notes = "送货单API")
  @RequestMapping(value = "/addOrderDeliverecords", method = RequestMethod.POST)
  public ResponseEnvelope addOrderDeliverecords(@RequestBody @Valid OrderDeliverecordsDTO dto)
      throws Exception {
    return iOrderDeliverecordsService.addOrderDeliverecords(dto);
  }

  /**
   * 送货记录更新接口，包含送货记录基本信息的更新 送货明细基本信息的更新 明细清单的资产信息更新
   *
   * @param dto
   * @return
   * @throws Exception
   */
  @ApiOperation(value = "送货单更新", notes = "送货单API")
  @RequestMapping(value = "/updateOrderDeliverecords", method = RequestMethod.POST)
  public ResponseEnvelope updateOrderDeliverecords(@RequestBody OrderDeliverecordsDTO dto)
      throws Exception {
    return iOrderDeliverecordsService.updateOrderDeliverecords(dto);
  }

  /**
   * 根据送货记录id同步进行送货记录的逻辑删除 同步删除送货记录的相关附件信息 送货记录下的明细信息删除 送货记录的相关附件信息删除
   *
   * @param id
   * @return
   * @throws Exception
   */
  @ApiOperation(value = "送货单删除", notes = "送货单API")
  @RequestMapping(value = "/deleteOrderDeliverecords", method = RequestMethod.POST)
  public ResponseEnvelope deleteOrderDeliverecords(Long id) throws Exception {
    return iOrderDeliverecordsService.deleteOrderDeliverecords(id);
  }
}
