package com.rhdk.purchasingservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderDeliverecordsDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderDeliverecords;
import com.rhdk.purchasingservice.pojo.query.OrderDeliverecordsQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderDeliverecordsVO;

import java.util.List;

/**
 * 送货单 服务类
 *
 * @author LMYOU
 * @since 2020-05-12
 */
public interface IOrderDeliverecordsService extends IService<OrderDeliverecords> {
  /**
   * 送货记录列表查询 返回送货记录的签收状态、送货记录名称、合同相关信息 不包括送货记录明细
   *
   * @param DTO
   * @return
   */
  IPage<OrderDeliverecordsVO> searchOrderDeliverecordsListPage(
      OrderDeliverecordsQuery DTO, Long orgId);

  /**
   * 根据送货记录id查询送货记录详情信息，返回送货记录基本信息，合同基本信息 送货记录下的明细记录基本信息
   *
   * @param id
   * @return
   */
  ResponseEnvelope searchOrderDeliverecordsOne(Long id);

  /**
   * 添加送货记录信息，需要填写送货记录基本信息、送货明细基本信息，还有相关附件信息
   *
   * @param DTO
   * @return
   * @throws Exception
   */
  void addOrderDeliverecords(OrderDeliverecordsDTO DTO) throws Exception;

  /**
   * 送货记录更新接口，包含送货记录基本信息的更新 送货明细基本信息的更新 明细清单的资产信息更新
   *
   * @param DTO
   * @return
   * @throws Exception
   */
  ResponseEnvelope updateOrderDeliverecords(OrderDeliverecordsDTO DTO) throws Exception;

  /**
   * 根据送货记录id同步进行送货记录的逻辑删除 同步删除送货记录的相关附件信息 送货记录下的明细信息删除 送货记录的相关附件信息删除
   *
   * @param id
   * @return
   * @throws Exception
   */
  ResponseEnvelope deleteOrderDeliverecords(Long id);

  /**
   * 根据搜索条件获取要导出的送货单列表数据
   *
   * @param dto
   * @return
   */
  List<OrderDeliverecordsVO> getDeliverInforList(OrderDeliverecordsQuery dto, Long orgId);
}
