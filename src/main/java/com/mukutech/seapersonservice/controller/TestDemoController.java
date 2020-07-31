package com.mukutech.seapersonservice.controller;


import com.mukutech.seapersonservice.common.utils.response.ResponseEnvelope;
import com.mukutech.seapersonservice.pojo.dto.TestDemoDTO;
import com.mukutech.seapersonservice.service.ITestDemoService;
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
 * <p>
 * 用户基础属性表 前端控制器
 * </p>
 *
 * @author LMYOU
 * @since 2020-07-22
 */
@Slf4j
@Api(tags = {"用户基础属性表API"})
@Controller
@RequestMapping("/seapersonservice/testDemo")
@ResponseBody
public class TestDemoController {

    @Autowired
    private ITestDemoService iTestDemoService;


    @ApiOperation(value = "用户基础属性表列表分页查询", notes = "用户基础属性表API")
    @RequestMapping(value = "/searchTestDemoListPage", method = RequestMethod.POST)
    public ResponseEnvelope searchTestDemoListPage(@RequestBody TestDemoDTO dto) {
        return iTestDemoService.searchTestDemoListPage(dto);
    }

    @ApiOperation(value = "用户基础属性表详细查询", notes = "用户基础属性表API")
    @RequestMapping(value = "/searchTestDemoOne", method = RequestMethod.GET)
    public ResponseEnvelope searchTestDemoOne(Long id) {
        return iTestDemoService.searchTestDemoOne(id);
    }

    @ApiOperation(value = "用户基础属性表添加", notes = "用户基础属性表API")
    @RequestMapping(value = "/addTestDemo", method = RequestMethod.POST)
    public ResponseEnvelope addTestDemo(@RequestBody TestDemoDTO dto) {
        return iTestDemoService.addTestDemo(dto);
    }

    @ApiOperation(value = "用户基础属性表更新", notes = "用户基础属性表API")
    @RequestMapping(value = "/updateTestDemo", method = RequestMethod.POST)
    public ResponseEnvelope updateTestDemo(@RequestBody TestDemoDTO dto) {
        return iTestDemoService.updateTestDemo(dto);
    }

    @ApiOperation(value = "用户基础属性表删除", notes = "用户基础属性表API")
    @RequestMapping(value = "/deleteTestDemo", method = RequestMethod.GET)
    public ResponseEnvelope deleteTestDemo(Long id) {
        return iTestDemoService.deleteTestDemo(id);
    }

}
