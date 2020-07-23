package com.mukutech.websiteservice.controller;


import com.mukutech.websiteservice.common.utils.response.ResponseEnvelope;
import com.mukutech.websiteservice.pojo.dto.SysJobDTO;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import com.mukutech.websiteservice.service.ISysJobService;

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
@Api(tags = {"招聘职位API"})
@Controller
@RequestMapping("/websiteservice/sysJob")
@ResponseBody
public class SysJobController {

    @Autowired
    private ISysJobService iSysJobService;


    @ApiOperation(value = "列表分页查询", notes = "API")
    @RequestMapping(value = "/searchSysJobListPage", method = RequestMethod.POST)
    public ResponseEnvelope searchSysJobListPage(@RequestBody SysJobDTO dto) {
        return iSysJobService.searchSysJobListPage(dto);
    }

    @ApiOperation(value = "详细查询", notes = "API")
    @RequestMapping(value = "/searchSysJobOne", method = RequestMethod.GET)
    public ResponseEnvelope searchSysJobOne(Long id) {
        return iSysJobService.searchSysJobOne(id);
    }

    @ApiOperation(value = "添加", notes = "API")
    @RequestMapping(value = "/addSysJob", method = RequestMethod.POST)
    public ResponseEnvelope addSysJob(@RequestBody SysJobDTO dto) {
        return iSysJobService.addSysJob(dto);
    }

    @ApiOperation(value = "更新", notes = "API")
    @RequestMapping(value = "/updateSysJob", method = RequestMethod.POST)
    public ResponseEnvelope updateSysJob(@RequestBody SysJobDTO dto) {
        return iSysJobService.updateSysJob(dto);
    }

    @ApiOperation(value = "删除", notes = "API")
    @RequestMapping(value = "/deleteSysJob", method = RequestMethod.GET)
    public ResponseEnvelope deleteSysJob(Long id) {
        return iSysJobService.deleteSysJob(id);
    }

}
