package com.rhdk.purchasingservice.controller;


import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.query.OrderAttachmentQuery;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import com.rhdk.purchasingservice.service.IOrderAttachmentService;

import org.springframework.stereotype.Controller;


/**
 * <p>
 * 附件表 前端控制器
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Slf4j
@Api(tags = {"附件表API"})
@Controller
@RequestMapping("/purchasingservice/orderAttachment")
@ResponseBody
public class OrderAttachmentController {

    @Autowired
    private IOrderAttachmentService iOrderAttachmentService;


    @ApiOperation(value = "附件表列表分页查询", notes = "附件表API")
    @RequestMapping(value = "/searchOrderAttachmentListPage", method = RequestMethod.POST)
    public ResponseEnvelope searchOrderAttachmentListPage(@RequestBody OrderAttachmentQuery dto) {
        return iOrderAttachmentService.searchOrderAttachmentListPage(dto);
    }

    @ApiOperation(value = "附件表详细查询", notes = "附件表API")
    @RequestMapping(value = "/searchOrderAttachmentOne", method = RequestMethod.GET)
    public ResponseEnvelope searchOrderAttachmentOne(Long id) {
        return iOrderAttachmentService.searchOrderAttachmentOne(id);
    }

    @ApiOperation(value = "附件表添加", notes = "附件表API")
    @RequestMapping(value = "/addOrderAttachment", method = RequestMethod.POST)
    public ResponseEnvelope addOrderAttachment(@RequestBody OrderAttachmentDTO dto) {
        return iOrderAttachmentService.addOrderAttachment(dto);
    }

    @ApiOperation(value = "附件表更新", notes = "附件表API")
    @RequestMapping(value = "/updateOrderAttachment", method = RequestMethod.POST)
    public ResponseEnvelope updateOrderAttachment(@RequestBody OrderAttachmentDTO dto) {
        return iOrderAttachmentService.updateOrderAttachment(dto);
    }

    @ApiOperation(value = "附件表删除", notes = "附件表API")
    @RequestMapping(value = "/deleteOrderAttachment", method = RequestMethod.GET)
    public ResponseEnvelope deleteOrderAttachment(Long id) {
        return iOrderAttachmentService.deleteOrderAttachment(id);
    }

}
