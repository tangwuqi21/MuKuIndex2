package com.mukutech.websiteservice.controller;


import com.mukutech.websiteservice.common.utils.response.ResponseEnvelope;
import com.mukutech.websiteservice.pojo.dto.SysCorpDTO;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import com.mukutech.websiteservice.service.ISysCorpService;

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
@Api(tags = {"公司信息API"})
@Controller
@RequestMapping("/websiteservice/sysCorp")
@ResponseBody
public class SysCorpController {

    @Autowired
    private ISysCorpService iSysCorpService;


    @ApiOperation(value = "列表分页查询", notes = "API")
    @RequestMapping(value = "/searchSysCorpListPage", method = RequestMethod.POST)
    public ResponseEnvelope searchSysCorpListPage(@RequestBody SysCorpDTO dto) {
        return iSysCorpService.searchSysCorpListPage(dto);
    }

    @ApiOperation(value = "查询公司详细信息", notes = "查询公司详细信息的API")
    @RequestMapping(value = "/searchSysCorpOne", method = RequestMethod.GET)
    public ResponseEnvelope searchSysCorpOne() {
        return iSysCorpService.searchSysCorpOne(1L);
    }

    @ApiOperation(value = "添加", notes = "API")
    @RequestMapping(value = "/addSysCorp", method = RequestMethod.POST)
    public ResponseEnvelope addSysCorp(@RequestBody SysCorpDTO dto) {
        return iSysCorpService.addSysCorp(dto);
    }

    @ApiOperation(value = "更新", notes = "API")
    @RequestMapping(value = "/updateSysCorp", method = RequestMethod.POST)
    public ResponseEnvelope updateSysCorp(@RequestBody SysCorpDTO dto) {
        return iSysCorpService.updateSysCorp(dto);
    }

    @ApiOperation(value = "删除", notes = "API")
    @RequestMapping(value = "/deleteSysCorp", method = RequestMethod.GET)
    public ResponseEnvelope deleteSysCorp(Long id) {
        return iSysCorpService.deleteSysCorp(id);
    }

}
