package com.rhdk.purchasingservice.controller;


import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;
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


    @ApiOperation(value = "送货记录明细中间表列表分页查询", notes = "送货记录明细中间表API")
    @RequestMapping(value = "/searchOrderDelivemiddleListPage", method = RequestMethod.POST)
    public ResponseEnvelope searchOrderDelivemiddleListPage(@RequestBody OrderDelivemiddleQuery dto) {
        return iOrderDelivemiddleService.searchOrderDelivemiddleListPage(dto);
    }

    @ApiOperation(value = "送货记录明细中间表详细查询", notes = "送货记录明细中间表API")
    @RequestMapping(value = "/searchOrderDelivemiddleOne", method = RequestMethod.GET)
    public ResponseEnvelope searchOrderDelivemiddleOne(Long id) {
        return iOrderDelivemiddleService.searchOrderDelivemiddleOne(id);
    }

    @ApiOperation(value = "送货记录明细中间表添加", notes = "送货记录明细中间表API")
    @RequestMapping(value = "/addOrderDelivemiddle", method = RequestMethod.POST)
    public ResponseEnvelope addOrderDelivemiddle(@RequestBody OrderDelivemiddleDTO dto) {
        return iOrderDelivemiddleService.addOrderDelivemiddle(dto);
    }

    @ApiOperation(value = "送货记录明细中间表更新", notes = "送货记录明细中间表API")
    @RequestMapping(value = "/updateOrderDelivemiddle", method = RequestMethod.POST)
    public ResponseEnvelope updateOrderDelivemiddle(@RequestBody OrderDelivemiddleDTO dto) {
        return iOrderDelivemiddleService.updateOrderDelivemiddle(dto);
    }

    @ApiOperation(value = "送货记录明细中间表删除", notes = "送货记录明细中间表API")
    @RequestMapping(value = "/deleteOrderDelivemiddle", method = RequestMethod.GET)
    public ResponseEnvelope deleteOrderDelivemiddle(Long id) {
        return iOrderDelivemiddleService.deleteOrderDelivemiddle(id);
    }

}
