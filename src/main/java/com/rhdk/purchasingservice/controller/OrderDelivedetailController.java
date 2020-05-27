package com.rhdk.purchasingservice.controller;

import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.query.OrderDelivedetailQuery;
import com.rhdk.purchasingservice.service.IOrderDelivedetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 送货明细 前端控制器
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Slf4j
@Api(tags = {"送货明细API"})
@Controller
@RequestMapping("/purchasingservice/orderDelivedetail")
@ResponseBody
public class OrderDelivedetailController {

  @Autowired private IOrderDelivedetailService iOrderDelivedetailService;

  /**
   * 获取送货明细清单列表
   *
   * @param dto
   * @return
   */
  @ApiOperation(value = "送货明细列表分页查询", notes = "送货明细API")
  @RequestMapping(value = "/searchOrderDelivedetailListPage", method = RequestMethod.POST)
  public ResponseEnvelope searchOrderDelivedetailListPage(@RequestBody OrderDelivedetailQuery dto) {
    return iOrderDelivedetailService.searchOrderDelivedetailListPage(dto);
  }
}
