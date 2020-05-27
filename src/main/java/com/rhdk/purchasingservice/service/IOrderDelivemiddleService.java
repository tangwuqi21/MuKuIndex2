package com.rhdk.purchasingservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivemiddle;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 送货记录明细中间表 服务类
 *
 * @author LMYOU
 * @since 2020-05-13
 */
public interface IOrderDelivemiddleService extends IService<OrderDelivemiddle> {
  /**
   * 送货记录下的明细列表查询，返回送货明细基本信息 关联的送货单基本信息
   *
   * @param DTO
   * @return
   */
  ResponseEnvelope searchOrderDelivemiddleListPage(OrderDelivemiddleQuery DTO);

  /**
   * 根据送货记录下明细id来查询送货记录下单一明细的详情
   *
   * @param id
   * @return
   */
  ResponseEnvelope searchOrderDelivemiddleOne(Long id);

  /**
   * 送货记录下，添加单一明细信息
   *
   * @param DTO
   * @return
   * @throws Exception
   */
  ResponseEnvelope addOrderDelivemiddle(OrderDelivemiddleDTO DTO) throws Exception;

  /**
   * @param moduleId
   * @return
   */
  List<Map<String, Object>> getTitleMap(Long moduleId);

  /**
   * @param id
   * @return
   */
  Integer deleteByPassNo(Long id);

  /**
   * 根据送货明细id来删除送货记录下单一明细
   *
   * @param id
   * @return
   * @throws Exception
   */
  ResponseEnvelope deleteOrderDetailrecords(Long id);

  /**
   * 送货记录下，更新单一明细记录基本信息，包括更换明细附件，更换明细资产模板
   *
   * @param dto
   * @return
   * @throws Exception
   */
  ResponseEnvelope updateOrderMiddle(OrderDelivemiddleDTO dto) throws Exception;

  /**
   * 送货记录下，添加单一明细附件，对附件内容进行格式校验，数据校验。 并将校验玩的数据进行暂存入库
   *
   * @param file
   * @param moduleId
   * @return
   * @throws Exception
   */
  ResponseEnvelope uploadFileCheck(MultipartFile file, Long moduleId) throws Exception;

  /** @param dto */
  void updateAssetStatus(OrderDelivemiddleDTO dto);

  /**
   * 根据送货记录下明细id来删除送货记录下单一明细的附件信息
   *
   * @param dto
   * @return
   * @throws Exception
   */
  ResponseEnvelope deleteDetailFile(OrderDelivemiddleDTO dto);
}
