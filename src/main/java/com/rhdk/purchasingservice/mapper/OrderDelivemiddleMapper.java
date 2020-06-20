package com.rhdk.purchasingservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rhdk.purchasingservice.pojo.entity.AssetEntityPrpt;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivedetail;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivemiddle;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;
import com.rhdk.purchasingservice.pojo.vo.AssetEntityInfoVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDelivemiddleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 送货记录明细中间表 Mapper 接口
 *
 * @author LMYOU
 * @since 2020-05-13
 */
public interface OrderDelivemiddleMapper extends BaseMapper<OrderDelivemiddle> {

  List<Map<String, Object>> getTitleMap(@Param("moduleId") Long moduleId);

  List<Long> getMIdsByDeliveryId(@Param("id") Long id);

  List<Map<String, Object>> getSignStatus();

  Long getEntitysKey();

  Long getPrptsKey();

  Long getDetailsKey();

  Integer insertEntitysPlan(@Param("assetEntityList") List<AssetEntityInfoVO> assetEntityInfoVO);

  Integer insertPrptsPlan(@Param("assetPrptList") List<AssetEntityPrpt> assetEntityPrptList);

  Integer insertDetailsPlan(
      @Param("orderDetailList") List<OrderDelivedetail> orderDelivedetailList);

  IPage<OrderDelivemiddleVO> selectMiddleList(
      Page page, @Param("dto") OrderDelivemiddleQuery dto, @Param("orgId") Long orgId);

  List<OrderDelivemiddleVO> selectMiddleList2(@Param("dto") OrderDelivemiddleQuery dto);

  List<OrderDelivemiddle> selectListByIds(@Param("ids") List<Long> ids);
}
