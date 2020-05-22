package com.rhdk.purchasingservice.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderDeliverecordsDTO;
import com.rhdk.purchasingservice.pojo.query.OrderDeliverecordsQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderDeliverecordsVO;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import com.rhdk.purchasingservice.service.IOrderDeliverecordsService;

import org.springframework.stereotype.Controller;

import javax.validation.Valid;

/**
 * <p>
 * 送货单 前端控制器
 * </p>
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

    @Autowired
    private IOrderDeliverecordsService iOrderDeliverecordsService;


    @ApiOperation(value = "送货单列表分页查询", notes = "送货单API")
    @RequestMapping(value = "/searchOrderDeliverecordsListPage", method = RequestMethod.POST)
    public ResponseEnvelope<IPage<OrderDeliverecordsVO>> searchOrderDeliverecordsListPage(@RequestBody OrderDeliverecordsQuery dto) {
        return iOrderDeliverecordsService.searchOrderDeliverecordsListPage(dto);
    }

    @ApiOperation(value = "送货单详细查询", notes = "送货单API")
    @RequestMapping(value = "/searchOrderDeliverecordsOne", method = RequestMethod.POST)
    public ResponseEnvelope<OrderDeliverecordsVO> searchOrderDeliverecordsOne(Long id) {
        return iOrderDeliverecordsService.searchOrderDeliverecordsOne(id);
    }

    @ApiOperation(value = "送货单添加", notes = "送货单API")
    @RequestMapping(value = "/addOrderDeliverecords", method = RequestMethod.POST)
    public ResponseEnvelope addOrderDeliverecords(@RequestBody @Valid OrderDeliverecordsDTO dto) throws Exception {
        return iOrderDeliverecordsService.addOrderDeliverecords(dto);
    }

    @ApiOperation(value = "送货单更新", notes = "送货单API")
    @RequestMapping(value = "/updateOrderDeliverecords", method = RequestMethod.POST)
    public ResponseEnvelope updateOrderDeliverecords(@RequestBody OrderDeliverecordsDTO dto) throws Exception{
        return iOrderDeliverecordsService.updateOrderDeliverecords(dto);
    }

    @ApiOperation(value = "送货单删除", notes = "送货单API")
    @RequestMapping(value = "/deleteOrderDeliverecords", method = RequestMethod.POST)
    public ResponseEnvelope deleteOrderDeliverecords(Long id) throws Exception{
        return iOrderDeliverecordsService.deleteOrderDeliverecords(id);
    }

}
