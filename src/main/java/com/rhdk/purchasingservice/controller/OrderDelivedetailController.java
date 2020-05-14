package com.rhdk.purchasingservice.controller;


import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivedetailDTO;
import com.rhdk.purchasingservice.pojo.query.OrderDelivedetailQuery;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import com.rhdk.purchasingservice.service.IOrderDelivedetailService;

import org.springframework.stereotype.Controller;


/**
 * <p>
 * 送货明细 前端控制器
 * </p>
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

    @Autowired
    private IOrderDelivedetailService iOrderDelivedetailService;


    @ApiOperation(value = "送货明细列表分页查询", notes = "送货明细API")
    @RequestMapping(value = "/searchOrderDelivedetailListPage", method = RequestMethod.POST)
    public ResponseEnvelope searchOrderDelivedetailListPage(@RequestBody OrderDelivedetailQuery dto) {
        return iOrderDelivedetailService.searchOrderDelivedetailListPage(dto);
    }

    @ApiOperation(value = "送货明细详细查询", notes = "送货明细API")
    @RequestMapping(value = "/searchOrderDelivedetailOne", method = RequestMethod.GET)
    public ResponseEnvelope searchOrderDelivedetailOne(Long id) {
        return iOrderDelivedetailService.searchOrderDelivedetailOne(id);
    }

    @ApiOperation(value = "送货明细添加", notes = "送货明细API")
    @RequestMapping(value = "/addOrderDelivedetail", method = RequestMethod.POST)
    public ResponseEnvelope addOrderDelivedetail(@RequestBody OrderDelivedetailDTO dto) {
        return iOrderDelivedetailService.addOrderDelivedetail(dto);
    }

    @ApiOperation(value = "送货明细更新", notes = "送货明细API")
    @RequestMapping(value = "/updateOrderDelivedetail", method = RequestMethod.POST)
    public ResponseEnvelope updateOrderDelivedetail(@RequestBody OrderDelivedetailDTO dto) {
        return iOrderDelivedetailService.updateOrderDelivedetail(dto);
    }

    @ApiOperation(value = "送货明细删除", notes = "送货明细API")
    @RequestMapping(value = "/deleteOrderDelivedetail", method = RequestMethod.GET)
    public ResponseEnvelope deleteOrderDelivedetail(Long id) {
        return iOrderDelivedetailService.deleteOrderDelivedetail(id);
    }

}
