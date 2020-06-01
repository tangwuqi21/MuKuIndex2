package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.*;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.AssetServiceFeign;
import com.rhdk.purchasingservice.mapper.*;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.entity.*;
import com.rhdk.purchasingservice.pojo.query.AssetQuery;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;
import com.rhdk.purchasingservice.pojo.query.TmplPrptsFilter;
import com.rhdk.purchasingservice.pojo.vo.AssetCatVO;
import com.rhdk.purchasingservice.pojo.vo.AssetEntityInfoVO;
import com.rhdk.purchasingservice.pojo.vo.AssetTmplInfoVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDelivemiddleVO;
import com.rhdk.purchasingservice.service.IOrderDelivemiddleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 送货记录明细中间表 服务实现类
 *
 * @author LMYOU
 * @since 2020-05-13
 */
@Slf4j
@Transactional
@Service
public class OrderDelivemiddleServiceImpl
    extends ServiceImpl<OrderDelivemiddleMapper, OrderDelivemiddle>
    implements IOrderDelivemiddleService {

  @Autowired private OrderDelivemiddleMapper orderDelivemiddleMapper;

  @Autowired private OrderAttachmentMapper orderAttachmentMapper;

  @Autowired private OrderDelivedetailMapper orderDelivedetailMapper;

  @Autowired private OrderDeliverecordsMapper orderDeliverecordsMapper;

  @Autowired private PurcasingContractMapper purcasingContractMapper;

  @Autowired private OrderContractMapper orderContractMapper;

  @Autowired private AssetServiceFeign assetServiceFeign;

  @Autowired private RedisTemplate redisTemplate;

  private static org.slf4j.Logger logger =
      LoggerFactory.getLogger(OrderDelivemiddleServiceImpl.class);

  /**
   * 获取资产明细中间表列表信息 同步获取送货单中的合同信息
   *
   * @param dto
   * @return
   */
  @Override
  public ResponseEnvelope searchOrderDelivemiddleListPage(OrderDelivemiddleQuery dto) {
    Page<OrderDelivemiddle> page = new Page<OrderDelivemiddle>();
    page.setSize(dto.getPageSize());
    page.setCurrent(dto.getCurrentPage());
    QueryWrapper<OrderDelivemiddle> queryWrapper = new QueryWrapper<OrderDelivemiddle>();
    OrderDelivemiddle entity = new OrderDelivemiddle();
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    queryWrapper.setEntity(entity);
    page = orderDelivemiddleMapper.selectPage(page, queryWrapper);
    List<OrderDelivemiddle> resultList = page.getRecords();
    // 6.查询所属附件资产清单id集合
    Long[] arr = new Long[0];
    Map<String, String> assetIdMap = new HashMap<>();
    List<Map<String, Object>> resMap =
        assetServiceFeign.getEntityIdsByMid(arr, TokenUtil.getToken()).getData();
    for (Map<String, Object> mo : resMap) {
      assetIdMap.put(mo.get("MIDDLEID").toString(), mo.get("IDS").toString());
    }
    List<OrderDelivemiddleVO> orderDelivemiddleVOList =
        resultList.stream()
            .map(
                a -> {
                  // 改造
                  // 1.查询送货信息
                  OrderDeliverecords orderDeliverecord =
                      orderDeliverecordsMapper.getDeliverecordInfo(a.getDeliveryId());
                  // 2.查询合作伙伴信息
                  PurcasingContract purcasingContract =
                      purcasingContractMapper.selectById(orderDeliverecord.getOrderId());
                  // 3.查询合同信息
                  OrderContract orderContract = new OrderContract();
                  if (purcasingContract != null) {
                    orderContract =
                        orderContractMapper.selectById(purcasingContract.getContractId());
                  }
                  // 4.查询模板名称
                  AssetTmplInfoVO assetTmplInfo =
                      assetServiceFeign
                          .selectPrptValByTmplId(a.getModuleId(), TokenUtil.getToken())
                          .getData();
                  // 5.查询供应商名称
                  Customer customer =
                      assetServiceFeign
                          .searchCustomerOne(
                              orderDeliverecord.getSupplierId(), TokenUtil.getToken())
                          .getData();

                  AssetQuery assetQuery = new AssetQuery();
                  assetQuery.setAssetTempId(a.getModuleId());
                  assetQuery.setPrptIds(a.getPrptIds());
                  OrderAttachmentDTO attachmentDTO = new OrderAttachmentDTO();
                  attachmentDTO.setAtttype(3);
                  attachmentDTO.setParentId(a.getId());
                  OrderDelivemiddleVO model =
                      OrderDelivemiddleVO.builder()
                          .attachmentList(
                              assetServiceFeign
                                  .selectListByParentId(attachmentDTO, TokenUtil.getToken())
                                  .getData())
                          .deliveryCode(orderDeliverecord.getDeliveryCode())
                          .deliveryName(orderDeliverecord.getDeliveryName())
                          .supplierId(orderDeliverecord.getSupplierId())
                          .signAddress(orderDeliverecord.getSignAddress())
                          .build();
                  if (purcasingContract != null && orderContract != null) {
                    model.setContractCode(orderContract.getContractCode());
                    model.setContractName(orderContract.getContractName());
                    model.setContractType(orderContract.getContractType());
                  } else {
                    model.setContractCode("--");
                    model.setContractName("--");
                    model.setContractType(0);
                  }
                  if (assetTmplInfo != null) {
                    model.setPrptValues(assetTmplInfo.getUnit() + "," + assetTmplInfo.getPrice());
                    model.setModuleName(assetTmplInfo.getName());
                    model.setWmType(assetTmplInfo.getWmType());
                  }
                  if (customer != null) {
                    model.setSupplierName(customer.getCusName());
                  }
                  if (assetIdMap.size() > 0) {
                    String ass =
                        assetIdMap.get(a.getId().toString()) == null
                            ? ""
                            : assetIdMap.get(a.getId().toString());
                    model.setAssetIds(ass);
                  }
                  BeanCopyUtil.copyPropertiesIgnoreNull(a, model);
                  return model;
                })
            .collect(Collectors.toList());
    Page<OrderDelivemiddleVO> page2 = new Page<OrderDelivemiddleVO>();
    page2.setRecords(orderDelivemiddleVOList);
    page2.setSize(page.getSize());
    page2.setCurrent(page.getCurrent());
    page2.setTotal(page.getTotal());
    page2.setOrders(page.getOrders());
    return ResultVOUtil.returnSuccess(page2);
  }

  /**
   * 获取单个的送货中间表明细信息 需要获取送货单信息 获取模板信息 获取供应商信息
   *
   * @param id
   * @return
   */
  @Override
  public ResponseEnvelope searchOrderDelivemiddleOne(Long id) {
    OrderDelivemiddleVO model = new OrderDelivemiddleVO();
    OrderDelivemiddle orderDelivemiddle = this.selectOne(id);
    if (orderDelivemiddle != null) {
      // 1.查询送货信息
      OrderDeliverecords orderDeliverecord =
          orderDeliverecordsMapper.getDeliverecordInfo(orderDelivemiddle.getDeliveryId());
      // 2.查询合作伙伴信息
      PurcasingContract purcasingContract =
          purcasingContractMapper.selectById(orderDeliverecord.getOrderId());
      // 3.查询合同信息
      OrderContract orderContract = new OrderContract();
      if (purcasingContract != null) {
        orderContract = orderContractMapper.selectById(purcasingContract.getContractId());
      }
      // 4.查询模板名称
      AssetTmplInfoVO assetTmplInfo =
          assetServiceFeign
              .selectPrptValByTmplId(orderDelivemiddle.getModuleId(), TokenUtil.getToken())
              .getData();
      // 5.查询供应商名称
      Customer customer =
          assetServiceFeign
              .searchCustomerOne(orderDeliverecord.getSupplierId(), TokenUtil.getToken())
              .getData();
      AssetQuery assetQuery = new AssetQuery();
      assetQuery.setAssetTempId(orderDelivemiddle.getModuleId());
      assetQuery.setPrptIds(orderDelivemiddle.getPrptIds());
      OrderAttachmentDTO orderAttachmentDTO = new OrderAttachmentDTO();
      orderAttachmentDTO.setParentId(id);
      orderAttachmentDTO.setAtttype(3);
      model =
          OrderDelivemiddleVO.builder()
              .attachmentList(
                  assetServiceFeign
                      .selectListByParentId(orderAttachmentDTO, TokenUtil.getToken())
                      .getData())
              .deliveryCode(orderDeliverecord.getDeliveryCode())
              .deliveryName(orderDeliverecord.getDeliveryName())
              .supplierId(orderDeliverecord.getSupplierId())
              .signAddress(orderDeliverecord.getSignAddress())
              .build();
      if (purcasingContract != null && orderContract != null) {
        model.setContractCode(orderContract.getContractCode());
        model.setContractName(orderContract.getContractName());
        model.setContractType(orderContract.getContractType());
      } else {
        model.setContractCode("--");
        model.setContractName("--");
        model.setContractType(0);
      }
      if (assetTmplInfo != null) {
        model.setPrptValues(assetTmplInfo.getUnit() + "," + assetTmplInfo.getPrice());
        model.setModuleName(assetTmplInfo.getName());
        model.setWmType(assetTmplInfo.getWmType());
      }
      if (customer != null) {
        model.setSupplierName(customer.getCusName());
      }
      BeanCopyUtil.copyPropertiesIgnoreNull(orderDelivemiddle, model);
    }
    return ResultVOUtil.returnSuccess(model);
  }

  /**
   * 添加单条明细记录信息 需要区分物管还是量管进行不同级别的入库操作
   *
   * @param dto
   * @return
   * @throws Exception
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public OrderDelivemiddle addOrderDelivemiddle(OrderDelivemiddleDTO dto) {
    OrderDelivemiddle entity = new OrderDelivemiddle();
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    // 这里自动生成送货明细业务编码，规则为：SHD+时间戳
    String code = NumberUtils.createCode("SHD");
    entity.setDeliverydetailCode(code);
    orderDelivemiddleMapper.insert(entity);
    if (StringUtils.isEmpty(entity.getId())) {
      throw new RuntimeException("送货明细记录插入失败！插入实体信息为：" + dto.toString());
    }
    // 进行附件清单的解析及数据源的入库操作
    dto.setId(entity.getId());
    if ("2".equals(dto.getWmType())) {
      // 这里进行资产实体表、资产实体属性值表、送货明细记录表，三表的状态变更
      logger.info("addOrderDelivemiddle--物管资产，同步更新已入库的资产状态：updateAssetStatus");
      Integer num = updateAssetStatus(dto);
      if (num == 0) {
        throw new RuntimeException("资产信息入库状态变更失败！关联签收对照表id为：" + dto.getId());
      }
      // 5.最后进行明细附件的入库
      for (OrderAttachmentDTO mo : dto.getAttachmentList()) {
        OrderAttachment orderAttachment = new OrderAttachment();
        orderAttachment.setParentId(entity.getId());
        orderAttachment.setAtttype(3);
        orderAttachment.setOrgfilename(mo.getOrgfilename());
        orderAttachment.setFileurl(mo.getFileurl());
        orderAttachmentMapper.insert(orderAttachment);
      }
    } else {
      logger.info("addOrderDelivemiddle--量管资产，添加量管资产信息");
      // 执行量管的数据记录,需要现判断该量管资产是否存在，若存在则更新该量管资产的数量，不存在就新增量管资产
      addOrUpdateNumberAsset(dto);
      logger.info("addOrderDelivemiddle--量管资产，添加量管资产信息结束");
    }
    return entity;
  }

  /**
   * 获取模板的个性表头列表
   *
   * @param moduleId
   * @return
   */
  @Override
  public List<Map<String, Object>> getTitleMap(Long moduleId) {
    return orderDelivemiddleMapper.getTitleMap(moduleId);
  }

  /**
   * 物理删除明细表下的明细附件，明细清单 资产实体，资产实体属性值信息
   *
   * @param id
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Integer deleteByPassNo(Long id) {
    // 根据送货单id查询出对应的所有明细清单
    List<Long> middleIds = orderDelivemiddleMapper.getMIdsByDeliveryId(id);
    if (middleIds.size() > 0) {
      // 逻辑删除送货明细表
      List<Long> assetIds = orderDelivedetailMapper.getAssetIdsByDId(middleIds);
      if (assetIds.size() > 0) {
        Long[] strArray = new Long[assetIds.size()];
        assetIds.toArray(strArray);
        orderDelivedetailMapper.updateDetailsDel(assetIds);
        // 逻辑删除资产实体表
        assetServiceFeign.updateEntitys(strArray, TokenUtil.getToken());
        // 逻辑删除资产实体属性值表
        assetServiceFeign.updateEntityprpts(strArray, TokenUtil.getToken());
      } else {
        return ResultVOUtil.returnFail().getCode();
      }
      // 逻辑删除送货中间表
      for (Long no : middleIds) {
        orderDelivemiddleMapper.deleteById(no);
        // 物理删除送货明细附件表
        OrderAttachmentDTO dto = new OrderAttachmentDTO();
        dto.setParentId(no);
        dto.setAtttype(3);
        assetServiceFeign.deleteAttachmentByParentId(dto, TokenUtil.getToken());
      }
      return ResultVOUtil.returnSuccess().getCode();
    } else {
      return ResultVOUtil.returnFail().getCode();
    }
  }

  @Override
  @Transactional
  public ResponseEnvelope deleteOrderDetailrecords(Long id) {
    // 逻辑删除送货中间表
    int num = orderDelivemiddleMapper.deleteById(id);
    if (num <= 0) {
      throw new RuntimeException("删除送货明细记录资产失败！明细id为：" + id);
    }
    // 逻辑删除送货明细附件表
    OrderAttachmentDTO dto = new OrderAttachmentDTO();
    dto.setAtttype(3);
    dto.setParentId(id);
    assetServiceFeign.deleteAttachmentByParentId(dto, TokenUtil.getToken());
    // 逻辑删除送货明细表
    List<Long> middleIds = new ArrayList<>();
    middleIds.add(id);
    List<Long> assetIds = orderDelivedetailMapper.getAssetIdsByDId(middleIds);
    Long[] strArray = new Long[assetIds.size()];
    assetIds.toArray(strArray);
    orderDelivedetailMapper.updateDetailsDel(assetIds);
    try {
      // 逻辑删除资产实体表
      assetServiceFeign.updateEntitys(strArray, TokenUtil.getToken());
      // 逻辑删除资产实体属性值表
      assetServiceFeign.updateEntityprpts(strArray, TokenUtil.getToken());
    } catch (Exception e) {
      throw new RuntimeException("远程调用fegin删除明细记录下的资产实体信息失败！明细id为：" + id);
    }
    return ResultVOUtil.returnSuccess();
  }

  @Override
  @Transactional
  public ResponseEnvelope updateOrderMiddle(OrderDelivemiddleDTO model) {
    OrderDelivemiddle entity = this.selectOne(model.getId());
    // 本次要更新的资产数量
    Long numT = model.getAssetNumber();
    // 老的资产数量
    Long numT2 = entity.getAssetNumber();
    BeanCopyUtil.copyPropertiesIgnoreNull(model, entity);
    // 更新送货记录内容
    int num = orderDelivemiddleMapper.updateById(entity);
    if (num <= 0) {
      throw new RuntimeException("更新送货明细记录失败！明细信息为：" + entity.toString());
    }
    // 更新送货记录表中的签收状态
    List<Integer> signStatList = orderDelivemiddleMapper.getSignStatus(entity.getDeliveryId());
    Integer status = 0;
    if (signStatList.size() == 1 && signStatList.contains(0)) {
      status = 0;
    } else if (signStatList.size() == 1 && signStatList.contains(2)) {
      status = 2;
    } else if (signStatList.size() >= 1) {
      status = 1;
    }
    OrderDeliverecords orderDeliverecords =
        orderDeliverecordsMapper.selectById(entity.getDeliveryId());
    orderDeliverecords.setSignStatus(status);
    orderDeliverecordsMapper.updateById(orderDeliverecords);
    // 判断是否切换了模板
    if (model.getModuleId() == entity.getModuleId()) {
      if ("2".equals(model.getWmType())) {
        // 物管状态的需要进行附件内容是否变化判断
        OrderAttachmentDTO dto = new OrderAttachmentDTO();
        dto.setAtttype(3);
        dto.setParentId(model.getId());
        List<Map<String, Object>> result =
            assetServiceFeign.selectListByParentId(dto, TokenUtil.getToken()).getData();
        if (!CollectionUtils.isEmpty(result)) {
          // 判断明细附件是否进行了改变，如果附件未发生改变，则不需要进行附件的明细记录更新，如果附件发生了改变，则更新送货记录明细附件列表
          Map<String, Object> attachmentInfo = result.get(0);
          String fileUrl = "";
          if (model.getAttachmentList().size() > 0) {
            fileUrl = model.getAttachmentList().get(0).getFileurl();
          }

          if (!fileUrl.equals(attachmentInfo.get("fileurl"))) {
            // 明细附件发生了改变，需要重新解析数据表格进行数据的上传
            // 通过明细中间表找到明细表，通过明细表，去到资产实体表中进行之前的数据删除，然后删除明细中间表的数据
            List<Long> detailAssetIds = orderDelivedetailMapper.getAssetIds(model.getId());
            try {
              Long[] strArray = new Long[detailAssetIds.size()];
              detailAssetIds.toArray(strArray);
              // 删除资产实体表相关信息
              assetServiceFeign.deleteEntitys(strArray, TokenUtil.getToken());
              // 删除资产实体属性值表
              assetServiceFeign.deleteEntityPrpts(strArray, TokenUtil.getToken());
              // 删除明细表的数据
              orderDelivedetailMapper.deleteDeliveDetails(detailAssetIds);
            } catch (Exception e) {
              throw new RuntimeException(
                  "物管资产明细记录附件变更，需要将之前的资产信息删除，远程调用fegin删除资产信息失败！要删除的资产id为："
                      + detailAssetIds.toString());
            }
            // 变更资产实体表，资产实体属性值表，送货明细表的三种资产状态）
            int num2 = updateAssetStatus(model);
            if (num2 <= 0) {
              throw new RuntimeException("物管资产明细记录附件变更，同步更新资产信息状态失败！入参信息为：" + model.toString());
            }
            // 更新附件表
            for (OrderAttachmentDTO mo : model.getAttachmentList()) {
              OrderAttachmentDTO orderAttachment = new OrderAttachmentDTO();
              orderAttachment.setParentId(model.getId());
              orderAttachment.setAtttype(3);
              orderAttachment.setFileurl(mo.getFileurl());
              orderAttachment.setOrgfilename(mo.getOrgfilename());
              orderAttachmentMapper.updateByParentIdAndType(orderAttachment);
            }
          }
        }
      } else {
        // 量管的资产，更新明细表和资产实体表中的数量
        // 这里需要根据明细表中的数据同步更新资产表中量管资产的数量
        Long assetNum = numT - numT2;
        List<Long> detailAssetIds = orderDelivedetailMapper.getAssetIds(model.getId());
        Long[] strArray = new Long[detailAssetIds.size()];
        detailAssetIds.toArray(strArray);
        if (!StringUtils.isEmpty(model.getAssetCatId())) {
          AssetCatVO assetCatVO =
              assetServiceFeign
                  .searchAssetCatOne(model.getAssetCatId(), TokenUtil.getToken())
                  .getData();
          model.setAssetCatSearchKey(assetCatVO.getSearchKey());
        }
        model.setAssetNumber(model.getAssetNumber());
        orderDelivedetailMapper.updateDetails(detailAssetIds, model);
        try {
          AssetQuery asset = new AssetQuery();
          asset.setAssetTemplId(model.getModuleId());
          asset.setAssetStatus(0);
          AssetEntityInfo assetInfo =
              assetServiceFeign.searchAssetEntityInfoOne(asset, TokenUtil.getToken()).getData();
          String assetIds = "";
          for (Long assetId : detailAssetIds) {
            assetIds += assetId + ",";
          }
          model.setAssetIds(assetIds);
          model.setOrgId(TokenUtil.getUserInfo().getOrganizationId().toString());
          Long numT3 = (assetInfo.getAmount() + assetNum);
          model.setAssetNumber(numT3);
          assetServiceFeign.updateEntityInfo(model, TokenUtil.getToken());
        } catch (Exception e) {
          throw new RuntimeException("物管资产实体信息变更出错！，要变更的资产信息为：" + model.toString());
        }
      }
    } else {
      // 判断切换后的模板类型是物管还是量管，物管更新资产实体表，资产实体属性值表，送货明细表的三种资产状态，量管新增一条数据
      if ("2".equals(model.getWmType())) {
        // 变更资产实体表，资产实体属性值表，送货明细表的三种资产状态）
        int num2 = updateAssetStatus(model);
        if (num2 <= 0) {
          throw new RuntimeException("切换模板物管资产同步更新资产信息状态失败！入参信息为：" + model.toString());
        }
      } else {
        // 执行量管的数据记录
        try {
          addOrUpdateNumberAsset(model);
        } catch (Exception e) {
          throw new RuntimeException("量管资产明细记录更新失败，失败信息为：" + e.getMessage());
        }
      }
      // 更新附件表
      for (OrderAttachmentDTO mo : model.getAttachmentList()) {
        OrderAttachmentDTO orderAttachment = new OrderAttachmentDTO();
        orderAttachment.setParentId(model.getId());
        orderAttachment.setAtttype(3);
        orderAttachment.setFileurl(mo.getFileurl());
        orderAttachment.setOrgfilename(mo.getOrgfilename());
        orderAttachmentMapper.updateByParentIdAndType(orderAttachment);
      }
    }
    return ResultVOUtil.returnSuccess();
  }

  // 执行量管资产的新增或修改操作
  public ResponseEnvelope addOrUpdateNumberAsset(OrderDelivemiddleDTO dto) {
    // 执行量管的数据记录,需要现判断该量管资产是否存在，若存在则更新该量管资产的数量，不存在就新增量管资产
    AssetQuery asset = new AssetQuery();
    asset.setAssetTemplId(dto.getModuleId());
    asset.setAssetStatus(0);
    try {
      AssetEntityInfo assetInfo =
          assetServiceFeign.searchAssetEntityInfoOne(asset, TokenUtil.getToken()).getData();
      AssetEntityInfo entityInfo = new AssetEntityInfo();
      entityInfo.setAssetCatId(dto.getAssetCatId());
      entityInfo.setAssetTemplId(dto.getModuleId());
      entityInfo.setAssetTemplVer(dto.getModuleVersion());
      entityInfo.setItemNo(dto.getItemNO());
      if(!StringUtils.isEmpty(dto.getModuleName())){
        entityInfo.setName(dto.getModuleName());
      }
      entityInfo.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
      entityInfo.setAssetStatus(0L);
      if (assetInfo != null) {
        // 量管资产存在走更新,数量累计
        entityInfo.setAmount(assetInfo.getAmount() + dto.getAssetNumber());
        entityInfo.setId(assetInfo.getId());
        try {
          entityInfo =
              assetServiceFeign.updateAssetEntityInfo(entityInfo, TokenUtil.getToken()).getData();
        } catch (Exception e) {
          throw new RuntimeException(
              "存在量管资产信息更新出错！参数信息为：" + entityInfo.toString() + "msg:" + e.getMessage());
        }
      } else {
        // 量管资产不存在，走新增
        // 1.入库资产实体表信息
        entityInfo.setAmount(dto.getAssetNumber());
        try {
          entityInfo = assetServiceFeign.addAssetEntityInfo(entityInfo, dto.getToken()).getData();
        } catch (Exception e) {
          throw new RuntimeException("插入量管资产信息到实体表出错！msg:" + e.getMessage());
        }
      }
      // 3.这里进行送货记录的详细表中入库
      OrderDelivedetail orderDelivedetail = new OrderDelivedetail();
      orderDelivedetail.setAssetId(entityInfo.getId());
      orderDelivedetail.setAssetNumber(dto.getAssetNumber());
      orderDelivedetail.setAssetCatId(dto.getAssetCatId());
      if(!StringUtils.isEmpty(dto.getModuleName())){
        orderDelivedetail.setAssetName(dto.getModuleName());
      }
      if (!StringUtils.isEmpty(dto.getAssetCatId())) {
        AssetCatVO assetCatVO =
            assetServiceFeign
                .searchAssetCatOne(dto.getAssetCatId(), TokenUtil.getToken())
                .getData();
        orderDelivedetail.setAssetCatSearchKey(assetCatVO.getSearchKey());
      }
      orderDelivedetail.setMiddleId(dto.getId());
      orderDelivedetail.setItemNo(dto.getItemNO());
      orderDelivedetailMapper.insert(orderDelivedetail);
      if (StringUtils.isEmpty(orderDelivedetail.getId())) {
        throw new RuntimeException("送货明细记录资产插入失败！插入资产信息为：" + orderDelivedetail.toString());
      }
    } catch (Exception e) {
      throw new RuntimeException(
          "判断量管资产是否存在出错！参数信息为：" + asset.toString() + "msg:" + e.getMessage());
    }
    return ResultVOUtil.returnSuccess();
  }

  /**
   * 明细附件上传校验
   *
   * @param file
   * @param moduleId
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseEnvelope uploadFileCheck(MultipartFile file, Long moduleId) {
    try {
      // 解析上传的Excel，并检查文件格式和内容是否正确
      Map<String, Object> resultMap = new HashMap<>();
      AssetTmplInfo assetTmplInfo =
          assetServiceFeign.searchAssetTmplInfoOne(moduleId, TokenUtil.getToken()).getData();
      String fileUrl = null;
      // 创建excel工作簿对象
      Workbook workbook = null;
      FormulaEvaluator formulaEvaluator = null;
      File excelFile = FileUtil.multipartFileToFile(file);
      InputStream is = new FileInputStream(excelFile);
      // 判断文件是xlsx还是xls
      if (excelFile.getName().endsWith("xlsx")) {
        workbook = new XSSFWorkbook(is);
        formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
      } else {
        workbook = new HSSFWorkbook(is);
        formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
      }
      // 判断excel文件打开是否正确
      if (workbook == null) {
        return ResultVOUtil.returnFail(
            ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
      }
      // 获取资产模板对应的个性表头信息
      List<Map<String, Object>> titleMap = getTitleMap(moduleId);
      // 获取资产模板所有的属性信息
      // List<Map<String, Object>> titleMap2 = orderDelivemiddleMapper.getTitleList(moduleId);
      Map<Integer, Integer> titleIdM = new HashMap<>();
      Map<Integer, String> titleNameM = new HashMap<>();
      Map<String, Integer> titleNameM2 = new HashMap<>();
      // 获取表头的下标
      List<Integer> collList = new ArrayList<>();
      for (int conum = 0; conum < titleMap.size(); conum++) {
        titleIdM.put(Integer.valueOf(titleMap.get(conum).get("PRPT_ORDER").toString()), conum);
        titleNameM2.put(titleMap.get(conum).get("NAME").toString(), conum);
        titleNameM.put(conum, titleMap.get(conum).get("NAME").toString());
        if (titleMap.get(conum).get("PK_FLAG") != null
            && Integer.valueOf(titleMap.get(conum).get("PK_FLAG").toString()) == 1) {
          collList.add(conum);
        }
      }
      Sheet sheet = workbook.getSheetAt(0);
      // 当前sheet页面为空,继续遍历
      if (sheet == null) {
        return ResultVOUtil.returnFail(
            ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
      }
      // 1.读取Excel的表头数据,比较模板是否一致
      Row row1 = sheet.getRow(0);
      boolean isExcel = true;
      // 比较表头大小是否一致
      if (titleNameM.size() != row1.getLastCellNum()) {
        return ResultVOUtil.returnFail(
            ResultEnum.TEMPLATE_NOTFORMAT.getCode(), ResultEnum.TEMPLATE_NOTFORMAT.getMessage());
      }
      // 比较表头内容是否一致
      for (int columnNum = 0; columnNum < row1.getLastCellNum(); columnNum++) {
        if (!titleNameM
            .get(columnNum)
            .equals(ExcleUtils.getValue(row1.getCell(columnNum), formulaEvaluator))) {
          isExcel = false;
          break;
        }
      }
      if (!isExcel) {
        return ResultVOUtil.returnFail(
            ResultEnum.TEMPLATE_NOTFORMAT.getCode(), ResultEnum.TEMPLATE_NOTFORMAT.getMessage());
      }
      // 2.检查是否存在空列值
      // 对于每个sheet，读取其中的每一行,从第二行开始读取
      resultMap.put("totalRow", sheet.getLastRowNum());
      boolean isRowNull = true;
      boolean isCellNull = true;
      boolean isDataT = true;
      boolean isDataT2 = true;
      int rowNo = 0;
      String cellMsg = "";
      Long startT = System.currentTimeMillis();
      System.out.println("开始解析：" + startT);
      List<AssetEntityInfoVO> assetEntityInfoVOList = new ArrayList<>();
      List<AssetEntityPrpt> assetEntityPrptList = new ArrayList<>();
      List<OrderDelivedetail> orderDelivedetailList = new ArrayList<>();
      for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
          rowNo = rowNum + 1;
          isRowNull = false;
          break;
        }
        // 入库每条资产实体对应的属性值（个性化的，不是共有的,从第二列开始）
        Set<String> collSet = new HashSet<String>();
        TmplPrptsFilter tmplPrptsFilter = new TmplPrptsFilter();
        tmplPrptsFilter.setTmplId(assetTmplInfo.getId());
        tmplPrptsFilter.setAssetTemplVer(assetTmplInfo.getVerNo());
        tmplPrptsFilter.setPkFlag(1);
        tmplPrptsFilter.setDefaultFlag(1);
        Set<String> valSet = new HashSet<>();
        try {
          valSet =
              assetServiceFeign.searchPKValByTmpId(tmplPrptsFilter, TokenUtil.getToken()).getData();
        } catch (Exception e) {
          throw new RuntimeException("fegin获取PK属性值出错，模板信息参数为：" + assetTmplInfo.toString());
        }
        String collStr = moduleId + "_";
        for (int columnNum = 0; columnNum < row.getLastCellNum(); columnNum++) {
          String cellValue = ExcleUtils.getValue(row.getCell(columnNum), formulaEvaluator);
          if (org.springframework.util.StringUtils.isEmpty(cellValue)) {
            isCellNull = false;
            cellMsg = "第" + (rowNum + 1) + "行，第" + (columnNum + 1) + "列";
            break;
          }
          // 3.检查Excel中是否存在重复行，根据数据库中模板属性pk_flag取值
          if (collList.contains(columnNum)) {
            collStr += cellValue;
          }
        }

        if (!collSet.add(collStr)) {
          isDataT = false;
          rowNo = rowNum + 1;
          break;
        }

        // 4.校验Excel中与库中的数据是否重复
        if (!valSet.add(collStr)) {
          // 存在抛出异常信息提醒
          isDataT2 = false;
          rowNo = rowNum + 1;
          break;
        }

        AssetEntityInfoVO colutMap =
            resoveRow(row, formulaEvaluator, assetTmplInfo, titleIdM, titleNameM2, titleMap);
        assetEntityInfoVOList.add(colutMap);
        orderDelivedetailList.add(colutMap.getOrderDelivedetail());
        assetEntityPrptList.addAll(colutMap.getAssetEntityPrptList());
      }

      if (!isRowNull) {
        return ResultVOUtil.returnFail(
            ResultEnum.TEMPLATE_CELLNULL.getCode(), "附件第" + rowNo + "行数据内容为空");
      }
      if (!isCellNull) {
        return ResultVOUtil.returnFail(
            ResultEnum.TEMPLATE_CELLNULL.getCode(), "附件" + cellMsg + "数据内容为空");
      }
      if (!isDataT) {
        return ResultVOUtil.returnFail(
            ResultEnum.TEMPLATE_ROWTWO.getCode(), "附件第" + rowNo + "行数据内容有重复");
      }
      if (!isDataT2) {
        return ResultVOUtil.returnFail(
            ResultEnum.TEMPLATE_ROWTWO.getCode(), "附件第" + rowNo + "行数据内容与系统库中重复");
      }
      // 这里进行批量插入的方法
      //      if (assetEntityInfoVOList.size() > 0) {
      //        Integer rowNum = orderDelivemiddleMapper.insertEntitysPlan(assetEntityInfoVOList);
      //        Integer rowNum2 = orderDelivemiddleMapper.insertPrptsPlan(assetEntityPrptList);
      //        Integer rowNum3 = orderDelivemiddleMapper.insertDetailsPlan(orderDelivedetailList);
      //        Long endT = System.currentTimeMillis();
      //        System.out.println(
      //            "结束解析："
      //                + endT
      //                + ",共用时："
      //                + (endT - startT) / 1000
      //                + ",资产条数为："
      //                + rowNum
      //                + ",属性值条数为："
      //                + rowNum2
      //                + ",明细条数为："
      //                + rowNum3);
      //      } else {
      //        return ResultVOUtil.returnFail(
      //            ResultEnum.TEMPLATE_CELLNULL.getCode(), "附件第" + rowNo + "行数据内容为空");
      //      }
      //      // 调用附件上传接口
      //      fileUrl = assetServiceFeign.uploadSingleFile(file, TokenUtil.getToken());
      resultMap.put("fileUrl", fileUrl);
      if (org.springframework.util.StringUtils.isEmpty(resultMap.get("fileUrl"))) {
        return ResultVOUtil.returnFail(
            ResultEnum.CREATE_FILEERROR.getCode(), ResultEnum.CREATE_FILEERROR.getMessage());
      }
      String assetIds = "";
      for (AssetEntityInfoVO vo : assetEntityInfoVOList) {
        assetIds += vo.getId() + ",";
      }
      resultMap.put("assetIds", assetIds);
      // 上传成功，删除无用文件
      excelFile.delete();
      return ResultVOUtil.returnSuccess(resultMap);
    } catch (Exception e) {
      throw new RuntimeException("模板参数配置有误，请联系管理员！");
    }
  }

  public OrderDelivemiddle selectOne(Long id) {
    OrderDelivemiddle entity = new OrderDelivemiddle();
    entity.setId(id);
    QueryWrapper<OrderDelivemiddle> queryWrapper = new QueryWrapper<>();
    queryWrapper.setEntity(entity);
    return orderDelivemiddleMapper.selectOne(queryWrapper);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Integer updateAssetStatus(OrderDelivemiddleDTO dto) {
    // 将对应的明细附件清单上传的资产进行状态改变，暂存-已提交
    // 从送货明细表中获取暂存的资产id集合
    Integer num1 = 0;
    List<Long> assetIds = new ArrayList<>();
    String[] arr = dto.getAssetIds().split(",");
    for (String mn : arr) {
      assetIds.add(Long.valueOf(mn));
    }
    logger.info("updateAssetStatus--获取待更新的资产id集合数目：" + assetIds.size());
    // 2.资产实体表，暂存状态变更为待签收状态
    dto.setAssetStatus(0);
    assetServiceFeign.updateEntitysStatus(dto, dto.getToken());
    // 4.资产明细表，更新资产明细表关联的签收对照表id
    AssetQuery assetQuery = new AssetQuery();
    assetQuery.setAssetIds(assetIds);
    assetQuery.setMiddleId(dto.getId());
    num1 = orderDelivedetailMapper.updateAssetStatus(assetQuery);
    logger.info("updateAssetStatus--获取更新的送货明细数目：" + num1);
    return num1;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseEnvelope deleteDetailFile(String dto) {
    if (StringUtils.isEmpty(dto)) {
      throw new RuntimeException("请求参数有误，明细资产ids字段不能为空！");
    }
    // 1.删除送货记录明细信息
    List<Long> assetIds = new ArrayList<>();
    String[] arr = dto.split(",");
    for (String mn : arr) {
      assetIds.add(Long.valueOf(mn));
    }
    if (assetIds.size() > 0) {
      Long[] strArray = new Long[assetIds.size()];
      assetIds.toArray(strArray);
      orderDelivedetailMapper.deleteDeliveDetails(assetIds);
      // 2.删除资产信息实体类
      assetServiceFeign.deleteEntitys(strArray, TokenUtil.getToken());
      // 3.删除资产属性值信息
      assetServiceFeign.deleteEntityPrpts(strArray, TokenUtil.getToken());
    }
    return ResultVOUtil.returnSuccess();
  }

  @Override
  public List<OrderDelivemiddleVO> getDeliverDetailList(OrderDelivemiddleQuery dto) {
    QueryWrapper<OrderDelivemiddle> queryWrapper = new QueryWrapper<OrderDelivemiddle>();
    OrderDelivemiddle entity = new OrderDelivemiddle();
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    queryWrapper.setEntity(entity);
    List<OrderDelivemiddle> resultList = orderDelivemiddleMapper.selectList(queryWrapper);
    Map<Long, String> supplierMap = new HashMap<>();
    logger.info("getDeliverDetailList--fegin远程获取供应商列表信息开始");
    List<HashMap<String, Object>> resultMap =
        (List<HashMap<String, Object>>)
            assetServiceFeign.getSupplyList(null, TokenUtil.getToken()).getData();
    logger.info("getDeliverDetailList--fegin远程获取供应商列表信息共：" + resultMap.size() + "条，结束");
    for (HashMap<String, Object> model : resultMap) {
      supplierMap.put(Long.valueOf(model.get("id").toString()), model.get("custName").toString());
    }
    List<OrderDelivemiddleVO> orderDelivemiddleVOList = new ArrayList<>();
    Integer rownum = 1;
    for (OrderDelivemiddle a : resultList) {
      // 改造
      // 1.查询送货信息
      OrderDeliverecords orderDeliverecord =
          orderDeliverecordsMapper.getDeliverecordInfo(a.getDeliveryId());
      // 4.查询模板名称
      AssetTmplInfoVO assetTmplInfo =
          assetServiceFeign.selectPrptValByTmplId(a.getModuleId(), TokenUtil.getToken()).getData();
      OrderAttachmentDTO attachmentDTO = new OrderAttachmentDTO();
      attachmentDTO.setAtttype(3);
      attachmentDTO.setParentId(a.getId());
      OrderDelivemiddleVO model =
          OrderDelivemiddleVO.builder()
              .haveFile(
                  assetServiceFeign.selectAttachNum(attachmentDTO, TokenUtil.getToken()).getData()
                          > 0
                      ? "是"
                      : "否")
              .deliveryCode(orderDeliverecord.getDeliveryCode())
              .deliveryName(orderDeliverecord.getDeliveryName())
              .signAddress(orderDeliverecord.getSignAddress())
              .build();
      if (assetTmplInfo != null) {
        model.setUnitVal(assetTmplInfo.getUnit());
        model.setPriceVal(assetTmplInfo.getPrice());
        model.setModuleName(assetTmplInfo.getName());
      }
      if (supplierMap != null) {
        model.setSupplierName(supplierMap.get(orderDeliverecord.getSupplierId()));
      }
      BeanCopyUtil.copyPropertiesIgnoreNull(a, model);
      model.setNo(rownum);
      model.setSignStatusName(
          a.getSignStatus() == 2 ? "已签收" : a.getSignStatus() == 1 ? "部分签收" : "未签收");
      orderDelivemiddleVOList.add(model);
      rownum += 1;
    }
    return orderDelivemiddleVOList;
  }

  @Async
  public AssetEntityInfoVO resoveRow(
      Row row,
      FormulaEvaluator formulaEvaluator,
      AssetTmplInfo assetTmplInfo,
      Map<Integer, Integer> titleIdM,
      Map<String, Integer> titleNameM2,
      List<Map<String, Object>> titleMap2) {
    // 检查没有问题，则暂存到资产实体表中，状态为暂存；资产属性值表中，状态为暂存；送货记录明细表中，状态为暂存；
    AssetEntityInfoVO entityInfo = new AssetEntityInfoVO();
    entityInfo.setAmount(1L);
    entityInfo.setId(orderDelivemiddleMapper.getEntitysKey());
    entityInfo.setAssetCatId(assetTmplInfo.getAssetCatId());
    entityInfo.setAssetTemplId(assetTmplInfo.getId());
    entityInfo.setAssetTemplVer(assetTmplInfo.getVerNo());
    entityInfo.setItemNo(assetTmplInfo.getItemNo());
    entityInfo.setDscp(assetTmplInfo.getDscp());
    entityInfo.setName(assetTmplInfo.getName());
    entityInfo.setAssetStatus(-2);
    entityInfo.setCreateBy(TokenUtil.getUserInfo().getUserId());
    entityInfo.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
    if (titleNameM2.get("名称") != null) {
      Cell cell = row.getCell(titleNameM2.get("名称"));
      entityInfo.setName(ExcleUtils.getValue(cell, formulaEvaluator));
    }
    // 1.入库资产实体表信息
    // 入库每条资产实体对应的属性值（每个属性都需要入到资产属性值表中）
    List<AssetEntityPrpt> assetEntityPrptList = new ArrayList<>();
    for (Map<String, Object> model : titleMap2) {
      int a = titleIdM.get(Integer.valueOf(model.get("PRPT_ORDER").toString()));
      String val = ExcleUtils.getValue(row.getCell(a), formulaEvaluator);
      if (StringUtils.isEmpty(val)) {
        continue;
      }
      // 存储个性属性
      AssetEntityPrpt assetEntityPrpt = new AssetEntityPrpt();
      assetEntityPrpt.setId(orderDelivemiddleMapper.getPrptsKey());
      assetEntityPrpt.setAssetId(entityInfo.getId());
      assetEntityPrpt.setPrptId(Long.valueOf(model.get("PRPT_ID").toString()));
      assetEntityPrpt.setCreateBy(entityInfo.getCreateBy());
      assetEntityPrpt.setVal(val);
      assetEntityPrpt.setCode(model.get("CODE").toString());
      assetEntityPrptList.add(assetEntityPrpt);
    }
    entityInfo.setAssetEntityPrptList(assetEntityPrptList);
    // 3.这里进行送货记录的详细表中入库
    OrderDelivedetail orderDelivedetail = new OrderDelivedetail();
    if (entityInfo.getName() != null) {
      orderDelivedetail.setAssetName(entityInfo.getName());
    }
    orderDelivedetail.setId(orderDelivemiddleMapper.getDetailsKey());
    orderDelivedetail.setAssetId(entityInfo.getId());
    orderDelivedetail.setAssetNumber(1L);
    orderDelivedetail.setAssetCatId(assetTmplInfo.getAssetCatId());
    if (!StringUtils.isEmpty(assetTmplInfo.getAssetCatId())) {
      AssetCatVO assetCatVO =
          assetServiceFeign
              .searchAssetCatOne(assetTmplInfo.getAssetCatId(), TokenUtil.getToken())
              .getData();
      orderDelivedetail.setAssetCatSearchKey(assetCatVO.getSearchKey());
    }
    orderDelivedetail.setCreateBy(entityInfo.getCreateBy());
    orderDelivedetail.setItemNo(assetTmplInfo.getItemNo());
    orderDelivedetail.setAssetName(assetTmplInfo.getName());
    if (titleNameM2.get("名称") != null) {
      Cell cell = row.getCell(titleNameM2.get("名称"));
      orderDelivedetail.setAssetName(ExcleUtils.getValue(cell, formulaEvaluator));
    }
    entityInfo.setOrderDelivedetail(orderDelivedetail);
    System.out.println(
        "id:" + Thread.currentThread().getId() + ",name:" + Thread.currentThread().getName());
    return entityInfo;
  }
}
