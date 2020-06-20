package com.rhdk.purchasingservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rhdk.purchasingservice.pojo.entity.OrderDeliverecords;
import com.rhdk.purchasingservice.pojo.query.OrderDeliverecordsQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderDeliverecordsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 送货单 Mapper 接口
 *
 * @author LMYOU
 * @since 2020-05-12
 */
public interface OrderDeliverecordsMapper extends BaseMapper<OrderDeliverecords> {
  OrderDeliverecords getDeliverecordInfo(@Param("deliveryId") Long deliveryId);

  IPage<OrderDeliverecordsVO> selectRecordsList(
      IPage page, @Param("dto") OrderDeliverecordsQuery dto, @Param("orgId") Long orgId);

  List<OrderDeliverecords> getDeliverecordList();

  List<OrderDeliverecordsVO> selectRecordsList2(@Param("dto") OrderDeliverecordsQuery dto);
}
