package com.rhdk.purchasingservice.feign;

import com.rhdk.purchasingservice.common.config.FeignExecptionConfig;
import com.rhdk.purchasingservice.common.config.MultipartSupportConfig;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.entity.AssetEntityInfo;
import com.rhdk.purchasingservice.pojo.entity.AssetEntityPrpt;
import com.rhdk.purchasingservice.pojo.entity.AssetTmplInfo;
import com.rhdk.purchasingservice.pojo.entity.Customer;
import com.rhdk.purchasingservice.pojo.query.AssetQuery;
import com.rhdk.purchasingservice.pojo.query.TmplPrptsFilter;
import com.rhdk.purchasingservice.pojo.vo.AssetCatVO;
import com.rhdk.purchasingservice.pojo.vo.AssetTmplInfoVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: LMYOU
 * @create: 2020-04-28 @Description:经租系统，资产基础服务
 */
@FeignClient(
    value = "${feignName.assetService}",
    fallback = FeignExecptionConfig.class,
    configuration = MultipartSupportConfig.class)
@Component
public interface AssetServiceFeign {

  /** 跨服务调用的方法,注意MultipartFile的注解要用@RequestPart */
  @RequestMapping(
      value = "/fileUploadService/uploadSingleFile",
      method = RequestMethod.POST,
      produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  String uploadSingleFile(
      @RequestPart("file") MultipartFile file,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 获取送货明细清单数据
   *
   * @param assetQuery
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetEntityInfo/searchEntityInfoList",
      method = RequestMethod.POST)
  ResponseEnvelope searchEntityInfoPage(
      @RequestBody AssetQuery assetQuery, @RequestHeader(value = "Authorization") String token);

  /**
   * 获取固有属性值
   *
   * @param assetQuery
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetTmplPrpts/searchValByPrptIds",
      method = RequestMethod.POST)
  ResponseEnvelope searchValByPrptIds(
      @RequestBody AssetQuery assetQuery, @RequestHeader(value = "Authorization") String token);

  /**
   * 模糊查询供应商
   *
   * @param companyName
   * @param token
   * @return
   */
  @RequestMapping(value = "/assetservice/orderCustomer/getSupplyList", method = RequestMethod.POST)
  ResponseEnvelope getSupplyList(
      @RequestParam("companyName") String companyName,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 查询客户ids
   *
   * @param companyName
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/orderCustomer/getIdsBySupplierName",
      method = RequestMethod.POST)
  ResponseEnvelope getIdsBySupplierName(
      @RequestParam("companyName") String companyName,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 查询单个客户详情
   *
   * @param id
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/orderCustomer/searchCustomerOne",
      method = RequestMethod.POST)
  ResponseEnvelope<Customer> searchCustomerOne(
      @RequestParam("id") Long id, @RequestHeader(value = "Authorization") String token);

  /**
   * 获取单个模板详情
   *
   * @param id
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetTmplInfo/searchAssetTmplInfoOne",
      method = RequestMethod.POST)
  ResponseEnvelope<AssetTmplInfo> searchAssetTmplInfoOne(
      @RequestParam("id") Long id, @RequestHeader(value = "Authorization") String token);

  /**
   * 资产实体添加
   *
   * @param assetEntityInfo
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetEntityInfo/addAssetEntityInfo",
      method = RequestMethod.POST)
  ResponseEnvelope<AssetEntityInfo> addAssetEntityInfo(
      @RequestBody AssetEntityInfo assetEntityInfo,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 根据资产实体id和资产状态来查询资产信息
   *
   * @param assetQuery
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetEntityInfo/searchAssetEntityInfoOne",
      method = RequestMethod.POST)
  ResponseEnvelope<AssetEntityInfo> searchAssetEntityInfoOne(
      @RequestBody AssetQuery assetQuery, @RequestHeader(value = "Authorization") String token);

  /**
   * 更新资产实体信息
   *
   * @param assetEntityInfo
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetEntityInfo/updateAssetEntityInfo",
      method = RequestMethod.POST)
  ResponseEnvelope<AssetEntityInfo> updateAssetEntityInfo(
      @RequestBody AssetEntityInfo assetEntityInfo,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 资产实体实际删除
   *
   * @param assetIds
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetEntityInfo/deleteEntitys",
      method = RequestMethod.POST)
  ResponseEnvelope deleteEntitys(
      @RequestParam("assetIds") Long[] assetIds,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 资产实体信息更新
   *
   * @param model
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetEntityInfo/updateEntityInfo",
      method = RequestMethod.POST)
  ResponseEnvelope updateEntityInfo(
      @RequestBody OrderDelivemiddleDTO model,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 资产实体信息更新
   *
   * @param assetIds
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetEntityInfo/updateEntitys",
      method = RequestMethod.POST)
  ResponseEnvelope updateEntitys(
      @RequestParam("assetIds") Long[] assetIds,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 资产实体属性值添加
   *
   * @param dto
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetEntityPrpt/addAssetEntityPrpt",
      method = RequestMethod.POST)
  ResponseEnvelope<AssetEntityPrpt> addAssetEntityPrpt(
      @RequestBody AssetEntityPrpt dto, @RequestHeader(value = "Authorization") String token);

  /**
   * 资产实体属性值批量实际删除
   *
   * @param assetIds
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetEntityPrpt/deleteEntityPrpts",
      method = RequestMethod.POST)
  ResponseEnvelope deleteEntityPrpts(
      @RequestParam("assetIds") Long[] assetIds,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 资产实体属性值批量物理删除
   *
   * @param assetIds
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetEntityPrpt/updateEntityprpts",
      method = RequestMethod.POST)
  ResponseEnvelope updateEntityprpts(
      @RequestParam("assetIds") Long[] assetIds,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 批量添加附件
   *
   * @param dto
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/orderAttachment/addBeatchAtta",
      method = RequestMethod.POST)
  ResponseEnvelope addBeatchAtta(
      @RequestBody List<OrderAttachmentDTO> dto,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 获取指定模块的附件列表
   *
   * @param dto
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/orderAttachment/selectListByParentId",
      method = RequestMethod.POST)
  ResponseEnvelope<List<Map<String, Object>>> selectListByParentId(
      @RequestBody OrderAttachmentDTO dto, @RequestHeader(value = "Authorization") String token);

  /**
   * 获取指定模块的附件列表数目
   *
   * @param dto
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/orderAttachment/selectAttachNum",
      method = RequestMethod.POST)
  ResponseEnvelope<Integer> selectAttachNum(
      @RequestBody OrderAttachmentDTO dto, @RequestHeader(value = "Authorization") String token);

  /**
   * 删除指定模块的附件列表
   *
   * @param dto
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/orderAttachment/deleteAttachmentByParentId",
      method = RequestMethod.POST)
  ResponseEnvelope deleteAttachmentByParentId(
      @RequestBody OrderAttachmentDTO dto, @RequestHeader(value = "Authorization") String token);

  /**
   * 根据资产模板id来获取资产id集合并将资产状态从暂存改变为待签收
   *
   * @param model
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetEntityInfo/updateEntitysStatus",
      method = RequestMethod.POST)
  ResponseEnvelope<List<Long>> updateEntitysStatus(
      @RequestBody OrderDelivemiddleDTO model,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 导出合同列表数据
   *
   * @param dto
   * @return
   */
  @RequestMapping(
      value = "/fileUploadService/exportDataList",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  byte[] exportDataList(
      @RequestBody Map<String, Object> dto, @RequestHeader(value = "Authorization") String token);

  /**
   * 根据模板id来查询固有属性值
   *
   * @param id
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetTmplPrpts/selectPrptValByTmplId",
      method = RequestMethod.POST)
  ResponseEnvelope<AssetTmplInfoVO> selectPrptValByTmplId(
      @RequestParam("id") Long id, @RequestHeader(value = "Authorization") String token);

  @RequestMapping(
      value = "/assetservice/assetTmplPrpts/searchPKValByTmpId",
      method = RequestMethod.POST)
  ResponseEnvelope<Set<String>> searchPKValByTmpId(
      @RequestBody TmplPrptsFilter tmplPrptsFilter,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 根据送货明细记录id来查询附件清单的资产id集合
   *
   * @param middleIds
   * @param token
   * @return
   */
  @RequestMapping(
      value = "/assetservice/assetEntityInfo/getEntityIdsByMid",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEnvelope<List<Map<String, Object>>> getEntityIdsByMid(
      @RequestParam("middleIds") Long[] middleIds,
      @RequestHeader(value = "Authorization") String token);

  /**
   * 根据资产类别id来查询资产的searchkey
   *
   * @param id
   * @param token
   * @return
   */
  @RequestMapping(value = "/assetservice/assetCat/searchAssetCatOne", method = RequestMethod.POST)
  ResponseEnvelope<AssetCatVO> searchAssetCatOne(
      @RequestParam("id") Long id, @RequestHeader(value = "Authorization") String token);
}
