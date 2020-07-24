package com.mukutech.websiteservice.controller;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
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


    @ApiOperation(value = "招聘职位列表查询", notes = "招聘职位列表的分页查询的API")
    @RequestMapping(value = "/searchSysJobListPage", method = RequestMethod.POST)
    @ApiOperationSupport()
    public ResponseEnvelope searchSysJobListPage() {
        return iSysJobService.searchSysJobListPage();
    }

    @ApiOperation(value = "详细查询", notes = "API")
    @RequestMapping(value = "/searchSysJobOne", method = RequestMethod.GET)
    public ResponseEnvelope searchSysJobOne(Long id) {
        return iSysJobService.searchSysJobOne(id);
    }

    @ApiOperation(value = "添加", notes = "API")
    @RequestMapping(value = "/addSysJob", method = RequestMethod.POST)
    @ApiOperationSupport(includeParameters = {"dto.name", "dto.department"})
    public ResponseEnvelope addSysJob(@RequestBody SysJobDTO dto) {
        return iSysJobService.addSysJob(dto);
    }

    @ApiOperation(value = "更新", notes = "API")
    @RequestMapping(value = "/updateSysJob", method = RequestMethod.POST)
    @ApiOperationSupport(includeParameters = {"dto.id", "dto.name", "dto.department"})
    public ResponseEnvelope updateSysJob(@RequestBody SysJobDTO dto) {
        return iSysJobService.updateSysJob(dto);
    }

    @ApiOperation(value = "真实删除", notes = "API")
    @RequestMapping(value = "/deleteSysJob", method = RequestMethod.GET)
    public ResponseEnvelope deleteSysJob(Long id) {
        return iSysJobService.deleteSysJob(id);
    }

    @ApiOperation(value = "逻辑删除", notes = "API")
    @RequestMapping(value = "/logicDeleteJob", method = RequestMethod.GET)
    public ResponseEnvelope logicDeleteJob(Long id) {
        return iSysJobService.logicDeleteJob(id);
    }

}
