package com.rhdk.purchasingservice.feign;

import com.rhdk.purchasingservice.common.config.FeignExecptionConfig;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.entity.AssetEntityInfo;
import com.rhdk.purchasingservice.pojo.entity.AssetEntityPrpt;
import com.rhdk.purchasingservice.pojo.entity.AssetTmplInfo;
import com.rhdk.purchasingservice.pojo.entity.Customer;
import com.rhdk.purchasingservice.pojo.query.AssetQuery;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: LMYOU
 * @create: 2020-04-28
 * @Description:经租系统，资产基础服务
 */
@FeignClient(value = "${feignName.assetService}", fallback = FeignExecptionConfig.class)
@Component
public interface AssetServiceFeign {

    /**
     * 获取送货明细清单数据
     * @param assetQuery
     * @param token
     * @return
     */
    @RequestMapping(value = "/assetservice/assetEntityInfo/searchEntityInfoList", method = RequestMethod.POST)
    ResponseEnvelope searchEntityInfoPage(@RequestBody AssetQuery assetQuery, @RequestHeader(value = "Authorization") String token);

    /**
     * 获取固有属性值
     * @param assetQuery
     * @param token
     * @return
     */
    @RequestMapping(value = "/assetservice/assetTmplPrpts/searchValByPrptIds", method = RequestMethod.POST)
    ResponseEnvelope searchValByPrptIds(@RequestBody AssetQuery assetQuery, @RequestHeader(value = "Authorization") String token);


    /**
     * 模糊查询供应商
     * @param companyName
     * @param token
     * @return
     */
    @RequestMapping(value = "/assetservice/orderCustomer/getSupplyList", method = RequestMethod.POST)
    ResponseEnvelope getSupplyList(@RequestParam("companyName") String companyName, @RequestHeader(value = "Authorization") String token);


    /**
     *查询客户ids
     * @param companyName
     * @param token
     * @return
     */
    @RequestMapping(value = "/assetservice/orderCustomer/getIdsBySupplierName", method = RequestMethod.POST)
    ResponseEnvelope getIdsBySupplierName(@RequestParam("companyName") String companyName, @RequestHeader(value = "Authorization") String token);


    /**
     * 查询单个客户详情
     * @param id
     * @param token
     * @return
     */
    @RequestMapping(value = "/assetservice/orderCustomer/searchCustomerOne", method = RequestMethod.POST)
    ResponseEnvelope<Customer> searchCustomerOne(@RequestParam("id") Long id, @RequestHeader(value = "Authorization") String token);


    /**
     * 获取单个模板详情
     * @param id
     * @param token
     * @return
     */
    @RequestMapping(value = "/assetservice/assetTmplInfo/searchAssetTmplInfoOne", method = RequestMethod.POST)
    ResponseEnvelope<AssetTmplInfo> searchAssetTmplInfoOne(@RequestParam("id") Long id, @RequestHeader(value = "Authorization") String token);

    /**
     * 资产实体添加
     * @param assetEntityInfo
     * @param token
     * @return
     */
    @RequestMapping(value = "/assetservice/assetEntityInfo/addAssetEntityInfo", method = RequestMethod.POST)
    ResponseEnvelope<AssetEntityInfo> addAssetEntityInfo(@RequestBody AssetEntityInfo assetEntityInfo, @RequestHeader(value = "Authorization") String token);

    /**
     * 资产实体实际删除
     * @param assetIds
     * @param token
     * @return
     */
    @RequestMapping(value = "/assetservice/assetEntityInfo/deleteEntitys", method = RequestMethod.POST)
    ResponseEnvelope deleteEntitys(@RequestParam("assetIds") List<Long> assetIds, @RequestHeader(value = "Authorization") String token);


    /**
     *资产实体信息更新
     * @param model
     * @param token
     * @return
     */
    @RequestMapping(value = "/assetservice/assetEntityInfo/updateEntityInfo", method = RequestMethod.POST)
    ResponseEnvelope updateEntityInfo(@RequestParam("assetIds") List<Long> assetIds,@RequestBody OrderDelivemiddleDTO model, @RequestHeader(value = "Authorization") String token);


    /**
     * 资产实体信息更新
     * @param assetIds
     * @param token
     * @return
     */
    @RequestMapping(value = "/assetservice/assetEntityInfo/updateEntitys", method = RequestMethod.POST)
    ResponseEnvelope updateEntitys(@RequestParam("assetIds") List<Long> assetIds, @RequestHeader(value = "Authorization") String token);


    /**
     * 资产实体属性值添加
     * @param dto
     * @param token
     * @return
     */
    @RequestMapping(value = "/assetservice/assetEntityPrpt/addAssetEntityPrpt", method = RequestMethod.POST)
    ResponseEnvelope<AssetEntityPrpt> addAssetEntityPrpt(@RequestBody AssetEntityPrpt dto, @RequestHeader(value = "Authorization") String token);

    /**
     * 资产实体属性值批量实际删除
     * @param assetIds
     * @param token
     * @return
     */
    @RequestMapping(value = "/assetservice/assetEntityPrpt/deleteEntityPrpts", method = RequestMethod.POST)
    ResponseEnvelope deleteEntityPrpts(@RequestParam("assetIds") List<Long> assetIds, @RequestHeader(value = "Authorization") String token);


    /**
     * 资产实体属性值批量物理删除
     * @param assetIds
     * @param token
     * @return
     */
    @RequestMapping(value = "/assetservice/assetEntityPrpt/updateEntityprpts", method = RequestMethod.POST)
    ResponseEnvelope updateEntityprpts(@RequestParam("assetIds") List<Long> assetIds, @RequestHeader(value = "Authorization") String token);


    /**
     * 批量添加附件
     * @param dto
     * @param token
     * @return
     */
    @RequestMapping(value = "/assetservice/orderAttachment/addBeatchAtta", method = RequestMethod.POST)
    ResponseEnvelope addBeatchAtta(@RequestBody List<OrderAttachmentDTO> dto, @RequestHeader(value = "Authorization") String token);


}
