package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.igen.acc.domain.dto.OrgUserDto;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.BeanCopyUtil;
import com.rhdk.purchasingservice.common.utils.NumberUtils;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.TokenUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.AssetServiceFeign;
import com.rhdk.purchasingservice.mapper.OrderAttachmentMapper;
import com.rhdk.purchasingservice.mapper.OrderContractMapper;
import com.rhdk.purchasingservice.mapper.OrderDelivemiddleMapper;
import com.rhdk.purchasingservice.mapper.OrderDeliverecordsMapper;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderDeliverecordsDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderAttachment;
import com.rhdk.purchasingservice.pojo.entity.OrderDeliverecords;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;
import com.rhdk.purchasingservice.pojo.query.OrderDeliverecordsQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDelivemiddleVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDeliverecordsVO;
import com.rhdk.purchasingservice.service.CommonService;
import com.rhdk.purchasingservice.service.IOrderAttachmentService;
import com.rhdk.purchasingservice.service.IOrderDelivemiddleService;
import com.rhdk.purchasingservice.service.IOrderDeliverecordsService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 送货单 服务实现类
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Slf4j
@Transactional
@Service
public class OrderDeliverecordsServiceImpl
    extends ServiceImpl<OrderDeliverecordsMapper, OrderDeliverecords>
    implements IOrderDeliverecordsService {

  @Autowired private OrderDeliverecordsMapper orderDeliverecordsMapper;

  @Autowired private IOrderAttachmentService iOrderAttachmentService;

  @Autowired private IOrderDelivemiddleService iOrderDelivemiddleService;

  @Autowired private OrderAttachmentMapper orderAttachmentMapper;

  @Autowired private OrderContractMapper orderContractMapper;

  @Autowired private OrderDelivemiddleMapper orderDelivemiddleMapper;

  @Autowired private CommonService commonService;

  @Autowired private AssetServiceFeign assetServiceFeign;

  private static org.slf4j.Logger logger =
      LoggerFactory.getLogger(OrderDeliverecordsServiceImpl.class);

  /**
   * 送货记录列表查询 返回送货记录的签收状态、送货记录名称、合同相关信息 不包括送货记录明细
   *
   * @param dto
   * @return
   */
  @Override
  public ResponseEnvelope searchOrderDeliverecordsListPage(OrderDeliverecordsQuery dto) {
    Page<OrderDeliverecords> page = new Page<OrderDeliverecords>();
    page.setSize(dto.getPageSize());
    page.setCurrent(dto.getCurrentPage());
    QueryWrapper<OrderDeliverecords> queryWrapper = new QueryWrapper<OrderDeliverecords>();
    OrderDeliverecords entity = new OrderDeliverecords();
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    queryWrapper.setEntity(entity);
    page = orderDeliverecordsMapper.selectPage(page, queryWrapper);
    Map<Long, String> supplierMap = new HashMap<>();
    logger.info("searchOrderDeliverecordsListPage--fegin远程获取供应商列表信息开始");
    List<HashMap<String, Object>> resultMap =
        (List<HashMap<String, Object>>)
            assetServiceFeign.getSupplyList(null, TokenUtil.getToken()).getData();
    logger.info("searchOrderDeliverecordsListPage--fegin远程获取供应商列表信息共：" + resultMap.size() + "条，结束");
    for (HashMap<String, Object> model : resultMap) {
      supplierMap.put(Long.valueOf(model.get("id").toString()), model.get("custName").toString());
    }
    // 获取源单信息，获取附件列表信息
    List<OrderDeliverecords> resultList = page.getRecords();
    logger.info("searchOrderDeliverecordsListPage--获取送货单信息开始");
    List<OrderDeliverecordsVO> orderDeliverecordsVOList =
        resultList.stream()
            .map(
                a -> {
                  OrderContractVO orderContract =
                      orderContractMapper.selectContractById(a.getOrderId());
                  OrgUserDto userDto = commonService.getOrgUserById(a.getOrgId(), a.getCreateBy());
                  List<Integer> signStatList = orderDelivemiddleMapper.getSignStatus(a.getId());
                  Integer status = getAssetStatus(signStatList);
                  OrderDeliverecordsVO orderDeliverecordsVO =
                      OrderDeliverecordsVO.builder()
                          .attachmentList(orderAttachmentMapper.selectListByParentId(a.getId(), 2))
                          .createName(userDto.getUserInfo().getName())
                          .deptName(userDto.getGroupName())
                          .build();
                  if (orderContract != null) {
                    orderDeliverecordsVO.setContractType(orderContract.getContractType());
                    orderDeliverecordsVO.setContractCode(orderContract.getContractCode());
                    orderDeliverecordsVO.setContractName(orderContract.getContractName());
                  } else {
                    orderDeliverecordsVO.setContractCode("--");
                    orderDeliverecordsVO.setContractName("--");
                    orderDeliverecordsVO.setContractType(0);
                  }
                  BeanCopyUtil.copyPropertiesIgnoreNull(a, orderDeliverecordsVO);
                  orderDeliverecordsVO.setSignStatus(status);
                  orderDeliverecordsVO.setSupplierName(supplierMap.get(a.getSupplierId()));
                  return orderDeliverecordsVO;
                })
            .collect(Collectors.toList());
    logger.info(
        "searchOrderDeliverecordsListPage--获取送货单信息共：" + orderDeliverecordsVOList.size() + "条，结束");
    Page<OrderDeliverecordsVO> page2 = new Page<OrderDeliverecordsVO>();
    page2.setRecords(orderDeliverecordsVOList);
    page2.setSize(page.getSize());
    page2.setCurrent(page.getCurrent());
    page2.setTotal(page.getTotal());
    page2.setOrders(page.getOrders());
    return ResultVOUtil.returnSuccess(page2);
  }

  /**
   * 根据送货记录id查询送货记录详情信息，返回送货记录基本信息，合同基本信息 送货记录下的明细记录基本信息
   *
   * @param id
   * @return
   */
  @Override
  public ResponseEnvelope searchOrderDeliverecordsOne(Long id) {
    OrderDeliverecords entity = this.selectOne(id);
    OrderDeliverecordsVO orderDeliverecordsVO = new OrderDeliverecordsVO();
    logger.info("searchOrderDeliverecordsOne--获取单个送货单信息开始");
    OrderContractVO orderContract = orderContractMapper.selectContractById(entity.getOrderId());
    OrgUserDto userDto = commonService.getOrgUserById(entity.getOrgId(), entity.getCreateBy());
    List<Integer> signStatList = orderDelivemiddleMapper.getSignStatus(id);
    Integer status = getAssetStatus(signStatList);
    BeanCopyUtil.copyPropertiesIgnoreNull(entity, orderDeliverecordsVO);
    if (orderContract != null) {
      orderDeliverecordsVO.setContractCode(orderContract.getContractCode());
      orderDeliverecordsVO.setContractName(orderContract.getContractName());
      orderDeliverecordsVO.setContractType(orderContract.getContractType());
    } else {
      orderDeliverecordsVO.setContractCode("--");
      orderDeliverecordsVO.setContractName("--");
      orderDeliverecordsVO.setContractType(0);
    }
    Map<Long, String> supplierMap = new HashMap<>();
    List<HashMap<String, Object>> resultMap =
        (List<HashMap<String, Object>>)
            assetServiceFeign.getSupplyList(null, TokenUtil.getToken()).getData();
    for (HashMap<String, Object> model : resultMap) {
      supplierMap.put(Long.valueOf(model.get("id").toString()), model.get("custName").toString());
    }
    orderDeliverecordsVO.setCreateName(userDto.getUserInfo().getName());
    orderDeliverecordsVO.setDeptName(userDto.getGroupName());
    orderDeliverecordsVO.setSignStatus(status);
    orderDeliverecordsVO.setSupplierName(supplierMap.get(entity.getSupplierId()));
    orderDeliverecordsVO.setAttachmentList(
        orderAttachmentMapper.selectListByParentId(entity.getId(), 2));
    // 添加送货记录明细信息
    OrderDelivemiddleQuery orderDelivemiddleQuery = new OrderDelivemiddleQuery();
    orderDelivemiddleQuery.setDeliveryId(entity.getId());
    orderDelivemiddleQuery.setPageSize(999999999);
    ResponseEnvelope result =
        iOrderDelivemiddleService.searchOrderDelivemiddleListPage(orderDelivemiddleQuery);
    Page<OrderDelivemiddleVO> page = (Page<OrderDelivemiddleVO>) result.getData();
    orderDeliverecordsVO.setDelivemiddleVOList(page.getRecords());
    return ResultVOUtil.returnSuccess(orderDeliverecordsVO);
  }

  /**
   * 添加送货单信息，同步添加 送货明细记录信息 送货明细清单信息 资产实体信息 资产实体属性值信息
   *
   * @param dto
   * @return
   * @throws Exception
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseEnvelope addOrderDeliverecords(OrderDeliverecordsDTO dto) throws Exception {
    // 保存送货记录基本信息
    OrderDeliverecords entity = new OrderDeliverecords();
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    entity.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
    // 这里自动生成送货记录业务编码，规则为：SH+时间戳
    if (StringUtils.isEmpty(entity.getDeliveryCode())) {
      String code = NumberUtils.createCode("SH");
      entity.setDeliveryCode(code);
    }
    logger.info("addOrderDeliverecords--新增送货单信息开始");
    orderDeliverecordsMapper.insert(entity);
    logger.info("addOrderDeliverecords--新增送货单信息结束");
    // 保存送货记录附件信息
    if (CollectionUtils.isEmpty(dto.getAttachmentList())) {
      return ResultVOUtil.returnFail(
          ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
    }
    for (OrderAttachmentDTO model : dto.getAttachmentList()) {
      model.setParentId(entity.getId());
      model.setAtttype(2);
    }
    List<Long> filelist =
        iOrderAttachmentService.insertAttachmentListOfIdList(dto.getAttachmentList());
    if (filelist.size() > 0) {
      // 循环保存送货记录明细基本信息，这里需要判断该资产是物管还是量管，物管需要有对应的明细Excel，量管可以没有对应的附件
      if (CollectionUtils.isEmpty(dto.getOrderDelivemiddleDTOList())) {
        return ResultVOUtil.returnFail(
            ResultEnum.DETAIL_NOTNULL.getCode(), ResultEnum.DETAIL_NOTNULL.getMessage());
      }
      logger.info("addOrderDeliverecords--新增送货单明细信息开始");
      for (OrderDelivemiddleDTO delivemiddleDTO : dto.getOrderDelivemiddleDTOList()) {
        delivemiddleDTO.setDeliveryId(entity.getId());
        delivemiddleDTO.setToken(TokenUtil.getToken());
        ResponseEnvelope result = iOrderDelivemiddleService.addOrderDelivemiddle(delivemiddleDTO);
        if (result.getCode() != 0) {
          break;
        }
      }
      logger.info("addOrderDeliverecords--新增送货单明细信息结束");
    } else {
      return ResultVOUtil.returnFail(ResultEnum.DETAIL_NOTNULL.getCode(), "保存附件发生异常");
    }
    return ResultVOUtil.returnSuccess();
  }

  /**
   * 更新送货单信息，同步更新送货单下的送货明细、送货附件、明细清单等相关信息
   *
   * @param dto
   * @return
   * @throws Exception
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseEnvelope updateOrderDeliverecords(OrderDeliverecordsDTO dto) throws Exception {
    if (StringUtils.isEmpty(dto.getId())) {
      return ResultVOUtil.returnFail(
          ResultEnum.ID_NOTNULL.getCode(), ResultEnum.ID_NOTNULL.getMessage());
    }
    OrderDeliverecords entity = this.selectOne(dto.getId());
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    entity.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
    // 更新送货记录内容
    orderDeliverecordsMapper.updateById(entity);
    // 更新送货记录附件内容
    if (CollectionUtils.isEmpty(dto.getAttachmentList())) {
      return ResultVOUtil.returnFail(
          ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
    }
    for (OrderAttachmentDTO model : dto.getAttachmentList()) {
      OrderAttachment orderAttachment = new OrderAttachment();
      model.setParentId(entity.getId());
      model.setAtttype(2);
      BeanCopyUtil.copyPropertiesIgnoreNull(model, orderAttachment);
      if (StringUtils.isEmpty(model.getId())) {
        orderAttachmentMapper.insert(orderAttachment);
      } else {
        orderAttachmentMapper.updateById(orderAttachment);
      }
    }
    // 更新中间表相关字段
    if (dto.getOrderDelivemiddleDTOList().size() > 0) {
      for (OrderDelivemiddleDTO model : dto.getOrderDelivemiddleDTOList()) {
        // 明细id如果为空则进行明细的新增操作
        if (StringUtils.isEmpty(model.getId())) {
          model.setDeliveryId(entity.getId());
          iOrderDelivemiddleService.addOrderDelivemiddle(model);
        } else {
          iOrderDelivemiddleService.updateOrderMiddle(model);
        }
      }
    }
    return ResultVOUtil.returnSuccess();
  }

  /**
   * 根据送货单id逻辑删除送货单下的送货明细、附件、清单等信息
   *
   * @param id
   * @return
   * @throws Exception
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseEnvelope deleteOrderDeliverecords(Long id) throws Exception {
    orderDeliverecordsMapper.deleteById(id);
    orderAttachmentMapper.deleteAttachmentByParentId(id, 2L);
    iOrderDelivemiddleService.deleteByPassNo(id);
    return ResultVOUtil.returnSuccess();
  }

  /**
   * 根据id来查询单个送货单信息
   *
   * @param id
   * @return
   */
  public OrderDeliverecords selectOne(Long id) {
    OrderDeliverecords entity = new OrderDeliverecords();
    entity.setId(id);
    QueryWrapper<OrderDeliverecords> queryWrapper = new QueryWrapper<>();
    queryWrapper.setEntity(entity);
    return orderDeliverecordsMapper.selectOne(queryWrapper);
  }

  /**
   * 获取送货单的签收状态
   *
   * @param signStatList
   * @return
   */
  public Integer getAssetStatus(List<Integer> signStatList) {
    Integer status = 0;
    if (signStatList.size() == 1 && signStatList.contains(0)) {
      status = 0;
    } else if (signStatList.size() == 1 && signStatList.contains(2)) {
      status = 2;
    } else if (signStatList.size() >= 1) {
      status = 1;
    }
    return status;
  }
}
