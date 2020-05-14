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
import com.rhdk.purchasingservice.pojo.dto.OrderDelivedetailDTO;
import com.rhdk.purchasingservice.pojo.entity.*;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderDelivemiddleVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDeliverecordsVO;
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
        List<OrderDelivemiddleVO> orderDelivemiddleVOList = resultList.stream().map(a -> {
            OrderDelivemiddleVO model = OrderDelivemiddleVO.builder()
                    .attachmentList(orderAttachmentMapper.selectListByParentId(a.getId(), 3))
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

        return ResultVOUtil.returnSuccess(this.selectOne(id));
    }

    @Override
    @Transactional
    public ResponseEnvelope addOrderDelivemiddle(OrderDelivemiddleDTO dto) {
        OrderDelivemiddle entity = new OrderDelivemiddle();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        orderDelivemiddleMapper.insert(entity);
        //进行附件清单的解析及数据源的入库操作
        if ("1".equals(dto.getWmType())) {
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
            try {
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
                //获取资产模板对应的表头信息
                List<Map<String, Object>> titleMap = orderDelivemiddleMapper.getTitleMap(dto.getModuleId());
                //获取表头的下标
                List<Integer> titleIndexM = new ArrayList<>();
                Map<Integer, Long> titleIdM = new HashMap<>();
                for (Map<String, Object> model : titleMap) {
                    titleIndexM.add(Integer.valueOf(model.get("PRPT_ORDER").toString()));
                    titleIdM.put(Integer.valueOf(model.get("PRPT_ORDER").toString()), Long.valueOf(model.get("PRPT_ID").toString()));
                }
                //遍历工作簿中的sheet
                for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
                    Sheet sheet = workbook.getSheetAt(numSheet);
                    //当前sheet页面为空,继续遍历
                    if (sheet == null) {
                        continue;
                    }
                    //读取Excel的表头数据
                    //Row row1 = sheet.getRow(0);
                    // 对于每个sheet，读取其中的每一行,从第二行开始读取
                    for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                        Row row = sheet.getRow(rowNum);
                        if (row == null) {
                            continue;
                        }
                        //这里默认第一列为资产名称,to do
                        Cell cell = row.getCell(1);
                        AssetEntityInfo entityInfo = new AssetEntityInfo();
                        entityInfo.setAmount(1L);
                        entityInfo.setAssetCatId(dto.getAssetCatId());
                        entityInfo.setAssetTemplId(dto.getModuleId());
                        entityInfo.setAssetTemplVer(dto.getModuleVersion());
                        entityInfo.setName(ExcleUtils.getValue(cell, formulaEvaluator));
                        //1.入库资产实体表信息
                        assetEntityInfoMapper.insert(entityInfo);
                        //入库每条资产实体对应的属性值（个性化的，不是共有的,从第二列开始）
                        for (int columnNum = 1; columnNum < row.getLastCellNum(); columnNum++) {
                            //只存储个性的属性，共性的直接跳过
                            if (titleIndexM.contains(columnNum)) {
                                Cell cell2 = row.getCell(columnNum);
                                AssetEntityPrpt assetEntityPrpt = new AssetEntityPrpt();
                                assetEntityPrpt.setAssetId(entityInfo.getId());
                                assetEntityPrpt.setPrptId(titleIdM.get(columnNum));
                                assetEntityPrpt.setVal(ExcleUtils.getValue(cell2, formulaEvaluator));
                                //2.入库到资产实体属性值表中
                                assetEntityPrptMapper.insert(assetEntityPrpt);
                            }
                        }
                        //3.这里进行送货记录的详细表中入库
                        OrderDelivedetail orderDelivedetail = new OrderDelivedetail();
                        orderDelivedetail.setAssetName(entityInfo.getName());
                        orderDelivedetail.setAssetId(entityInfo.getId());
                        orderDelivedetail.setAssetNumber(1L);
                        orderDelivedetail.setAssetCatId(dto.getAssetCatId());
                        orderDelivedetail.setMiddleId(entity.getId());
                        orderDelivedetail.setAssetUnit(dto.getAssetUnit());
                        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
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
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return ResultVOUtil.returnSuccess();
    }

    @Override
    public ResponseEnvelope updateOrderDelivemiddle(OrderDelivemiddleDTO dto) {
        OrderDelivemiddle entity = this.selectOne(dto.getId());
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        orderDelivemiddleMapper.updateById(entity);
        return ResultVOUtil.returnSuccess();
    }

    @Override
    public ResponseEnvelope deleteOrderDelivemiddle(Long id) {
        orderDelivemiddleMapper.deleteById(id);
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
