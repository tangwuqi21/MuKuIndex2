package com.rhdk.purchasingservice.controller;

import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.query.CommonQuery;
import com.rhdk.purchasingservice.service.CommonService;
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
 * 查询选择框API 前端控制器
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Slf4j
@Api(tags = {"查询选择框API"})
@Controller
@RequestMapping("/purchasingservice/common")
@ResponseBody
public class OrderCommonController {

  @Autowired private CommonService commonService;

  /**
   * 合同列表选择框数据查询
   *
   * @param commonQuery
   * @return
   */
  @ApiOperation(value = "合同列表查询", notes = "需提供采购合同id，合同名称、往来单位、源单类型、源单编码")
  @RequestMapping(value = "/getContractInfoList", method = RequestMethod.POST)
  public ResponseEnvelope getContractInfoList(@RequestBody CommonQuery commonQuery) {
    return commonService.getContractInfoList(commonQuery);
  }
}
