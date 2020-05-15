package com.rhdk.purchasingservice.controller;


import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.service.CommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * <p>
 * 查询选择框API 前端控制器
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Slf4j
@Api(tags = {"查询选择框API"})
@Controller
@RequestMapping("/purchasingservice/common")
@ResponseBody
public class OrderCommonController {

    @Autowired
    private CommonService commonService;


    @ApiOperation(value = "供应商、合作伙伴列表查询", notes = "需提供单位编码，单位名称")
    @RequestMapping(value = "/getSupplyInfoList", method = RequestMethod.POST)
    public ResponseEnvelope getSupplyList(String companyName) {
        return commonService.getSupplyList(companyName);
    }


    @ApiOperation(value = "合同列表查询", notes = "需提供采购合同id，合同名称、往来单位、源单类型、源单编码")
    @RequestMapping(value = "/getContractInfoList", method = RequestMethod.POST)
    public ResponseEnvelope getContractInfoList(String contractName) {
        return commonService.getContractInfoList(contractName);
    }


    @ApiOperation(value = "资产品名列表查询", notes = "需提供资产模板名称，模板id，物管类型，模板版本号，资产类型id")
    @RequestMapping(value = "/getAssetInfoList", method = RequestMethod.POST)
    public ResponseEnvelope getAssetInfoList() {
        return commonService.getAssetInfoList();
    }

}
