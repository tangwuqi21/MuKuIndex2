package com.mukutech.websiteservice.controller;


import com.mukutech.websiteservice.common.utils.response.ResponseEnvelope;
import com.mukutech.websiteservice.pojo.dto.SysCasusDTO;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import com.mukutech.websiteservice.service.ISysCasusService;

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
@Api(tags = {"成功案例API"})
@Controller
@RequestMapping("/websiteservice/sysCasus")
@ResponseBody
public class SysCasusController {

    @Autowired
    private ISysCasusService iSysCasusService;


    @ApiOperation(value = "列表分页查询", notes = "API")
    @RequestMapping(value = "/searchSysCasusListPage", method = RequestMethod.POST)
    public ResponseEnvelope searchSysCasusListPage(@RequestBody SysCasusDTO dto) {
        return iSysCasusService.searchSysCasusListPage(dto);
    }

    @ApiOperation(value = "详细查询", notes = "API")
    @RequestMapping(value = "/searchSysCasusOne", method = RequestMethod.GET)
    public ResponseEnvelope searchSysCasusOne(Long id) {
        return iSysCasusService.searchSysCasusOne(id);
    }

    @ApiOperation(value = "添加", notes = "API")
    @RequestMapping(value = "/addSysCasus", method = RequestMethod.POST)
    public ResponseEnvelope addSysCasus(@RequestBody SysCasusDTO dto) {
        return iSysCasusService.addSysCasus(dto);
    }

    @ApiOperation(value = "更新", notes = "API")
    @RequestMapping(value = "/updateSysCasus", method = RequestMethod.POST)
    public ResponseEnvelope updateSysCasus(@RequestBody SysCasusDTO dto) {
        return iSysCasusService.updateSysCasus(dto);
    }

    @ApiOperation(value = "删除", notes = "API")
    @RequestMapping(value = "/deleteSysCasus", method = RequestMethod.GET)
    public ResponseEnvelope deleteSysCasus(Long id) {
        return iSysCasusService.deleteSysCasus(id);
    }

}
