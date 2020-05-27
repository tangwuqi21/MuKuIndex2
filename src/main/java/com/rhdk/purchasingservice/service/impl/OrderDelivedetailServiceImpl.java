package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.TokenUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.AssetServiceFeign;
import com.rhdk.purchasingservice.mapper.OrderDelivedetailMapper;
import com.rhdk.purchasingservice.mapper.OrderDelivemiddleMapper;
import com.rhdk.purchasingservice.mapper.OrderDeliverecordsMapper;
import com.rhdk.purchasingservice.pojo.entity.AssetTmplInfo;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivedetail;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivemiddle;
import com.rhdk.purchasingservice.pojo.entity.OrderDeliverecords;
import com.rhdk.purchasingservice.pojo.query.AssetQuery;
import com.rhdk.purchasingservice.pojo.query.OrderDelivedetailQuery;
import com.rhdk.purchasingservice.service.IOrderDelivedetailService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 送货明细 服务实现类
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Slf4j
@Transactional
@Service
public class OrderDelivedetailServiceImpl
    extends ServiceImpl<OrderDelivedetailMapper, OrderDelivedetail>
    implements IOrderDelivedetailService {

  @Autowired private OrderDelivedetailMapper orderDelivedetailMapper;

  @Autowired private OrderDelivemiddleMapper orderDelivemiddleMapper;

  @Autowired private OrderDeliverecordsMapper orderDeliverecordsMapper;

  @Autowired private AssetServiceFeign assetServiceFeign;

  private static org.slf4j.Logger logger =
      LoggerFactory.getLogger(OrderDelivedetailServiceImpl.class);

  @Override
  public ResponseEnvelope searchOrderDelivedetailListPage(OrderDelivedetailQuery dto) {
    Page<OrderDelivedetail> page = new Page<OrderDelivedetail>();
    page.setSize(dto.getPageSize());
    page.setCurrent(dto.getCurrentPage());
    QueryWrapper<OrderDelivedetail> queryWrapper = new QueryWrapper<OrderDelivedetail>();
    OrderDelivedetail entity = new OrderDelivedetail();
    entity.setMiddleId(dto.getMiddleId());
    queryWrapper.setEntity(entity);
    logger.info("searchOrderDelivedetailListPage--获取送货明细签收对照表信息开始");
    OrderDelivemiddle orderDelivemiddle = orderDelivemiddleMapper.selectById(dto.getMiddleId());
    if (orderDelivemiddle == null) {
      return ResultVOUtil.returnFail(
          ResultEnum.DELIVER_MIDDLENULL.getCode(), ResultEnum.DELIVER_MIDDLENULL.getMessage());
    }
    logger.info(
        "searchOrderDelivedetailListPage--获取送货明细签收对照表信息：" + orderDelivemiddle.toString() + "结束");
    // 1.查询送货信息
    logger.info("searchOrderDelivedetailListPage--获取送货单信息开始");
    OrderDeliverecords orderDeliverecord =
        orderDeliverecordsMapper.getDeliverecordInfo(orderDelivemiddle.getDeliveryId());
    if (orderDeliverecord == null) {
      return ResultVOUtil.returnFail(
          ResultEnum.DELIVER_MIDDLDETAILENULL.getCode(),
          ResultEnum.DELIVER_MIDDLDETAILENULL.getMessage());
    }
    logger.info("searchOrderDelivedetailListPage--获取送货单信息：" + orderDeliverecord.toString() + "结束");
    // 4.查询模板名称
    logger.info("searchOrderDelivedetailListPage--fegin远程调用模板信息开始");
    AssetTmplInfo assetTmplInfo =
        (AssetTmplInfo)
            assetServiceFeign
                .searchAssetTmplInfoOne(orderDelivemiddle.getModuleId(), TokenUtil.getToken())
                .getData();
    if (assetTmplInfo == null) {
      return ResultVOUtil.returnFail(
          ResultEnum.FEGIN_TEMPLINFONULL.getCode(), ResultEnum.FEGIN_TEMPLINFONULL.getMessage());
    }
    logger.info(
        "searchOrderDelivedetailListPage--fegin远程调用模板信息：" + assetTmplInfo.toString() + "结束");
    page = orderDelivedetailMapper.selectPage(page, queryWrapper);
    List<OrderDelivedetail> resultList = page.getRecords();
    List<Long> assetIds = new ArrayList<>();
    for (OrderDelivedetail a : resultList) {
      assetIds.add(a.getAssetId());
    }
    // fegin调用资产服务，获取明细表格数据
    Map<String, Object> resultMap = new HashMap<>();
    if (assetIds.size() > 0) {
      AssetQuery assetQuery = new AssetQuery();
      assetQuery.setAssetIds(assetIds);
      assetQuery.setAssetTemplId(dto.getModuleId());
      logger.info("searchOrderDelivedetailListPage--fegin远程获取明细清单开始：" + assetIds.size() + "条");
      Map<String, List<Object>> map =
          (Map<String, List<Object>>)
              assetServiceFeign.searchEntityInfoPage(assetQuery, TokenUtil.getToken()).getData();
      logger.info("searchOrderDelivedetailListPage--fegin远程获取明细清单结束");
      if (map == null) {
        return ResultVOUtil.returnFail(
            ResultEnum.FEGIN_DETAILLISTNULL.getCode(),
            ResultEnum.FEGIN_DETAILLISTNULL.getMessage());
      }
      Page pageResult = new Page<>();
      pageResult.setRecords(map.get("content"));
      pageResult.setSize(page.getSize());
      pageResult.setTotal(page.getTotal());
      resultMap.put("content", pageResult);
      resultMap.put("title", map.get("title"));
      resultMap.put("detailCode", orderDelivemiddle.getDeliverydetailCode());
      resultMap.put("deliverName", orderDeliverecord.getDeliveryName());
      resultMap.put("moduleName", assetTmplInfo.getName());
    }
    return ResultVOUtil.returnSuccess(resultMap);
  }
}
