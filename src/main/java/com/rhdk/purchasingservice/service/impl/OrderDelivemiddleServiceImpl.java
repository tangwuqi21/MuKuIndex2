package com.rhdk.purchasingservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rhdk.purchasingservice.common.enums.Constants;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.*;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.AssetServiceFeign;
import com.rhdk.purchasingservice.feign.InventoryServiceFeign;
import com.rhdk.purchasingservice.mapper.*;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.entity.*;
import com.rhdk.purchasingservice.pojo.query.AssetQuery;
import com.rhdk.purchasingservice.pojo.query.EntityUpVo;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;
import com.rhdk.purchasingservice.pojo.query.TmplPrptsFilter;
import com.rhdk.purchasingservice.pojo.vo.AssetCatVO;
import com.rhdk.purchasingservice.pojo.vo.AssetEntityInfoVO;
import com.rhdk.purchasingservice.pojo.vo.AssetTmplInfoVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDelivemiddleVO;
import com.rhdk.purchasingservice.service.IOrderDelivemiddleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

  @Autowired private InventoryServiceFeign inventoryServiceFeign;

  @Autowired private RedisTemplate<String, String> redisTemplate;

  @Resource private RedisUtils redisUtils;

  private static org.slf4j.Logger logger =
      LoggerFactory.getLogger(OrderDelivemiddleServiceImpl.class);

  /**
   * 获取资产明细中间表列表信息 同步获取送货单中的合同信息
   *
   * @param dto
   * @return
   */
  @Override
  public IPage<OrderDelivemiddleVO> searchOrderDelivemiddleListPage(
      OrderDelivemiddleQuery dto, Long orgId) {
    Page page = new Page();
    page.setSize(dto.getPageSize());
    page.setCurrent(dto.getCurrentPage());
    IPage<OrderDelivemiddleVO> recordsList =
        orderDelivemiddleMapper.selectMiddleList(page, dto, orgId);
    List<OrderDelivemiddleVO> resultList = recordsList.getRecords();
    if (resultList.size() > 0) {
      List<Long> middleList = new ArrayList<>();
      resultList.forEach(
          a -> {
            middleList.add(a.getId());
          });
      Map<String, Object> signStatMap = checkReceiveIsExist(middleList);
      // 6.查询所属附件资产清单id集合
      List<Long> arr = new ArrayList<>();
      Map<String, String> assetIdMap = new HashMap<>();
      List<Map<String, Object>> resMap = orderDelivedetailMapper.getEntityIdsByMid(arr);
      for (Map<String, Object> mo : resMap) {
        assetIdMap.put(mo.get("MIDDLEID").toString(), mo.get("IDS").toString());
      }
      resultList.stream()
          .forEach(
              a -> {
                // 改造
                // 1.查询送货信息
                OrderDeliverecords orderDeliverecord =
                    orderDeliverecordsMapper.getDeliverecordInfo(a.getDeliveryId());
                // 3.查询合同信息
                OrderContract orderContract = new OrderContract();
                PurcasingContract purcasingContract =
                    purcasingContractMapper.selectById(orderDeliverecord.getOrderId());
                if (purcasingContract != null) {
                  orderContract = orderContractMapper.selectById(purcasingContract.getContractId());
                }
                a.setOrderId(orderDeliverecord.getOrderId());
                // 4.查询模板名称
                AssetTmplInfoVO assetTmplInfo = new AssetTmplInfoVO();
                if (redisTemplate.hasKey(Constants.TMPL_KEY + a.getModuleId())) {
                  assetTmplInfo =
                      JSON.parseObject(
                          redisUtils.get(Constants.TMPL_KEY + a.getModuleId()),
                          AssetTmplInfoVO.class);
                } else {
                  assetTmplInfo =
                      assetServiceFeign
                          .selectPrptValByTmplId(a.getModuleId(), dto.getToken())
                          .getData();
                }
                // 5.查询供应商名称,这里的客户信息从Redis中获取，若Redis中不存在则从库中取，同时更新到Redis中
                Customer customer = new Customer();
                if (redisTemplate.hasKey(Constants.CUST_KEY + orderDeliverecord.getSupplierId())) {
                  customer =
                      JSON.parseObject(
                          redisUtils.get(Constants.CUST_KEY + orderDeliverecord.getSupplierId()),
                          Customer.class);
                } else {
                  customer =
                      assetServiceFeign
                          .searchCustomerOne(orderDeliverecord.getSupplierId(), dto.getToken())
                          .getData();
                  redisUtils.set(
                      "CUST_" + orderDeliverecord.getSupplierId(),
                      JSON.toJSON(customer).toString());
                }
                AssetQuery assetQuery = new AssetQuery();
                assetQuery.setAssetTempId(a.getModuleId());
                assetQuery.setPrptIds(a.getPrptIds());
                OrderAttachmentDTO attachmentDTO = new OrderAttachmentDTO();
                attachmentDTO.setAtttype(3);
                attachmentDTO.setParentId(a.getId());
                a.setAttachmentList(
                    assetServiceFeign
                        .selectListByParentId(attachmentDTO, dto.getToken())
                        .getData());
                a.setDeliveryCode(orderDeliverecord.getDeliveryCode());
                a.setDeliveryName(orderDeliverecord.getDeliveryName());
                a.setSupplierId(orderDeliverecord.getSupplierId());
                a.setSignAddress(orderDeliverecord.getSignAddress());
                if (purcasingContract != null && orderContract != null) {
                  a.setContractCode(orderContract.getContractCode());
                  a.setContractName(orderContract.getContractName());
                  a.setContractType(orderContract.getContractType());
                } else {
                  a.setContractCode("--");
                  a.setContractName("--");
                  a.setContractType(0);
                }
                if (assetTmplInfo != null) {
                  a.setPrptValues(assetTmplInfo.getUnit() + "," + assetTmplInfo.getPrice());
                  if (assetTmplInfo.getUnit() != null) {
                    a.setUnitVal(assetTmplInfo.getUnit());
                    a.setUnitId(assetTmplInfo.getUnitId());
                  }
                  if (assetTmplInfo.getPrice() != null) {
                    a.setPriceVal(assetTmplInfo.getPrice());
                    a.setPriceId(assetTmplInfo.getPriceId());
                  }
                  a.setModuleName(assetTmplInfo.getName());
                  a.setWmType(assetTmplInfo.getWmType());
                }
                if (customer != null) {
                  a.setSupplierName(customer.getCusName());
                }
                if (assetIdMap.size() > 0) {
                  String ass =
                      assetIdMap.get(a.getId().toString()) == null
                          ? ""
                          : assetIdMap.get(a.getId().toString());
                  a.setAssetIds(ass);
                }
                if (signStatMap != null) {
                  String str =
                      signStatMap.get(a.getId().toString()) == null
                          ? ""
                          : signStatMap.get(a.getId().toString()).toString();
                  a.setSignRecord(str);
                }
              });
      recordsList.setRecords(resultList);
    }
    return recordsList;
  }

  /**
   * 获取明细签收状态Map值
   *
   * @param middleIds
   * @return
   */
  @Override
  public Map<String, Object> checkReceiveIsExist(List<Long> middleIds) {
    Long[] strArray = new Long[middleIds.size()];
    middleIds.toArray(strArray);
    Map<String, Object> resutl = new HashMap<>();
    try {
      resutl = inventoryServiceFeign.checkReceiveIsExist(strArray, TokenUtil.getToken()).getData();
    } catch (Exception e) {
      throw new RuntimeException("获取明细签收状态失败！明细id为：" + middleIds.toString());
    }

    return resutl;
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
    OrderDelivemiddleQuery orderDelivemiddleQuery = new OrderDelivemiddleQuery();
    orderDelivemiddleQuery.setId(id);
    orderDelivemiddleQuery.setToken(TokenUtil.getToken());
    IPage<OrderDelivemiddleVO> result = null;
    try {
      result =
          searchOrderDelivemiddleListPage(
              orderDelivemiddleQuery, TokenUtil.getUserInfo().getOrganizationId());
      if (result != null && result.getRecords().size() > 0) {
        model = result.getRecords().get(0);
      }
    } catch (Exception e) {
      throw new RuntimeException("获取单个送货明细详情出错！送货明细id为：" + id);
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
    String code = NumberUtils.createCode("MX");
    entity.setDeliverydetailCode(code);
    if (dto.getUnitId() != null && dto.getPriceId() != null) {
      entity.setPrptIds(dto.getUnitId() + "," + dto.getPriceId());
    }
    orderDelivemiddleMapper.insert(entity);
    if (StringUtils.isEmpty(entity.getId())) {
      throw new RuntimeException("送货明细记录插入失败！插入实体信息为：" + dto.toString());
    }
    // 进行附件清单的解析及数据源的入库操作
    dto.setId(entity.getId());
    if ("2".equals(dto.getWmType())) {
      // PK值校验，Redis数据同步入库
      logger.info("addOrderDelivemiddle--物管资产，同步更新Redis入库状态：insertAllEntityInfo");
      try {
        insertAllEntityInfo(
            dto.getPkValKey(), dto.getAssetKey(), dto.getModuleId(), entity.getId());
      } catch (Exception e) {
        throw new RuntimeException("资产信息系统缓存同步入库失败!" + e.getMessage());
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

  @Override
  @Transactional
  public ResponseEnvelope deleteOrderDetailrecords(Long id) {
    // 逻辑删除送货明细附件表
    OrderAttachmentDTO dto = new OrderAttachmentDTO();
    dto.setAtttype(3);
    dto.setParentId(id);
    assetServiceFeign.deleteAttachmentByParentId(dto, TokenUtil.getToken());
    // 逻辑删除送货明细表
    List<Long> middleIds = new ArrayList<>();
    middleIds.add(id);
    List<Long> assetIds = orderDelivedetailMapper.getAssetIdsByDId(middleIds);
    if (assetIds.size() > 0) {
      Long[] strArray = new Long[assetIds.size()];
      assetIds.toArray(strArray);
      orderDelivedetailMapper.updateDetailsDel(assetIds, TokenUtil.getUserInfo().getUserId(), id);
      // 这里需要区分明细的资产类型，量管的更新资产表中量管资产数量就行了
      OrderDelivemiddle orderDelivemiddle = orderDelivemiddleMapper.selectById(id);
      AssetTmplInfoVO assetTmplInfoVO = new AssetTmplInfoVO();
      if (redisTemplate.hasKey(Constants.TMPL_KEY + orderDelivemiddle.getModuleId())) {
        assetTmplInfoVO =
            JSON.parseObject(
                redisUtils.get(Constants.TMPL_KEY + orderDelivemiddle.getModuleId()),
                AssetTmplInfoVO.class);
      } else {
        assetTmplInfoVO =
            assetServiceFeign
                .selectPrptValByTmplId(orderDelivemiddle.getModuleId(), TokenUtil.getToken())
                .getData();
      }
      // 量管的资产类型需要去更新对应资产实体表中资产实体的数量，物管的删除不需要进行资产实体的相关删除
      if (assetTmplInfoVO != null && "1".equals(assetTmplInfoVO.getWmType())) {
        // 调用fegin更新量管资产数量
        EntityUpVo entityUpVo = new EntityUpVo();
        entityUpVo.setAssetTemplId(orderDelivemiddle.getModuleId());
        entityUpVo.setAmount(0 - orderDelivemiddle.getAssetNumber());
        entityUpVo.setAssetStatus(0);
        entityUpVo.setOriginalStatus(0);
        try {
          assetServiceFeign.updateEntityInfoStatus(entityUpVo, TokenUtil.getToken());
        } catch (Exception e) {
          throw new RuntimeException(
              "远程调用fegin更新量管资产数量失败！资产明细id为：" + id + ",资产模板id为：" + entityUpVo.getAssetTemplId());
        }
      } else {
        // 物管同步更新数据状态
        // 这里同步对Redis的PK值进行删除
        TmplPrptsFilter tmplPrptsFilter = new TmplPrptsFilter();
        tmplPrptsFilter.setTmplId(orderDelivemiddle.getModuleId());
        Set<String> valSet =
            assetServiceFeign.searchPKValByTmpId(tmplPrptsFilter, TokenUtil.getToken()).getData();
        if (valSet.size() > 0) {
          for (String str : valSet) {
            redisUtils.delete(str);
          }
        }
        // 同步更新状态为0的资产实体信息和资产实体属性值信息
        // 2.逻辑删除资产信息实体类
        Integer rownum =
            assetServiceFeign.deleteEntitys(strArray, 0, TokenUtil.getToken()).getData();
        // 3.逻辑删除资产属性值信息
        if (rownum > 0) {
          assetServiceFeign.deleteEntityPrpts(strArray, TokenUtil.getToken());
        }
      }
    }
    // 逻辑删除送货中间表
    int num = orderDelivemiddleMapper.deleteById(id);
    if (num <= 0) {
      throw new RuntimeException("删除送货明细记录资产失败！明细id为：" + id);
    }
    return ResultVOUtil.returnSuccess();
  }

  /**
   * @param model
   * @return
   */
  @Override
  @Transactional
  public ResponseEnvelope updateOrderMiddle(OrderDelivemiddleDTO model) {
    OrderDelivemiddle entity = this.selectOne(model.getId());
    // 针对量管本次要更新的资产数量
    Long numT = model.getAssetNumber();
    // 针对量管老的资产数量
    Long numT2 = entity.getAssetNumber();
    // 入参封装
    if (model.getUnitId() != null && model.getPriceId() != null) {
      entity.setPrptIds(model.getUnitId() + "," + model.getPriceId());
    }
    // 明细记录变更，分以下几种情况：
    // 若切换模板前的资产类型是物管，切换后需要将之前的明细记录逻辑删除，对应资产状态若为待签收状态的也一并删除；
    // 若切换模板前的资产类型是量管，切换后需要将之前的明细记录逻辑删除，对应资产信息不需要删除；
    // 1、切换了模板
    // （1）判断资产类型是物管还是量管，物管的需要走物管的新增逻辑；若是量管的则同步更新量管的资产数量即可；
    // 2、未切换模板
    // （1）判断是否是物管的资产类型，物管的资产类型需要判断是否重新上传了明细附件，若重新上传了明细附件，则需要将之前的明细附件对应的资产
    // 状态为待签收的数据进行逻辑删除；然后重新走物管的新增逻辑；若是量管的则同步更新资产数量即可

    // 判断是否切换了模板
    if (model.getModuleId().equals(entity.getModuleId())) {
      logger.info("updateOrderMiddle--进入未切换模板环节");
      // 未切换模板
      if ("2".equals(model.getWmType())) {
        // 物管状态的需要进行附件内容是否变化判断
        logger.info("updateOrderMiddle--物管状态变更");
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
            logger.info("updateOrderMiddle--明细附件发生变化");
            // 通过明细中间表找到明细表，通过明细表，去到资产实体表中进行之前的数据删除，然后删除明细中间表的数据
            updateDetailAndAsset(model.getId(), model.getWmType(), model.getModuleId());
            // 校验资产PK值，同步Redis数据入库
            try {
              insertAllEntityInfo(
                  model.getPkValKey(), model.getAssetKey(), model.getModuleId(), model.getId());
            } catch (Exception e) {
              throw new RuntimeException("物管资产明细记录附件变更，同步Redis资产信息入库失败！" + e.getMessage());
            }
            // 更新附件表
            // 逻辑删除送货明细附件表
            if (model.getAttachmentList().size() > 0) {
              OrderAttachmentDTO dto1 = new OrderAttachmentDTO();
              dto1.setAtttype(3);
              dto1.setParentId(model.getId());
              assetServiceFeign.deleteAttachmentByParentId(dto1, TokenUtil.getToken());
              for (OrderAttachmentDTO model2 : model.getAttachmentList()) {
                model2.setParentId(model.getId());
                model2.setAtttype(3);
              }
              assetServiceFeign.addBeatchAtta(model.getAttachmentList(), TokenUtil.getToken());
            }
          } else {
            logger.info("updateOrderMiddle--明细附件未发生变化，不做任何操作");
          }
        }
      } else {
        logger.info("updateOrderMiddle--量管状态变更");
        // 量管的资产，更新明细表和资产实体表中的数量
        List<Long> midList = new ArrayList<>();
        midList.add(model.getId());
        List<Long> detailAssetIds = orderDelivedetailMapper.getAssetIdsByDId(midList);
        if (detailAssetIds.size() > 0) {
          orderDelivedetailMapper.updateDetails(detailAssetIds, model);
        }
        // 这里需要根据明细表中的数据同步更新资产表中量管资产的数量
        Long assetNum = numT - numT2;
        EntityUpVo entityUpVo = new EntityUpVo();
        entityUpVo.setAssetTemplId(model.getModuleId());
        entityUpVo.setAmount(assetNum);
        entityUpVo.setAssetStatus(0);
        entityUpVo.setOriginalStatus(0);
        try {
          assetServiceFeign.updateEntityInfoStatus(entityUpVo, TokenUtil.getToken());
        } catch (Exception e) {
          throw new RuntimeException(
              "远程调用fegin更新量管资产数量失败！资产明细id为："
                  + model.getId()
                  + ",资产模板id为："
                  + entityUpVo.getAssetTemplId());
        }
      }
    } else {
      logger.info("updateOrderMiddle--进入切换模板环节");
      // 切换模板判断是否有明细id存在，如果有则进行之前的明细id删除，否则不进行操作
      AssetTmplInfoVO assetTmplInfoVO = new AssetTmplInfoVO();
      if (redisTemplate.hasKey(Constants.TMPL_KEY + entity.getModuleId())) {
        assetTmplInfoVO =
            JSON.parseObject(
                redisUtils.get(Constants.TMPL_KEY + entity.getModuleId()), AssetTmplInfoVO.class);
      } else {
        assetTmplInfoVO =
            assetServiceFeign
                .selectPrptValByTmplId(entity.getModuleId(), TokenUtil.getToken())
                .getData();
      }
      // 判断之前模板是否是物管需要进行数据清除
      if ("2".equals(assetTmplInfoVO.getWmType())) {
        logger.info("updateOrderMiddle--切换模板，清除之前物管数据");
        OrderAttachmentDTO dto1 = new OrderAttachmentDTO();
        dto1.setAtttype(3);
        dto1.setParentId(entity.getId());
        assetServiceFeign.deleteAttachmentByParentId(dto1, TokenUtil.getToken());
        updateDetailAndAsset(
            entity.getId(), assetTmplInfoVO.getWmType().toString(), entity.getModuleId());
      } else {
        logger.info("updateOrderMiddle--切换模板，清除之前量管数据");
        List<Long> midList = new ArrayList<>();
        midList.add(model.getId());
        List<Long> detailAssetIds = orderDelivedetailMapper.getAssetIdsByDId(midList);
        if (detailAssetIds.size() > 0) {
          orderDelivedetailMapper.updateDetails(detailAssetIds, model);
        }
      }
      // 判断切换后的模板类型是物管还是量管，物管更新Redis数据入库，量管新增一条数据
      if ("2".equals(model.getWmType())) {
        logger.info("updateOrderMiddle--切换模板，当前物管数据新增");
        // 校验PK值，同步Redis数据入库
        try {
          insertAllEntityInfo(
              model.getPkValKey(), model.getAssetKey(), model.getModuleId(), model.getId());
        } catch (Exception e) {
          throw new RuntimeException("切换模板物管资产redis同步资产信息失败！" + e.getMessage());
        }
        for (OrderAttachmentDTO model2 : model.getAttachmentList()) {
          model2.setParentId(model.getId());
          model2.setAtttype(3);
        }
        assetServiceFeign.addBeatchAtta(model.getAttachmentList(), TokenUtil.getToken());
      } else {
        logger.info("updateOrderMiddle--切换模板，当前量管数据新增");
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
        if (StringUtils.isEmpty(mo.getId())) {
          OrderAttachment entityA = new OrderAttachment();
          BeanCopyUtil.copyPropertiesIgnoreNull(orderAttachment, entityA);
          orderAttachmentMapper.insert(entityA);
        } else {
          orderAttachmentMapper.updateByParentIdAndType(orderAttachment);
        }
      }
    }
    // 最后更新明细中间表
    BeanCopyUtil.copyPropertiesIgnoreNull(model, entity);
    int num = orderDelivemiddleMapper.updateById(entity);
    if (num <= 0) {
      throw new RuntimeException("更新送货明细记录失败！明细信息id为：" + entity.getId());
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
    return ResultVOUtil.returnSuccess();
  }

  /**
   * 根据明细id，逻辑删除明细下的资产明细和资产实体、资产实体属性值（只更新状态为0的资产）
   *
   * @param middleId
   */
  public void updateDetailAndAsset(Long middleId, String wmType, Long moduleId) {
    // 通过明细中间表找到明细表，通过明细表，去到资产实体表中进行之前的数据删除，然后删除明细中间表的数据
    List<Long> midList = new ArrayList<>();
    midList.add(middleId);
    List<Long> detailAssetIds = orderDelivedetailMapper.getAssetIdsByDId(midList);
    if (detailAssetIds.size() > 0) {
      try {
        // 逻辑删除明细表的数据
        orderDelivedetailMapper.updateDetailsDel(
            detailAssetIds, TokenUtil.getUserInfo().getUserId(), middleId);
      } catch (Exception e) {
        throw new RuntimeException(
            "物管资产明细记录附件变更，同步删除明细资产信息失败！要删除的资产id为：" + detailAssetIds.toString());
      }
      // 同步更新状态为0的资产实体信息和资产实体属性值信息,物管数据进行资产实体的信息删除，量管不需要做操作
      if ("2".equals(wmType)) {
        // 2.逻辑删除资产信息实体类
        Long[] strArray = new Long[detailAssetIds.size()];
        detailAssetIds.toArray(strArray);
        // 这里同步对Redis的PK值进行删除
        TmplPrptsFilter tmplPrptsFilter = new TmplPrptsFilter();
        tmplPrptsFilter.setTmplId(moduleId);
        Set<String> valSet =
            assetServiceFeign.searchPKValByTmpId(tmplPrptsFilter, TokenUtil.getToken()).getData();
        if (valSet.size() > 0) {
          for (String str : valSet) {
            redisUtils.delete(str);
          }
        }
        Integer rownum =
            assetServiceFeign.deleteEntitys(strArray, 0, TokenUtil.getToken()).getData();
        // 3.逻辑删除资产属性值信息
        if (rownum > 0) {
          assetServiceFeign.deleteEntityPrpts(strArray, TokenUtil.getToken());
        }
      }
    }
  }

  /** 执行量管资产的新增或修改操作 只用于量管记录的变更 */
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
      if (!StringUtils.isEmpty(dto.getModuleName())) {
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
          entityInfo =
              assetServiceFeign.addAssetEntityInfo(entityInfo, TokenUtil.getToken()).getData();
        } catch (Exception e) {
          throw new RuntimeException("插入量管资产信息到实体表出错！msg:" + e.getMessage());
        }
      }
      // 3.这里进行送货记录的详细表中入库
      OrderDelivedetail orderDelivedetail = new OrderDelivedetail();
      orderDelivedetail.setAssetId(entityInfo.getId());
      orderDelivedetail.setAssetNumber(dto.getAssetNumber());
      orderDelivedetail.setAssetCatId(dto.getAssetCatId());
      if (!StringUtils.isEmpty(dto.getModuleName())) {
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
    // 解析上传的Excel，并检查文件格式和内容是否正确
    Map<String, Object> resultMap = new HashMap<>();
    AssetTmplInfo assetTmplInfo =
        assetServiceFeign.searchAssetTmplInfoOne(moduleId, TokenUtil.getToken()).getData();
    String fileUrl = null;
    // 创建excel工作簿对象
    Workbook workbook = null;
    File excelFile = null;
    FormulaEvaluator formulaEvaluator = null;
    try {
      excelFile = FileUtil.multipartFileToFile(file);
      InputStream is = file.getInputStream();
      workbook = WorkbookFactory.create(is);
      // 判断excel文件打开是否正确
      if (workbook == null) {
        // 解析有误，删除无用文件
        excelFile.delete();
        return ResultVOUtil.returnFail(
            ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
      }
    } catch (Exception e) {
      // 解析有误，删除无用文件
      excelFile.delete();
      throw new RuntimeException("文件上传模板解析出错！只允许上传附件格式为xlsx、xlsm、xls的明细附件，请检查后再上传！");
    }
    // 获取资产模板对应的个性表头信息
    List<Map<String, Object>> titleMap = new ArrayList<>();
    // 获取资产模板所有的属性信息
    Map<Integer, Integer> titleIdM = new HashMap<>();
    Map<Integer, String> titleNameM = new HashMap<>();
    Map<String, Integer> titleNameM2 = new HashMap<>();
    // 获取表头的下标
    List<Integer> collList = new ArrayList<>();
    try {
      titleMap = getTitleMap(moduleId);
      if (titleMap == null || titleMap.size() == 0) {
        return ResultVOUtil.returnFail(
            ResultEnum.FILE_NOTNULL.getCode(), "该模板未进行参数配置，请联系管理员进行配置后上传！模板id为:" + moduleId);
      }
      for (int conum = 0; conum < titleMap.size(); conum++) {
        titleIdM.put(Integer.valueOf(titleMap.get(conum).get("PRPT_ORDER").toString()), conum);
        titleNameM2.put(titleMap.get(conum).get("NAME").toString(), conum);
        titleNameM.put(conum, titleMap.get(conum).get("NAME").toString());
        if (titleMap.get(conum).get("PK_FLAG") != null
            && Integer.valueOf(titleMap.get(conum).get("PK_FLAG").toString()) == 1) {
          collList.add(conum);
        }
      }
    } catch (Exception e) {
      // 解析有误，删除无用文件
      excelFile.delete();
      throw new RuntimeException("获取模板参数配置有误，请联系管理员！");
    }
    Sheet sheet = workbook.getSheetAt(0);
    // 当前sheet页面为空,继续遍历
    if (sheet == null) {
      // 解析有误，删除无用文件
      excelFile.delete();
      return ResultVOUtil.returnFail(
          ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
    }
    // 1.读取Excel的表头数据,比较模板是否一致
    Row row1 = sheet.getRow(0);
    boolean isExcel = true;
    String titleMsg = "";
    // 比较表头大小是否一致
    if (row1 == null || titleNameM.size() != row1.getLastCellNum()) {
      // 解析有误，删除无用文件
      excelFile.delete();
      return ResultVOUtil.returnFail(
          ResultEnum.TEMPLATE_NOTFORMAT.getCode(), ResultEnum.TEMPLATE_NOTFORMAT.getMessage());
    }
    // 比较表头内容是否一致
    for (int columnNum = 0; columnNum < row1.getLastCellNum(); columnNum++) {
      String titleDb = titleNameM.get(columnNum);
      String titleUp = ExcleUtils.getValue(row1.getCell(columnNum), formulaEvaluator);
      if (!titleDb.equals(titleUp)) {
        isExcel = false;
        titleMsg =
            "您维护的第"
                + (columnNum + 1)
                + "列表头字段的标题列名称不正确，应是"
                + titleDb
                + "，实际维护成了"
                + titleUp
                + ",请调整上传的文件。";
        break;
      }
    }
    if (!isExcel) {
      // 解析有误，删除无用文件
      excelFile.delete();
      return ResultVOUtil.returnFail(
          ResultEnum.TEMPLATE_NOTFORMAT.getCode(), ResultEnum.TEMPLATE_NOTFORMAT.getMessage());
    }
    // 2.检查是否存在空列值
    // 对于每个sheet，读取其中的每一行,从第二行开始读取
    resultMap.put("totalRow", sheet.getLastRowNum());
    boolean isRowNull = true;
    boolean isCellNull = true;
    boolean isDataT = true;
    int rowNo = 0;
    String cellMsg = "";
    Long startT = System.currentTimeMillis();
    System.out.println("开始解析：" + startT);
    List<AssetEntityInfoVO> assetEntityInfoVOList = new ArrayList<>();
    List<AssetEntityPrpt> assetEntityPrptList = new ArrayList<>();
    List<OrderDelivedetail> orderDelivedetailList = new ArrayList<>();
    List<String> pkStrList = new ArrayList<>();
    Set<String> collSet = new HashSet<String>();
    for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
      Row row = sheet.getRow(rowNum);
      if (row == null || isRowEmpty(row)) {
        continue;
      }
      // 入库每条资产实体对应的属性值（个性化的，不是共有的,从第二列开始）
      String collStr = moduleId + "_";
      String colVal = "";
      for (int columnNum = 0; columnNum < row.getLastCellNum(); columnNum++) {
        String cellValue = ExcleUtils.getValue(row.getCell(columnNum), formulaEvaluator);
        if (org.springframework.util.StringUtils.isEmpty(cellValue)) {
          isCellNull = false;
          cellMsg = "第" + (rowNum) + "行，第" + (columnNum + 1) + "列";
          break;
        }
        // 3.检查Excel中是否存在重复行，根据数据库中模板属性pk_flag取值
        if (collList.contains(columnNum)) {
          collStr += cellValue;
          colVal += cellValue;
        }
      }

      if (!collSet.add(colVal)) {
        isDataT = false;
        rowNo = rowNum + 1;
        break;
      }

      // 4.添加PK值到数组中
      pkStrList.add(collStr);

      AssetEntityInfoVO colutMap =
          resoveRow(row, formulaEvaluator, assetTmplInfo, titleIdM, titleNameM2, titleMap);
      assetEntityInfoVOList.add(colutMap);
      orderDelivedetailList.add(colutMap.getOrderDelivedetail());
      assetEntityPrptList.addAll(colutMap.getAssetEntityPrptList());
    }
    if (!isRowNull) {
      // 解析有误，删除无用文件
      excelFile.delete();
      return ResultVOUtil.returnFail(
          ResultEnum.TEMPLATE_CELLNULL.getCode(), "附件第" + rowNo + "行数据内容为空");
    }
    if (!isCellNull) {
      // 解析有误，删除无用文件
      excelFile.delete();
      return ResultVOUtil.returnFail(
          ResultEnum.TEMPLATE_CELLNULL.getCode(), "附件" + cellMsg + "数据内容为空");
    }
    if (!isDataT) {
      // 解析有误，删除无用文件
      excelFile.delete();
      return ResultVOUtil.returnFail(
          ResultEnum.TEMPLATE_ROWTWO.getCode(), "附件第" + rowNo + "行数据内容有重复");
    }

    // 这里进行批量插入的方法
    resultMap.put("assetKey", "");
    resultMap.put("pkValKey", "");
    if (assetEntityInfoVOList.size() > 0) {
      // 获取待暂存的数据
      // 这里统一设置Redis的过期时间为40分钟，40分钟不提交，则该条数据作废
      String key = moduleId + "_" + NumberUtils.createCode("SC");
      try {
        redisUtils.setWithTime(
            key,
            JSON.toJSON(assetEntityInfoVOList).toString(),
            Constants.TMPLINFO_TIMEOUT,
            TimeUnit.MINUTES);
      } catch (Exception e) {
        throw new RuntimeException("系统缓存服务异常，请检查！");
      }
      // 将Excel中的PK值暂存到Redis中
      String key2 = moduleId + "_" + NumberUtils.createCode("PK");
      try {
        redisUtils.setWithTime(
            key2, JSON.toJSON(pkStrList).toString(), Constants.TMPLINFO_TIMEOUT, TimeUnit.MINUTES);
      } catch (Exception e) {
        throw new RuntimeException("系统缓存服务异常，请检查！");
      }
      resultMap.put("pkValKey", key2);
      resultMap.put("assetKey", key);
    } else {
      // 解析有误，删除无用文件
      excelFile.delete();
      return ResultVOUtil.returnFail(
          ResultEnum.TEMPLATE_CELLNULL.getCode(), "附件第" + rowNo + "行数据内容为空");
    }
    // 调用附件上传接口
    fileUrl = assetServiceFeign.uploadSingleFile(file, TokenUtil.getToken());
    resultMap.put("fileUrl", fileUrl);
    if (org.springframework.util.StringUtils.isEmpty(resultMap.get("fileUrl"))) {
      // 上传失败，删除无用文件
      excelFile.delete();
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
  }

  public static boolean isRowEmpty(Row row) {
    for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
      Cell cell = row.getCell(i);
      if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
        return false;
      }
    }
    return true;
  }

  /**
   * 解析Excel中的每一行数据 封装资产实体信息 资产PK值信息 暂存入Redis中
   *
   * @param row
   * @param formulaEvaluator
   * @param assetTmplInfo
   * @param titleIdM
   * @param titleNameM2
   * @param titleMap2
   * @return
   */
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
    entityInfo.setAssetStatus(0);
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

  public OrderDelivemiddle selectOne(Long id) {
    OrderDelivemiddle entity = new OrderDelivemiddle();
    entity.setId(id);
    QueryWrapper<OrderDelivemiddle> queryWrapper = new QueryWrapper<>();
    queryWrapper.setEntity(entity);
    return orderDelivemiddleMapper.selectOne(queryWrapper);
  }

  /**
   * 批量入库物管资产信息 同步Redis中的数据入库
   *
   * @param pkvalKey
   * @return
   */
  public void insertAllEntityInfo(String pkvalKey, String assetKey, Long moudleId, Long middleId) {
    // 1.校验Excel中的资产实体是否与库中已经存在的资产实体重复
    String pkvalJsonArray = "";
    try {
      pkvalJsonArray = redisUtils.get(pkvalKey);
    } catch (Exception e) {
      throw new RuntimeException("系统缓存服务异常，获取系统缓存值数据失败！取值PKkey为：" + pkvalKey);
    }
    List<String> pkStrList = new ArrayList<>();
    if (!StringUtils.isEmpty(pkvalJsonArray)) {
      pkStrList = JSONObject.parseArray(pkvalJsonArray, String.class);
    }
    for (String pkstr : pkStrList) {
      if (redisTemplate.hasKey(pkstr)) {
        throw new RuntimeException("明细附件内容与系统库中数据重复，请检查再上传！重复数据为：" + pkstr);
      } else {
        redisTemplate.opsForValue().set(pkstr, moudleId.toString());
      }
    }
    // 2.进行资产数据Redis同步入库
    String businessJsonArray = "";
    try {
      businessJsonArray = redisUtils.get(assetKey);
    } catch (Exception e) {
      throw new RuntimeException("系统缓存服务异常，获取系统缓存值数据失败！取值资产key为：" + assetKey);
    }
    List<AssetEntityInfoVO> businessIdList = new ArrayList<>();
    if (!StringUtils.isEmpty(businessJsonArray)) {
      businessIdList = JSONObject.parseArray(businessJsonArray, AssetEntityInfoVO.class);
    } else {
      throw new RuntimeException("系统缓存获取资产数据失败！取值资产key为：" + assetKey);
    }
    // 封装入库
    List<AssetEntityPrpt> assetEntityPrptList = new ArrayList<>();
    List<OrderDelivedetail> orderDelivedetailList = new ArrayList<>();
    businessIdList.stream()
        .forEach(
            a -> {
              assetEntityPrptList.addAll(a.getAssetEntityPrptList());
              OrderDelivedetail model = a.getOrderDelivedetail();
              model.setMiddleId(middleId);
              orderDelivedetailList.add(model);
            });

    if (businessIdList.size() > 0) {
      orderDelivemiddleMapper.insertEntitysPlan(businessIdList);
      orderDelivemiddleMapper.insertPrptsPlan(assetEntityPrptList);
      orderDelivemiddleMapper.insertDetailsPlan(orderDelivedetailList);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseEnvelope deleteDetailFile(String dto) {
    // 此方法2020/06/10已去除
    //    if (StringUtils.isEmpty(dto)) {
    //      throw new RuntimeException("请求参数有误，明细资产ids字段不能为空！");
    //    }
    // 1.删除送货记录明细信息
    //    List<Long> assetIds = new ArrayList<>();
    //    String[] arr = dto.split(",");
    //    for (String mn : arr) {
    //      assetIds.add(Long.valueOf(mn));
    //    }
    //    if (assetIds.size() > 0) {
    //      Long[] strArray = new Long[assetIds.size()];
    //      assetIds.toArray(strArray);
    //      orderDelivedetailMapper.deleteDeliveDetails(assetIds);
    //      // 2.删除资产信息实体类
    //      Integer num = assetServiceFeign.deleteEntitys(strArray, -2,
    // TokenUtil.getToken()).getData();
    //      // 3.删除资产属性值信息
    //      if (num > 0) {
    //        assetServiceFeign.deleteEntityPrpts(strArray, TokenUtil.getToken());
    //      }
    //    }
    return ResultVOUtil.returnSuccess();
  }

  @Override
  public List<OrderDelivemiddleVO> getDeliverDetailList(OrderDelivemiddleQuery dto, Long orgId) {
    Map<Long, String> supplierMap = new HashMap<>();
    logger.info("getDeliverDetailList--fegin远程获取供应商列表信息开始");
    List<HashMap<String, Object>> resultMap =
        (List<HashMap<String, Object>>)
            assetServiceFeign.getSupplyList(null, dto.getToken()).getData();
    logger.info("getDeliverDetailList--fegin远程获取供应商列表信息共：" + resultMap.size() + "条，结束");
    for (HashMap<String, Object> model : resultMap) {
      supplierMap.put(Long.valueOf(model.get("id").toString()), model.get("custName").toString());
    }
    Page page = new Page();
    page.setSize(dto.getPageSize());
    page.setCurrent(dto.getCurrentPage());
    IPage<OrderDelivemiddleVO> recordsList =
        orderDelivemiddleMapper.selectMiddleList(page, dto, orgId);
    List<OrderDelivemiddleVO> resultList = recordsList.getRecords();
    // 6.查询所属附件资产清单id集合
    List<Long> arr = new ArrayList<>();
    Map<String, String> assetIdMap = new HashMap<>();
    List<Map<String, Object>> resMap = orderDelivedetailMapper.getEntityIdsByMid(arr);
    for (Map<String, Object> mo : resMap) {
      assetIdMap.put(mo.get("MIDDLEID").toString(), mo.get("IDS").toString());
    }
    resultList.stream()
        .forEach(
            a -> {
              // 1.查询送货信息
              OrderDeliverecords orderDeliverecord =
                  orderDeliverecordsMapper.getDeliverecordInfo(a.getDeliveryId());
              // 4.查询模板名称
              AssetTmplInfoVO assetTmplInfo = new AssetTmplInfoVO();
              if (redisTemplate.hasKey(Constants.TMPL_KEY + a.getModuleId())) {
                assetTmplInfo =
                    JSON.parseObject(
                        redisUtils.get(Constants.TMPL_KEY + a.getModuleId()),
                        AssetTmplInfoVO.class);
              } else {
                assetTmplInfo =
                    assetServiceFeign
                        .selectPrptValByTmplId(a.getModuleId(), dto.getToken())
                        .getData();
              }
              OrderAttachmentDTO attachmentDTO = new OrderAttachmentDTO();
              attachmentDTO.setAtttype(3);
              attachmentDTO.setParentId(a.getId());
              a.setHaveFile(
                  assetServiceFeign.selectAttachNum(attachmentDTO, dto.getToken()).getData() > 0
                      ? "是"
                      : "否");
              a.setDeliveryCode(orderDeliverecord.getDeliveryCode());
              a.setDeliveryName(orderDeliverecord.getDeliveryName());
              a.setSignAddress(orderDeliverecord.getSignAddress());
              if (assetTmplInfo != null) {
                a.setUnitVal(assetTmplInfo.getUnit());
                a.setPriceVal(assetTmplInfo.getPrice());
                a.setModuleName(assetTmplInfo.getName());
              }
              if (supplierMap != null) {
                a.setSupplierName(supplierMap.get(orderDeliverecord.getSupplierId()));
              }
              a.setSignStatusName(
                  a.getSignStatus() == 2 ? "已签收" : a.getSignStatus() == 1 ? "部分签收" : "未签收");
            });
    return resultList;
  }

  /**
   * 通过送货单id来获取送货明细下的所有明细id集合
   *
   * @param id
   * @return
   */
  @Override
  public List<Long> getMIdsByDeliveryId(Long id) {
    return orderDelivemiddleMapper.getMIdsByDeliveryId(id);
  }

  /**
   * 此方法用来提供给签收模块，用户对明细记录进行签收入库， 回显明细中间表的签收号和签收状态
   *
   * @param dto
   * @return
   */
  @Override
  public ResponseEnvelope updateMiddleById(OrderDelivemiddleDTO dto) {
    if (dto.getId() != null) {
      OrderDelivemiddle entity = new OrderDelivemiddle();
      BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
      orderDelivemiddleMapper.updateById(entity);
    } else {
      throw new RuntimeException("更新资产明细记录，id不能为空！");
    }
    return ResultVOUtil.returnSuccess();
  }

  @Override
  public ResponseEnvelope searchAssetListByMid(Long id) {
    List<Long> resList = new ArrayList<>();
    if (id != null) {
      List<Long> middleIds = new ArrayList<>();
      middleIds.add(id);
      resList = orderDelivedetailMapper.getAssetIdsByDId(middleIds);
    }
    return ResultVOUtil.returnSuccess(resList);
  }

  @Override
  public ResponseEnvelope deleteRedisKey(Set<String> keyList) {
    if (keyList.size() > 0) {
      for (String mo : keyList) {
        logger.info("删除Redis_key:" + mo);
        redisUtils.delete(mo);
      }
    }
    return ResultVOUtil.returnSuccess();
  }
}
