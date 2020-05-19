package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.BeanCopyUtil;
import com.rhdk.purchasingservice.common.utils.ExcleUtils;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.mapper.*;
import com.rhdk.purchasingservice.pojo.entity.*;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderDelivemiddleVO;
import com.rhdk.purchasingservice.service.IOrderDelivemiddleService;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 送货记录明细中间表 服务实现类
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-13
 */
@Slf4j
@Transactional
@Service
public class OrderDelivemiddleServiceImpl extends ServiceImpl<OrderDelivemiddleMapper, OrderDelivemiddle> implements IOrderDelivemiddleService {


    @Autowired
    private OrderDelivemiddleMapper orderDelivemiddleMapper;

    @Autowired
    private AssetEntityInfoMapper assetEntityInfoMapper;

    @Autowired
    private AssetEntityPrptMapper assetEntityPrptMapper;

    @Autowired
    private OrderAttachmentMapper orderAttachmentMapper;

    @Autowired
    private OrderDelivedetailMapper orderDelivedetailMapper;

    @Autowired
    private OrderDeliverecordsMapper orderDeliverecordsMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Override
    public ResponseEnvelope searchOrderDelivemiddleListPage(OrderDelivemiddleQuery dto) {
        Page<OrderDelivemiddle> page = new Page<OrderDelivemiddle>();
        page.setSize(dto.getPageSize());
        page.setCurrent(dto.getCurrentPage());
        QueryWrapper<OrderDelivemiddle> queryWrapper = new QueryWrapper<OrderDelivemiddle>();
        OrderDelivemiddle entity = new OrderDelivemiddle();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        queryWrapper.setEntity(entity);
        if(!StringUtils.isEmpty(dto.getSupplierName())){
            List<Long> supplierIds=commonMapper.getIdsBySupplierName(dto.getSupplierName());
            List<Long> deliverIds=orderDeliverecordsMapper.getIdsBySupplierId(supplierIds);
            queryWrapper.in("DELIVERY_ID",deliverIds);
        }
        page = orderDelivemiddleMapper.selectPage(page, queryWrapper);
        List<OrderDelivemiddle> resultList = page.getRecords();
        List<OrderDelivemiddleVO> orderDelivemiddleVOList = resultList.stream().map(a -> {
            Map<String, Object> billInfoMap = orderDelivemiddleMapper.getContractInfoByMId(a.getId());
            OrderDelivemiddleVO model = OrderDelivemiddleVO.builder()
                    .attachmentList(orderAttachmentMapper.selectListByParentId(a.getId(), 3))
                    .deliveryCode(billInfoMap.get("deliveryCode").toString()).deliveryName(billInfoMap.get("deliveryName").toString())
                    .supplierId(Long.valueOf(billInfoMap.get("supplierId").toString())).signAddress(billInfoMap.get("signAddress").toString())
                    .supplierName(billInfoMap.get("custName").toString())
                    .contractCode(billInfoMap.get("contractCode").toString()).contractName(billInfoMap.get("contractName").toString())
                    .contractType(Integer.valueOf(billInfoMap.get("contractType").toString()))
                    .build();
            BeanCopyUtil.copyPropertiesIgnoreNull(a, model);
            return model;
        }).collect(Collectors.toList());
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
        OrderDelivemiddle orderDelivemiddle = this.selectOne(id);
        Map<String, Object> billInfoMap = orderDelivemiddleMapper.getContractInfoByMId(id);
        OrderDelivemiddleVO model = OrderDelivemiddleVO.builder()
                .attachmentList(orderAttachmentMapper.selectListByParentId(id, 3))
                .deliveryCode(billInfoMap.get("deliveryCode").toString()).deliveryName(billInfoMap.get("deliveryName").toString())
                .supplierId(Long.valueOf(billInfoMap.get("supplierId").toString())).signAddress(billInfoMap.get("signAddress").toString())
                .contractCode(billInfoMap.get("contractCode").toString()).contractName(billInfoMap.get("contractName").toString())
                .contractType(Integer.valueOf(billInfoMap.get("contractType").toString()))
                .build();
        BeanCopyUtil.copyPropertiesIgnoreNull(orderDelivemiddle, model);
        return ResultVOUtil.returnSuccess(model);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEnvelope addOrderDelivemiddle(OrderDelivemiddleDTO dto) throws Exception {
        OrderDelivemiddle entity = new OrderDelivemiddle();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        orderDelivemiddleMapper.insert(entity);
        //获取资产模板所有的属性信息
        List<Map<String, Object>> titleMap = orderDelivemiddleMapper.getTitleList(dto.getModuleId());

        //获取表格里的个性属性
        List<Map<String, Object>> titleMap2 = orderDelivemiddleMapper.getTitleMap(dto.getModuleId());
        //获取表头的下标
        Map<Integer, Integer> titleIdM = new HashMap<>();
        Map<String, Integer> titleNameM = new HashMap<>();
        for (int num = 0; num < titleMap2.size(); num++) {
            titleIdM.put(Integer.valueOf(titleMap2.get(num).get("PRPT_ORDER").toString()), num);
            titleNameM.put(titleMap2.get(num).get("NAME").toString(), num);
        }
        //进行附件清单的解析及数据源的入库操作
        if ("2".equals(dto.getWmType())) {
            /**
             *    这里需要进行区分附件的资产信息是物管还是量管，若是物管需要上传明细附件，
             *    若是量管则不需要进行明细附件的上传操作
             *    1.根据上传文件的路径读取Excel里面的内容
             *    2.将Excel中的资产信息对应存储到资产实体表中
             *    3.将资产实体属性值对应存储到对应记录表中
             *    4.将Excel中的数据同步读取到送货记录明细表中
             *    5.将明细附件存储到对应的附件表中
             *    6.保存明细记录附件信息
             **/

            if (StringUtils.isEmpty(dto.getFileUrl())) {
                return ResultVOUtil.returnFail(ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
            }
            // 创建excel工作簿对象
            Workbook workbook = null;
            FormulaEvaluator formulaEvaluator = null;
            File excelFile = new File(dto.getFileUrl());
            InputStream is = new FileInputStream(excelFile);
            // 判断文件是xlsx还是xls
            if (excelFile.getName().endsWith("xlsx")) {
                workbook = new XSSFWorkbook(is);
                formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
            } else {
                workbook = new HSSFWorkbook(is);
                formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
            }
            //判断excel文件打开是否正确
            if (workbook == null) {
                System.err.println("未读取到内容,请检查路径！");
            }
            //遍历工作簿中的sheet
            for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
                Sheet sheet = workbook.getSheetAt(numSheet);
                //当前sheet页面为空,继续遍历
                if (sheet == null) {
                    continue;
                }
                // 对于每个sheet，读取其中的每一行,从第二行开始读取
                for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    AssetEntityInfo entityInfo = new AssetEntityInfo();
                    entityInfo.setAmount(1L);
                    entityInfo.setAssetCatId(dto.getAssetCatId());
                    entityInfo.setAssetTemplId(dto.getModuleId());
                    entityInfo.setAssetTemplVer(dto.getModuleVersion());
                    entityInfo.setItemNo(dto.getItemNO());
                    entityInfo.setAssetStatus(0L);
                    if (titleNameM.get("名称") != null) {
                        Cell cell = row.getCell(titleNameM.get("名称"));
                        entityInfo.setName(ExcleUtils.getValue(cell, formulaEvaluator));
                    }
                    //1.入库资产实体表信息
                    assetEntityInfoMapper.insert(entityInfo);
                    //入库每条资产实体对应的属性值（每个属性都需要入到资产属性值表中）
                    for (Map<String, Object> model : titleMap) {
                        //存储个性+共性的属性
                        AssetEntityPrpt assetEntityPrpt = new AssetEntityPrpt();
                        assetEntityPrpt.setAssetId(entityInfo.getId());
                        assetEntityPrpt.setPrptId(Long.valueOf(model.get("PRPT_ID").toString()));
                        //这里判断对应的属性值是共性的还是个性的，共性属性值从数据库取，个性的属性值从cell中进行读取
                        if (model.get("PRPT_VALUE") == null || StringUtils.isEmpty(model.get("PRPT_VALUE").toString())) {
                            int a = titleIdM.get(Integer.valueOf(model.get("PRPT_ORDER").toString()));
                            assetEntityPrpt.setVal(ExcleUtils.getValue(row.getCell(a), formulaEvaluator));
                        } else {
                            assetEntityPrpt.setVal(model.get("PRPT_VALUE").toString());
                        }
                        assetEntityPrpt.setCode(model.get("CODE").toString());
                        //2.入库到资产实体属性值表中
                        assetEntityPrptMapper.insert(assetEntityPrpt);
                    }
                    //3.这里进行送货记录的详细表中入库
                    OrderDelivedetail orderDelivedetail = new OrderDelivedetail();
                    if (entityInfo.getName() != null) {
                        orderDelivedetail.setAssetName(entityInfo.getName());
                    }
                    orderDelivedetail.setAssetId(entityInfo.getId());
                    orderDelivedetail.setAssetNumber(1L);
                    orderDelivedetail.setAssetCatId(dto.getAssetCatId());
                    orderDelivedetail.setMiddleId(entity.getId());
                    orderDelivedetail.setItemNo(dto.getItemNO());
                    orderDelivedetailMapper.insert(orderDelivedetail);
                }
            }
            //5.最后进行明细附件的入库
            OrderAttachment orderAttachment = new OrderAttachment();
            orderAttachment.setParentId(entity.getId());
            orderAttachment.setAtttype(3);
            orderAttachment.setOrgfilename(dto.getFileName());
            orderAttachment.setFileurl(dto.getFileUrl());
            orderAttachmentMapper.insert(orderAttachment);
        } else {
            //执行量管的数据记录
            AssetEntityInfo entityInfo = new AssetEntityInfo();
            entityInfo.setAmount(dto.getAssetNumber());
            entityInfo.setAssetCatId(dto.getAssetCatId());
            entityInfo.setAssetTemplId(dto.getModuleId());
            entityInfo.setAssetTemplVer(dto.getModuleVersion());
            entityInfo.setItemNo(dto.getItemNO());
            entityInfo.setAssetStatus(0L);
            //1.入库资产实体表信息
            assetEntityInfoMapper.insert(entityInfo);

            //3.这里进行送货记录的详细表中入库
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
        //根据送货单id查询出对应的所有明细清单
        List<Long> middleIds = orderDelivemiddleMapper.getMIdsByDeliveryId(id);
        //物理删除送货明细表
        List<Long> assetIds = orderDelivedetailMapper.getAssetIdsByDId(middleIds);
        orderDelivedetailMapper.updateDetailsDel(assetIds);
        //物理删除资产实体表
        assetEntityInfoMapper.updateEntitys(assetIds);
        //物理删除资产实体属性值表
        assetEntityPrptMapper.updateEntityprpts(assetIds);
        //物理删除送货中间表
        for (Long no : middleIds) {
            orderDelivemiddleMapper.deleteById(no);
            //物理删除送货明细附件表
            orderAttachmentMapper.deleteAttachmentByParentId(no, 3L);
        }
        return ResultVOUtil.returnSuccess().getCode();
    }

    @Override
    public ResponseEnvelope deleteOrderDetailrecords(Long id) {
        //物理删除送货中间表
        orderDelivemiddleMapper.deleteById(id);
        //物理删除送货明细附件表
        orderAttachmentMapper.deleteAttachmentByParentId(id, 3L);
        //物理删除送货明细表
        List<Long> middleIds=new ArrayList<>();
        middleIds.add(id);
        List<Long> assetIds = orderDelivedetailMapper.getAssetIdsByDId(middleIds);
        orderDelivedetailMapper.updateDetailsDel(assetIds);
        //物理删除资产实体表
        assetEntityInfoMapper.updateEntitys(assetIds);
        //物理删除资产实体属性值表
        assetEntityPrptMapper.updateEntityprpts(assetIds);
        return ResultVOUtil.returnSuccess();
    }

    @Override
    public ResponseEnvelope updateOrderMiddle(OrderDelivemiddleDTO dto) {
        OrderDelivemiddle entity = this.selectOne(dto.getId());
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        //更新送货记录内容
        orderDelivemiddleMapper.updateById(entity);
        return ResultVOUtil.returnSuccess();
    }


    public OrderDelivemiddle selectOne(Long id) {
        OrderDelivemiddle entity = new OrderDelivemiddle();
        entity.setId(id);
        QueryWrapper<OrderDelivemiddle> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return orderDelivemiddleMapper.selectOne(queryWrapper);
    }


}