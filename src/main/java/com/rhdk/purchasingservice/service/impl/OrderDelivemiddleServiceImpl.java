package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.*;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.AssetServiceFeign;
import com.rhdk.purchasingservice.mapper.*;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.entity.*;
import com.rhdk.purchasingservice.pojo.query.AssetQuery;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;
import com.rhdk.purchasingservice.pojo.vo.AssetEntityInfoVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDelivemiddleVO;
import com.rhdk.purchasingservice.service.IOrderDelivemiddleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

  private static org.slf4j.Logger logger =
      LoggerFactory.getLogger(OrderDelivemiddleServiceImpl.class);

  @Override
  public ResponseEnvelope searchOrderDelivemiddleListPage(OrderDelivemiddleQuery dto) {
    Page<OrderDelivemiddle> page = new Page<OrderDelivemiddle>();
    page.setSize(dto.getPageSize());
    page.setCurrent(dto.getCurrentPage());
    QueryWrapper<OrderDelivemiddle> queryWrapper = new QueryWrapper<OrderDelivemiddle>();
    OrderDelivemiddle entity = new OrderDelivemiddle();
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    queryWrapper.setEntity(entity);
    if (!StringUtils.isEmpty(dto.getSupplierName())) {
      List<Long> supplierIds =
          (List<Long>)
              assetServiceFeign.getIdsBySupplierName(dto.getSupplierName(), TokenUtil.getToken());
      List<Long> deliverIds = orderDeliverecordsMapper.getIdsBySupplierId(supplierIds);
      queryWrapper.in("DELIVERY_ID", deliverIds);
    }
    page = orderDelivemiddleMapper.selectPage(page, queryWrapper);
    List<OrderDelivemiddle> resultList = page.getRecords();
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
                  AssetTmplInfo assetTmplInfo =
                      assetServiceFeign
                          .searchAssetTmplInfoOne(a.getModuleId(), TokenUtil.getToken())
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
                  List<String> assetValue =
                      (List<String>)
                          assetServiceFeign
                              .searchValByPrptIds(assetQuery, TokenUtil.getToken())
                              .getData();
                  String assetValueStr = StringUtils.join(assetValue.toArray(), ",");
                  OrderDelivemiddleVO model =
                      OrderDelivemiddleVO.builder()
                          .attachmentList(orderAttachmentMapper.selectListByParentId(a.getId(), 3))
                          .deliveryCode(orderDeliverecord.getDeliveryCode())
                          .deliveryName(orderDeliverecord.getDeliveryName())
                          .supplierId(orderDeliverecord.getSupplierId())
                          .signAddress(orderDeliverecord.getSignAddress())
                          .supplierName(customer.getCusName())
                          .prptValues(assetValueStr)
                          .moduleName(assetTmplInfo.getName())
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
      AssetTmplInfo assetTmplInfo =
          assetServiceFeign
              .searchAssetTmplInfoOne(orderDelivemiddle.getModuleId(), TokenUtil.getToken())
              .getData();
      // 5.查询供应商名称
      Customer customer =
          assetServiceFeign
              .searchCustomerOne(orderDeliverecord.getSupplierId(), TokenUtil.getToken())
              .getData();
      AssetQuery assetQuery = new AssetQuery();
      assetQuery.setAssetTempId(orderDelivemiddle.getModuleId());
      assetQuery.setPrptIds(orderDelivemiddle.getPrptIds());
      List<String> assetValue =
          (List<String>)
              assetServiceFeign.searchValByPrptIds(assetQuery, TokenUtil.getToken()).getData();
      String assetValueStr = StringUtils.join(assetValue.toArray(), ",");
      model =
          OrderDelivemiddleVO.builder()
              .attachmentList(orderAttachmentMapper.selectListByParentId(id, 3))
              .deliveryCode(orderDeliverecord.getDeliveryCode())
              .deliveryName(orderDeliverecord.getDeliveryName())
              .supplierId(orderDeliverecord.getSupplierId())
              .signAddress(orderDeliverecord.getSignAddress())
              .supplierName(customer.getCusName())
              .moduleName(assetTmplInfo.getName())
              .prptValues(assetValueStr)
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
      BeanCopyUtil.copyPropertiesIgnoreNull(orderDelivemiddle, model);
    }
    return ResultVOUtil.returnSuccess(model);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseEnvelope addOrderDelivemiddle(OrderDelivemiddleDTO dto) throws Exception {
    OrderDelivemiddle entity = new OrderDelivemiddle();
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    // 这里自动生成送货明细业务编码，规则为：SHD+时间戳
    String code = NumberUtils.createCode("SHD");
    entity.setDeliverydetailCode(code);
    orderDelivemiddleMapper.insert(entity);
    // 进行附件清单的解析及数据源的入库操作
    if ("2".equals(dto.getWmType())) {
      dto.setId(entity.getId());
      // 这里进行资产实体表、资产实体属性值表、送货明细记录表，三表的状态变更
      updateAssetStatus(dto);
      // 5.最后进行明细附件的入库
      OrderAttachment orderAttachment = new OrderAttachment();
      orderAttachment.setParentId(entity.getId());
      orderAttachment.setAtttype(3);
      orderAttachment.setOrgfilename(dto.getFileName());
      orderAttachment.setFileurl(dto.getFileUrl());
      orderAttachmentMapper.insert(orderAttachment);
    } else {
      // 执行量管的数据记录
      AssetEntityInfo entityInfo = new AssetEntityInfo();
      entityInfo.setAmount(dto.getAssetNumber());
      entityInfo.setAssetCatId(dto.getAssetCatId());
      entityInfo.setAssetTemplId(dto.getModuleId());
      entityInfo.setAssetTemplVer(dto.getModuleVersion());
      entityInfo.setItemNo(dto.getItemNO());
      entityInfo.setAssetStatus(0L);
      // 1.入库资产实体表信息
      entityInfo = assetServiceFeign.addAssetEntityInfo(entityInfo, dto.getToken()).getData();

      // 3.这里进行送货记录的详细表中入库
      OrderDelivedetail orderDelivedetail = new OrderDelivedetail();
      orderDelivedetail.setAssetId(entityInfo.getId());
      orderDelivedetail.setAssetNumber(dto.getAssetNumber());
      orderDelivedetail.setAssetCatId(dto.getAssetCatId());
      orderDelivedetail.setMiddleId(entity.getId());
      orderDelivedetail.setItemNo(dto.getItemNO());
      orderDelivedetailMapper.insert(orderDelivedetail);
    }

    return ResultVOUtil.returnSuccess();
  }

  @Override
  public List<Map<String, Object>> getTitleList(Long moduleId) {
    return orderDelivemiddleMapper.getTitleList(moduleId);
  }

  @Override
  public List<Map<String, Object>> getTitleMap(Long moduleId) {
    return orderDelivemiddleMapper.getTitleMap(moduleId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Integer deleteByPassNo(Long id) {
    // 根据送货单id查询出对应的所有明细清单
    List<Long> middleIds = orderDelivemiddleMapper.getMIdsByDeliveryId(id);
    if (middleIds.size() > 0) {
      // 物理删除送货明细表
      List<Long> assetIds = orderDelivedetailMapper.getAssetIdsByDId(middleIds);
      if (assetIds.size() > 0) {
        Long[] strArray = new Long[assetIds.size()];
        assetIds.toArray(strArray);
        orderDelivedetailMapper.updateDetailsDel(assetIds);
        // 物理删除资产实体表
        assetServiceFeign.updateEntitys(strArray, TokenUtil.getToken());
        // 物理删除资产实体属性值表
        assetServiceFeign.updateEntityprpts(strArray, TokenUtil.getToken());
      }
      // 物理删除送货中间表
      for (Long no : middleIds) {
        orderDelivemiddleMapper.deleteById(no);
        // 物理删除送货明细附件表
        orderAttachmentMapper.deleteAttachmentByParentId(no, 3L);
      }
    }

    return ResultVOUtil.returnSuccess().getCode();
  }

  @Override
  @Transactional
  public ResponseEnvelope deleteOrderDetailrecords(Long id) {
    // 物理删除送货中间表
    orderDelivemiddleMapper.deleteById(id);
    // 物理删除送货明细附件表
    orderAttachmentMapper.deleteAttachmentByParentId(id, 3L);
    // 物理删除送货明细表
    List<Long> middleIds = new ArrayList<>();
    middleIds.add(id);
    List<Long> assetIds = orderDelivedetailMapper.getAssetIdsByDId(middleIds);
    Long[] strArray = new Long[assetIds.size()];
    assetIds.toArray(strArray);
    orderDelivedetailMapper.updateDetailsDel(assetIds);
    // 物理删除资产实体表
    assetServiceFeign.updateEntitys(strArray, TokenUtil.getToken());
    // 物理删除资产实体属性值表
    assetServiceFeign.updateEntityprpts(strArray, TokenUtil.getToken());
    return ResultVOUtil.returnSuccess();
  }

  @Override
  public ResponseEnvelope updateOrderMiddle(OrderDelivemiddleDTO dto) {
    OrderDelivemiddle entity = this.selectOne(dto.getId());
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    // 更新送货记录内容
    orderDelivemiddleMapper.updateById(entity);
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

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseEnvelope uploadFileCheck(MultipartFile file, Long moduleId) throws Exception {
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
    Set<String> collSet = new HashSet<String>();
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
    for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
      Row row = sheet.getRow(rowNum);
      if (row == null) {
        rowNo = rowNum + 1;
        isRowNull = false;
        break;
      }
      // 入库每条资产实体对应的属性值（个性化的，不是共有的,从第二列开始）
      String collStr = "";
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
      AssetEntityInfoVO colutMap =
          resoveRow(row, formulaEvaluator, assetTmplInfo, titleIdM, titleNameM2, titleMap);
      assetEntityInfoVOList.add(colutMap);
      orderDelivedetailList.add(colutMap.getOrderDelivedetail());
      assetEntityPrptList.addAll(colutMap.getAssetEntityPrptList());
    }
    // 这里进行批量插入的方法
    Integer rowNum = orderDelivemiddleMapper.insertEntitysPlan(assetEntityInfoVOList);
    Integer rowNum2 = orderDelivemiddleMapper.insertPrptsPlan(assetEntityPrptList);
    Integer rowNum3 = orderDelivemiddleMapper.insertDetailsPlan(orderDelivedetailList);
    Long endT = System.currentTimeMillis();
    System.out.println(
        "结束解析："
            + endT
            + ",共用时："
            + (endT - startT) / 1000
            + ",资产条数为："
            + rowNum
            + ",属性值条数为："
            + rowNum2
            + ",明细条数为："
            + rowNum3);
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
    // 调用附件上传接口
    fileUrl = assetServiceFeign.uploadSingleFile(file, TokenUtil.getToken());
    resultMap.put("fileUrl", fileUrl);
    if (org.springframework.util.StringUtils.isEmpty(resultMap.get("fileUrl"))) {
      return ResultVOUtil.returnFail(
          ResultEnum.CREATE_FILEERROR.getCode(), ResultEnum.CREATE_FILEERROR.getMessage());
    }
    return ResultVOUtil.returnSuccess(resultMap);
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
  public void updateAssetStatus(OrderDelivemiddleDTO dto) {
    // 将对应的明细附件清单上传的资产进行状态改变，暂存-已提交
    // 从送货明细表中获取暂存的资产id集合
    dto.setSaveStatus(0);
    List<Long> assetIds = orderDelivedetailMapper.getAssetIdsBYStatus(dto);
    // 3.资产实体属性值表，暂存状态变更为已提交状态
    if (assetIds.size() > 0) {
      Long[] strArray = new Long[assetIds.size()];
      assetIds.toArray(strArray);
      assetServiceFeign.updateAssetprptsStatus(strArray, dto.getToken());
      // 2.资产实体表，暂存状态变更为待签收状态
      assetServiceFeign.updateEntitysStatus(strArray, dto.getToken()).getData();
      // 4.资产明细表，暂存状态变更为已提交状态
      AssetQuery assetQuery = new AssetQuery();
      assetQuery.setAssetIds(assetIds);
      assetQuery.setMiddleId(dto.getId());
      orderDelivedetailMapper.updateAssetStatus(assetQuery);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseEnvelope deleteDetailFile(OrderDelivemiddleDTO dto) {
    // 1.删除附件信息
    // 物理删除送货明细附件表
    orderAttachmentMapper.deleteAttachmentByParentId(dto.getId(), 3L);
    // 2.删除送货记录明细信息
    List<Long> middleIds = new ArrayList<>();
    middleIds.add(dto.getId());
    List<Long> assetIds = orderDelivedetailMapper.getAssetIdsByDId(middleIds);
    Long[] strArray = new Long[assetIds.size()];
    assetIds.toArray(strArray);
    orderDelivedetailMapper.deleteDeliveDetails(assetIds);
    // 3.删除资产信息实体类
    assetServiceFeign.deleteEntitys(strArray, TokenUtil.getToken());
    // 4.删除资产属性值信息
    assetServiceFeign.deleteEntityPrpts(strArray, TokenUtil.getToken());
    return ResultVOUtil.returnSuccess();
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
    entityInfo.setAssetStatus(4);
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
      assetEntityPrpt.setSaveStatus(0L);
      assetEntityPrpt.setCreateBy(entityInfo.getCreateBy());
      // 这里判断对应的属性值是共性的还是个性的，共性属性值从数据库取，个性的属性值从cell中进行读取
      //      if (model.get("PRPT_VALUE") == null
      //          || StringUtils.isEmpty(model.get("PRPT_VALUE").toString())) {
      // int a = titleIdM.get(Integer.valueOf(model.get("PRPT_ORDER").toString()));
      assetEntityPrpt.setVal(val);
      //      } else {
      //        assetEntityPrpt.setVal(model.get("PRPT_VALUE").toString());
      //      }
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
    orderDelivedetail.setSaveStatus(0L);
    orderDelivedetail.setAssetCatId(assetTmplInfo.getAssetCatId());
    orderDelivedetail.setCreateBy(entityInfo.getCreateBy());
    orderDelivedetail.setItemNo(assetTmplInfo.getItemNo());
    entityInfo.setOrderDelivedetail(orderDelivedetail);
    return entityInfo;
  }
}
