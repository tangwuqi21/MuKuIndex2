package com.rhdk.purchasingservice.controller;


import com.rhdk.purchasingservice.common.exception.RequestEmptyException;
import com.rhdk.purchasingservice.common.utils.FileUtil;
import com.rhdk.purchasingservice.common.utils.TokenUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderContractDTO;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import com.rhdk.purchasingservice.service.IOrderContractService;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;


/**
 * <p>
 * 合同表 前端控制器
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Slf4j
@Api(tags = {"合同表API"})
@Controller
@RequestMapping("/purchasingservice/orderContract")
@ResponseBody
public class OrderContractController {

    @Autowired
    private IOrderContractService iOrderContractService;


    /**
     * 采购管理-合同列表查询
     * @param dto
     * @return
     */
    @ApiOperation(value = "合同表列表分页查询", notes = "合同表API")
    @RequestMapping(value = "/searchOrderContractListPage", method = RequestMethod.POST)
    public ResponseEnvelope searchOrderContractListPage(@RequestBody OrderContractDTO dto) {
        return iOrderContractService.searchOrderContractListPage(dto);
    }

    /**
     * 采购管理-合同明细查询
     * @param id
     * @return
     */
    @ApiOperation(value = "合同表详细查询", notes = "合同表API")
    @RequestMapping(value = "/searchOrderContractOne", method = RequestMethod.POST)
    public ResponseEnvelope searchOrderContractOne(Long id) {
        return iOrderContractService.searchOrderContractOne(id);
    }

    /**
     * 采购管理-合同添加
     * @param dto
     * @return
     */
    @ApiOperation(value = "合同表添加", notes = "合同表API")
    @RequestMapping(value = "/addOrderContract", method = RequestMethod.POST)
    public ResponseEnvelope addOrderContract(@RequestBody @Valid OrderContractDTO dto) {
        return iOrderContractService.addOrderContract(dto);
    }

    /**
     * 采购管理-合同更新
     * @param dto
     * @return
     */
    @ApiOperation(value = "合同表更新", notes = "合同表API")
    @RequestMapping(value = "/updateOrderContract", method = RequestMethod.POST)
    public ResponseEnvelope updateOrderContract(@RequestBody @Valid OrderContractDTO dto) {
        return iOrderContractService.updateOrderContract(dto);
    }

}
