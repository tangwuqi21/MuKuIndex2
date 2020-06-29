package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.igen.acc.domain.dto.OrgUserDto;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.BeanCopyUtil;
import com.rhdk.purchasingservice.common.utils.NumberUtils;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.TokenUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.AssetServiceFeign;
import com.rhdk.purchasingservice.feign.InventoryServiceFeign;
import com.rhdk.purchasingservice.mapper.OrderContractMapper;
import com.rhdk.purchasingservice.mapper.OrderDelivemiddleMapper;
import com.rhdk.purchasingservice.mapper.OrderDeliverecordsMapper;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderDeliverecordsDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivemiddle;
import com.rhdk.purchasingservice.pojo.entity.OrderDeliverecords;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;
import com.rhdk.purchasingservice.pojo.query.OrderDeliverecordsQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDelivemiddleVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDeliverecordsVO;
import com.rhdk.purchasingservice.service.CommonService;
import com.rhdk.purchasingservice.service.IOrderDelivemiddleService;
import com.rhdk.purchasingservice.service.IOrderDeliverecordsService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  @Autowired private IOrderDelivemiddleService iOrderDelivemiddleService;

  @Autowired private OrderContractMapper orderContractMapper;

  @Autowired private OrderDelivemiddleMapper orderDelivemiddleMapper;

  @Autowired private CommonService commonService;

  @Autowired private AssetServiceFeign assetServiceFeign;

  @Autowired private InventoryServiceFeign inventoryServiceFeign;

  private static org.slf4j.Logger logger =
      LoggerFactory.getLogger(OrderDeliverecordsServiceImpl.class);

  /**
   * 送货记录列表查询 返回送货记录的签收状态、送货记录名称、合同相关信息 不包括送货记录明细
   *
   * @param dto
   * @return
   */
  @Override
  public IPage<OrderDeliverecordsVO> searchOrderDeliverecordsListPage(
      OrderDeliverecordsQuery dto, Long orgId) {
    Page page = new Page();
    page.setSize(dto.getPageSize());
    page.setCurrent(dto.getCurrentPage());
    logger.info("searchOrderDeliverecordsListPage--主列表信息查询开始");
    Long startT = System.currentTimeMillis();
    IPage<OrderDeliverecordsVO> recordsList =
        orderDeliverecordsMapper.selectRecordsList(page, dto, orgId);
    Long endT = System.currentTimeMillis();

    logger.info("searchOrderDeliverecordsListPage--主列表信息查询结束，共耗时：" + (endT - startT));
    Map<Long, String> supplierMap = new HashMap<>();
    Long startT2 = System.currentTimeMillis();
    logger.info("searchOrderDeliverecordsListPage--程序数据处理开始");
    List<HashMap<String, Object>> resultMap =
        (List<HashMap<String, Object>>)
            assetServiceFeign.getSupplyList(null, dto.getToken()).getData();
    logger.info("searchOrderDeliverecordsListPage--fegin远程获取供应商列表信息共：" + resultMap.size() + "条，结束");
    for (HashMap<String, Object> model : resultMap) {
      supplierMap.put(Long.valueOf(model.get("id").toString()), model.get("custName").toString());
    }
    // 获取源单信息，获取附件列表信息
    List<OrderDeliverecordsVO> resultList = recordsList.getRecords();
    logger.info("searchOrderDeliverecordsListPage--获取送货单信息开始");
    // 一次取出所有的合同信息
    List<OrderContractVO> contractVOList = orderContractMapper.selectContractById(null, null);
    Map<Long, OrderContractVO> contractVOMap = listToMap(contractVOList);
    // 一次取出所有的送货单签收状态
    List<Map<String, Object>> statusList = orderDelivemiddleMapper.getSignStatus();
    Map<String, Object> statusMap =
        ResultVOUtil.listToMap(statusList, "DELIVERY_ID", "SIGN_STATUS");
    resultList.forEach(
        temp -> {
          // 获取合同数据
          OrderContractVO orderContract = new OrderContractVO();
          if (contractVOMap.get(temp.getOrderId()) != null) {
            orderContract = contractVOMap.get(temp.getOrderId());
            temp.setContractType(orderContract.getContractType());
            temp.setContractCode(orderContract.getContractCode());
            temp.setContractName(orderContract.getContractName());
          } else {
            temp.setContractCode("--");
            temp.setContractName("--");
            temp.setContractType(0);
          }
          OrgUserDto userDto = commonService.getOrgUserById(temp.getOrgId(), temp.getCreateBy());
          // 获取送货单的签收状态，一个送货单下面关联多个明细单状态
          Integer status = 0;
          if (statusMap.get(temp.getId().toString()) != null) {
            status = Integer.valueOf(statusMap.get(temp.getId().toString()).toString());
          }
          // 获取附件列表
          OrderAttachmentDTO dto1 = new OrderAttachmentDTO();
          dto1.setAtttype(2);
          dto1.setParentId(temp.getId());
          temp.setAttachmentList(
              assetServiceFeign.selectListByParentId(dto1, dto.getToken()).getData());
          temp.setCreateName(userDto.getUserInfo().getName());
          temp.setDeptName(userDto.getGroupName());
          temp.setSignStatus(status);
          temp.setSupplierName(supplierMap.get(temp.getSupplierId()));
        });
    Long endT2 = System.currentTimeMillis();
    logger.info("searchOrderDeliverecordsListPage--程序处理数据结束，共耗时：" + +(endT2 - startT2));
    recordsList.setRecords(resultList);
    return recordsList;
  }

  public Map<Long, OrderContractVO> listToMap(List<OrderContractVO> contractVOList) {
    Map<Long, OrderContractVO> result = new HashMap<>();
    contractVOList.forEach(
        temp -> {
          result.put(temp.getOrderId(), temp);
        });
    return result;
  }

  /**
   * 根据送货记录id查询送货记录详情信息，返回送货记录基本信息，合同基本信息 送货记录下的明细记录基本信息
   *
   * @param id
   * @return
   */
  @Override
  public ResponseEnvelope searchOrderDeliverecordsOne(Long id) {
    OrderDeliverecordsVO orderDeliverecordsVO = new OrderDeliverecordsVO();
    OrderDeliverecordsQuery deliverecordsQuery = new OrderDeliverecordsQuery();
    deliverecordsQuery.setToken(TokenUtil.getToken());
    deliverecordsQuery.setId(id);
    logger.info("searchOrderDeliverecordsOne--获取单个送货单信息开始");
    IPage<OrderDeliverecordsVO> result = null;
    try {
      result =
          searchOrderDeliverecordsListPage(
              deliverecordsQuery, TokenUtil.getUserInfo().getOrganizationId());
      if (result != null && result.getRecords().size() > 0) {
        orderDeliverecordsVO = result.getRecords().get(0);
      }
    } catch (Exception e) {
      throw new RuntimeException("获取单个送货详情出错！送货单id为：" + id);
    }
    logger.info("searchOrderDeliverecordsOne--获取单个送货单信息结束");
    // 添加送货记录明细信息
    OrderDelivemiddleQuery orderDelivemiddleQuery = new OrderDelivemiddleQuery();
    orderDelivemiddleQuery.setDeliveryId(orderDeliverecordsVO.getId());
    orderDelivemiddleQuery.setPageSize(999999999);
    orderDelivemiddleQuery.setToken(deliverecordsQuery.getToken());
    IPage<OrderDelivemiddleVO> page = null;
    try {
      page =
          iOrderDelivemiddleService.searchOrderDelivemiddleListPage(
              orderDelivemiddleQuery, TokenUtil.getUserInfo().getOrganizationId());
    } catch (Exception e) {
      throw new RuntimeException("获取送货单明细列表数据失败！送货单id为：" + id + "，失败信息为：" + e.getMessage());
    }
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
  public void addOrderDeliverecords(OrderDeliverecordsDTO dto) {
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
    // 保存送货记录附件信息
    if (StringUtils.isEmpty(entity.getId())) {
      throw new RuntimeException("新增送货单失败，请检查入参信息");
    }
    if (dto.getAttachmentList().size() > 0) {
      for (OrderAttachmentDTO model : dto.getAttachmentList()) {
        model.setParentId(entity.getId());
        model.setAtttype(2);
      }
      assetServiceFeign.addBeatchAtta(dto.getAttachmentList(), TokenUtil.getToken()).getCode();
    }
    logger.info("addOrderDeliverecords--新增送货单信息结束");
    // 循环保存送货记录明细基本信息，这里需要判断该资产是物管还是量管，物管需要有对应的明细Excel，量管可以没有对应的附件
    //    if (CollectionUtils.isEmpty(dto.getOrderDelivemiddleDTOList())) {
    //      logger.error("送货明细记录为空，关联送货单id为：" + entity.getId());
    //      throw new RuntimeException(ResultEnum.DETAIL_NOTNULL.getMessage());
    //    }
    logger.info("addOrderDeliverecords--新增送货单明细信息开始");
    for (OrderDelivemiddleDTO delivemiddleDTO : dto.getOrderDelivemiddleDTOList()) {
      delivemiddleDTO.setDeliveryId(entity.getId());
      delivemiddleDTO.setToken(TokenUtil.getToken());
      OrderDelivemiddle result = iOrderDelivemiddleService.addOrderDelivemiddle(delivemiddleDTO);
      if (StringUtils.isEmpty(result.getId())) {
        break;
      }
    }
    logger.info("addOrderDeliverecords--新增送货单明细信息结束");
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
  @LcnTransaction
  public ResponseEnvelope updateOrderDeliverecords(OrderDeliverecordsDTO dto) {
    if (StringUtils.isEmpty(dto.getId())) {
      return ResultVOUtil.returnFail(
          ResultEnum.ID_NOTNULL.getCode(), ResultEnum.ID_NOTNULL.getMessage());
    }
    OrderDeliverecords entity = this.selectOne(dto.getId());
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    entity.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
    // 更新送货记录内容
    int num = orderDeliverecordsMapper.updateById(entity);
    if (num <= 0) {
      throw new RuntimeException("更新送货单记录失败，请检查相关参数，记录id为：" + dto.getId());
    }
    // 1.先将之前的附件列表统一删除，
    OrderAttachmentDTO orderAttachmentDTO = new OrderAttachmentDTO();
    orderAttachmentDTO.setParentId(dto.getId());
    orderAttachmentDTO.setAtttype(2);
    try {
      assetServiceFeign.deleteAttachmentByParentId(orderAttachmentDTO, TokenUtil.getToken());
    } catch (Exception e) {
      throw new RuntimeException("删除送货单附件失败，合同id为：" + dto.getId());
    }
    // 2.新增送货记录附件内容
    if (dto.getAttachmentList().size() > 0) {
      for (OrderAttachmentDTO model : dto.getAttachmentList()) {
        model.setParentId(dto.getId());
        model.setAtttype(2);
      }
      assetServiceFeign.addBeatchAtta(dto.getAttachmentList(), TokenUtil.getToken()).getCode();
    }
    // 更新中间表相关字段
    // 本次修改的id集合
    List<Long> middleList = new ArrayList<>();
    for (OrderDelivemiddleDTO model : dto.getOrderDelivemiddleDTOList()) {
      // 获取已存在的明细id集合
      if (!StringUtils.isEmpty(model.getId())) {
        middleList.add(model.getId());
      }
    }
    // 这里需要判断是否存在待删除的明细数据
    List<Long> middleIds = iOrderDelivemiddleService.getMIdsByDeliveryId(dto.getId());
    Map<String, Object> signStatMap = null;
    if (middleIds.size() > 0) {
      signStatMap = iOrderDelivemiddleService.checkReceiveIsExist(middleIds);
    }
    for (Long mid : middleIds) {
      // 不包含的数据则进行删除,这里需要排除掉本次新增的记录
      if (!middleList.contains(mid)) {
        try {
          iOrderDelivemiddleService.deleteOrderDetailrecords(mid);
        } catch (Exception e) {
          throw new RuntimeException("修改送货单删除之前历史送货明细信息失败！送货单明细id为：" + mid);
        }
        // 通知签收模块进行数据删除操作
        try {
          if (signStatMap != null && !StringUtils.isEmpty(signStatMap.get(mid.toString()))) {
            Integer dataId = Integer.valueOf(signStatMap.get(mid.toString()).toString());
            inventoryServiceFeign.deleteReceiveOne(dataId, TokenUtil.getToken());
          }
        } catch (Exception e) {
          throw new RuntimeException("修改送货单删除签收暂存状态信息失败！送货单明细id为：" + mid);
        }
      }
    }
    // 这里进行明细记录的新增或者修改操作
    for (OrderDelivemiddleDTO model : dto.getOrderDelivemiddleDTOList()) {
      // 明细id如果为空则进行明细的新增操作
      if (StringUtils.isEmpty(model.getId())) {
        model.setDeliveryId(entity.getId());
        iOrderDelivemiddleService.addOrderDelivemiddle(model);
      } else {
        iOrderDelivemiddleService.updateOrderMiddle(model);
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
  @LcnTransaction
  public ResponseEnvelope deleteOrderDeliverecords(Long id) {
    int num = orderDeliverecordsMapper.deleteById(id);
    if (num <= 0) {
      throw new RuntimeException("删除送货单信息失败！送货单id为：" + id);
    }
    OrderAttachmentDTO orderAttachmentDTO = new OrderAttachmentDTO();
    orderAttachmentDTO.setAtttype(2);
    orderAttachmentDTO.setParentId(id);
    try {
      assetServiceFeign.deleteAttachmentByParentId(orderAttachmentDTO, TokenUtil.getToken());
    } catch (Exception e) {
      throw new RuntimeException("删除送货单附件信息失败！送货单id为：" + id + "，报错信息为：" + e.getMessage());
    }
    // 这里根据送货单id来获取送货单下的所有明细id集合，然后循环删除明细清单列表
    List<Long> detailIds = iOrderDelivemiddleService.getMIdsByDeliveryId(id);
    for (Long detailId : detailIds) {
      try {
        iOrderDelivemiddleService.deleteOrderDetailrecords(detailId);
      } catch (Exception e) {
        throw new RuntimeException("删除送货单明细信息失败！送货单明细id为：" + detailId);
      }
    }
    return ResultVOUtil.returnSuccess();
  }

  @Override
  public List<OrderDeliverecordsVO> getDeliverInforList(OrderDeliverecordsQuery dto, Long orgId) {
    List<OrderDeliverecordsVO> orderDeliverecordsVOList = new ArrayList<>();
    dto.setOrgId(orgId);
    orderDeliverecordsVOList = orderDeliverecordsMapper.selectRecordsList2(dto);
    Map<Long, String> supplierMap = new HashMap<>();
    logger.info("searchOrderDeliverecordsListPage--fegin远程获取供应商列表信息开始");
    List<HashMap<String, Object>> resultMap =
        (List<HashMap<String, Object>>)
            assetServiceFeign.getSupplyList(null, dto.getToken()).getData();
    logger.info("searchOrderDeliverecordsListPage--fegin远程获取供应商列表信息共：" + resultMap.size() + "条，结束");
    for (HashMap<String, Object> model : resultMap) {
      supplierMap.put(Long.valueOf(model.get("id").toString()), model.get("custName").toString());
    }
    // 一次取出所有合同信息
    List<OrderContractVO> contractVOList = orderContractMapper.selectContractById(null, null);
    Map<Long, OrderContractVO> contractVOMap = listToMap(contractVOList);
    // 一次取出所有的送货单签收状态
    List<Map<String, Object>> statusList = orderDelivemiddleMapper.getSignStatus();
    Map<String, Object> statusMap =
        ResultVOUtil.listToMap(statusList, "DELIVERY_ID", "SIGN_STATUS");
    orderDeliverecordsVOList.forEach(
        a -> {
          OrderContractVO orderContract = new OrderContractVO();
          if (contractVOMap.get(a.getOrderId()) != null) {
            orderContract = contractVOMap.get(a.getOrderId());
            a.setContractTypeName(orderContract.getContractType() == 1 ? "采购合同" : "其他");
            a.setContractCode(orderContract.getContractCode());
            a.setContractName(orderContract.getContractName());
          } else {
            a.setContractCode("--");
            a.setContractName("--");
            a.setContractTypeName("--");
          }
          OrgUserDto userDto = commonService.getOrgUserById(a.getOrgId(), a.getCreateBy());
          Integer status = 0;
          if (statusMap.get(a.getId().toString()) != null) {
            status = Integer.valueOf(statusMap.get(a.getId().toString()).toString());
          }
          a.setSignStatus(status);
          OrderAttachmentDTO dto1 = new OrderAttachmentDTO();
          dto1.setParentId(a.getId());
          dto1.setAtttype(2);
          a.setHaveFile(
              assetServiceFeign.selectAttachNum(dto1, dto.getToken()).getData() > 0 ? "是" : "否");
          a.setCreateName(userDto.getUserInfo().getName());
          a.setDeptName(userDto.getGroupName());
          a.setSignStatusName(status == 2 ? "已签收" : status == 1 ? "部分签收" : "未签收");
          a.setSupplierName(supplierMap.get(a.getSupplierId()));
        });
    return orderDeliverecordsVOList;
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
