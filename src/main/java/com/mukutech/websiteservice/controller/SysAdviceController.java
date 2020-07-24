package com.mukutech.websiteservice.controller;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.mukutech.websiteservice.common.utils.response.ResponseEnvelope;
import com.mukutech.websiteservice.pojo.dto.SysAdviceDTO;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import com.mukutech.websiteservice.service.ISysAdviceService;

import org.springframework.stereotype.Controller;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author TCGUO
 * @since 2020-07-23
 */
@Slf4j
@Api(tags = {"留言信息API"})
@Controller
@RequestMapping("/websiteservice/sysAdvice")
@ResponseBody
public class SysAdviceController {

    @Autowired
    private ISysAdviceService iSysAdviceService;


    @ApiOperation(value = "列表分页查询", notes = "API")
    @RequestMapping(value = "/searchSysAdviceListPage", method = RequestMethod.POST)
    @ApiOperationSupport(includeParameters = {"dto.pageSize", "dto.currentPage"})
    public ResponseEnvelope searchSysAdviceListPage(@RequestBody SysAdviceDTO dto) {
        return iSysAdviceService.searchSysAdviceListPage(dto);
    }

    @ApiOperation(value = "详细查询", notes = "API")
    @RequestMapping(value = "/searchSysAdviceOne", method = RequestMethod.GET)
    public ResponseEnvelope searchSysAdviceOne(Long id) {
        return iSysAdviceService.searchSysAdviceOne(id);
    }

    @ApiOperation(value = "添加", notes = "API")
    @RequestMapping(value = "/addSysAdvice", method = RequestMethod.POST)
    @ApiOperationSupport(includeParameters = {"dto.title", "dto.msg", "dto.email"})
    public ResponseEnvelope addSysAdvice(@RequestBody SysAdviceDTO dto) {
        return iSysAdviceService.addSysAdvice(dto);
    }

    @ApiOperation(value = "更新", notes = "API")
    @RequestMapping(value = "/updateSysAdvice", method = RequestMethod.POST)
    @ApiOperationSupport(ignoreParameters = {"dto.pageSize", "dto.currentPage", "dto.state", "dto.createTime"})
    public ResponseEnvelope updateSysAdvice(@RequestBody SysAdviceDTO dto) {
        return iSysAdviceService.updateSysAdvice(dto);
    }

    @ApiOperation(value = "真实删除", notes = "API")
    @RequestMapping(value = "/deleteSysAdvice", method = RequestMethod.GET)
    public ResponseEnvelope deleteSysAdvice(Long id) {
        return iSysAdviceService.deleteSysAdvice(id);
    }

    @ApiOperation(value = "逻辑删除", notes = "API")
    @RequestMapping(value = "/logicDeleteAdvice", method = RequestMethod.GET)
    public ResponseEnvelope logicDeleteAdvice(Long id) {
        return iSysAdviceService.logicDeleteAdvice(id);
    }


}
