package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.igen.acc.domain.dto.OrgUserDto;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.*;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.feign.AssetServiceFeign;
import com.rhdk.purchasingservice.mapper.*;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderDeliverecordsDTO;
import com.rhdk.purchasingservice.pojo.entity.*;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;
import com.rhdk.purchasingservice.pojo.query.OrderDeliverecordsQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDelivemiddleVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDeliverecordsVO;
import com.rhdk.purchasingservice.service.*;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 送货单 服务实现类
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-12
 */
@Slf4j
@Transactional
@Service
public class OrderDeliverecordsServiceImpl extends ServiceImpl<OrderDeliverecordsMapper, OrderDeliverecords> implements IOrderDeliverecordsService {


    @Autowired
    private OrderDeliverecordsMapper orderDeliverecordsMapper;

    @Autowired
    private IOrderAttachmentService iOrderAttachmentService;

    @Autowired
    private IOrderDelivemiddleService iOrderDelivemiddleService;

    @Autowired
    private OrderAttachmentMapper orderAttachmentMapper;

    @Autowired
    private OrderDelivedetailMapper orderDelivedetailMapper;

    @Autowired
    private OrderContractMapper orderContractMapper;

    @Autowired
    private OrderDelivemiddleMapper orderDelivemiddleMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private AssetServiceFeign assetServiceFeign;

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(OrderDeliverecordsServiceImpl.class);


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
        Map<Integer, String> supplierMap = new HashMap<>();
        logger.info("searchOrderDeliverecordsListPage--fegin远程获取供应商列表信息开始");
        List<HashMap<String, Object>> resultMap = (List<HashMap<String, Object>>) assetServiceFeign.getSupplyList(null, TokenUtil.getToken()).getData();
        logger.info("searchOrderDeliverecordsListPage--fegin远程获取供应商列表信息共：" + resultMap.size() + "条，结束");
        for (HashMap<String, Object> model : resultMap) {
            supplierMap.put(Integer.valueOf(model.get("id").toString()), model.get("custName").toString());
        }
        //获取源单信息，获取附件列表信息
        List<OrderDeliverecords> resultList = page.getRecords();
        logger.info("searchOrderDeliverecordsListPage--获取送货单信息开始");
        List<OrderDeliverecordsVO> orderDeliverecordsVOList = resultList.stream().map(a -> {
            OrderContractVO orderContract = orderContractMapper.selectContractById(a.getOrderId());
            OrgUserDto userDto = commonService.getOrgUserById(a.getOrgId(), a.getCreateBy());
            List<Integer> signStatList = orderDelivemiddleMapper.getSignStatus(a.getId());
            Integer status = 0;
            if (signStatList.size() == 1 && signStatList.contains(0)) {
                status = 0;
            } else if (signStatList.size() == 1 && signStatList.contains(2)) {
                status = 2;
            } else if (signStatList.size() >= 1) {
                status = 1;
            }
            OrderDeliverecordsVO orderDeliverecordsVO = OrderDeliverecordsVO.builder()
                    .supplierName(supplierMap.get(a.getSupplierId()))
                    .attachmentList(orderAttachmentMapper.selectListByParentId(a.getId(), 2))
                    .createName(userDto.getUserInfo().getName()).deptName(userDto.getGroupName())
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
            return orderDeliverecordsVO;
        }).collect(Collectors.toList());
        logger.info("searchOrderDeliverecordsListPage--获取送货单信息共：" + orderDeliverecordsVOList.size() + "条，结束");
        Page<OrderDeliverecordsVO> page2 = new Page<OrderDeliverecordsVO>();
        page2.setRecords(orderDeliverecordsVOList);
        page2.setSize(page.getSize());
        page2.setCurrent(page.getCurrent());
        page2.setTotal(page.getTotal());
        page2.setOrders(page.getOrders());
        return ResultVOUtil.returnSuccess(page2);
    }

    @Override
    public ResponseEnvelope searchOrderDeliverecordsOne(Long id) {
        OrderDeliverecords entity = this.selectOne(id);
        OrderDeliverecordsVO orderDeliverecordsVO = new OrderDeliverecordsVO();
        logger.info("searchOrderDeliverecordsOne--获取单个送货单信息开始");
        OrderContractVO orderContract = orderContractMapper.selectContractById(entity.getOrderId());
        OrgUserDto userDto = commonService.getOrgUserById(entity.getOrgId(), entity.getCreateBy());
        List<Integer> signStatList = orderDelivemiddleMapper.getSignStatus(id);
        Integer status = 0;
        if (signStatList.size() == 1 && signStatList.contains(0)) {
            status = 0;
        } else if (signStatList.size() == 1 && signStatList.contains(2)) {
            status = 2;
        } else if (signStatList.size() >= 1) {
            status = 1;
        }
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
        orderDeliverecordsVO.setCreateName(userDto.getUserInfo().getName());
        orderDeliverecordsVO.setDeptName(userDto.getGroupName());
        orderDeliverecordsVO.setSignStatus(status);
        orderDeliverecordsVO.setAttachmentList(orderAttachmentMapper.selectListByParentId(entity.getId(), 2));
        //添加送货记录明细信息
        OrderDelivemiddleQuery orderDelivemiddleQuery = new OrderDelivemiddleQuery();
        orderDelivemiddleQuery.setDeliveryId(entity.getId());
        orderDelivemiddleQuery.setPageSize(999999999);
        ResponseEnvelope result = iOrderDelivemiddleService.searchOrderDelivemiddleListPage(orderDelivemiddleQuery);
        Page<OrderDelivemiddleVO> page = (Page<OrderDelivemiddleVO>) result.getData();
        orderDeliverecordsVO.setDelivemiddleVOList(page.getRecords());
        return ResultVOUtil.returnSuccess(orderDeliverecordsVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEnvelope addOrderDeliverecords(OrderDeliverecordsDTO dto) throws Exception {
        //保存送货记录基本信息
        OrderDeliverecords entity = new OrderDeliverecords();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        entity.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
        //这里自动生成送货记录业务编码，规则为：SH+时间戳
        if (StringUtils.isEmpty(entity.getDeliveryCode())) {
            String code = NumberUtils.createCode("SH");
            entity.setDeliveryCode(code);
        }
        logger.info("addOrderDeliverecords--新增送货单信息开始");
        orderDeliverecordsMapper.insert(entity);
        logger.info("addOrderDeliverecords--新增送货单信息结束");
        //保存送货记录附件信息
        if (CollectionUtils.isEmpty(dto.getAttachmentList())) {
            return ResultVOUtil.returnFail(ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
        }
        for (OrderAttachmentDTO model : dto.getAttachmentList()) {
            model.setParentId(entity.getId());
            model.setAtttype(2);
        }
        List<Long> filelist = iOrderAttachmentService.insertAttachmentListOfIdList(dto.getAttachmentList());
        if (filelist.size() > 0) {
            //循环保存送货记录明细基本信息，这里需要判断该资产是物管还是量管，物管需要有对应的明细Excel，量管可以没有对应的附件
            if (CollectionUtils.isEmpty(dto.getOrderDelivemiddleDTOList())) {
                return ResultVOUtil.returnFail(ResultEnum.DETAIL_NOTNULL.getCode(), ResultEnum.DETAIL_NOTNULL.getMessage());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEnvelope updateOrderDeliverecords(OrderDeliverecordsDTO dto) throws Exception {
        if (StringUtils.isEmpty(dto.getId())) {
            return ResultVOUtil.returnFail(ResultEnum.ID_NOTNULL.getCode(), ResultEnum.ID_NOTNULL.getMessage());
        }
        OrderDeliverecords entity = this.selectOne(dto.getId());
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        entity.setOrgId(TokenUtil.getUserInfo().getOrganizationId());
        //更新送货记录内容
        orderDeliverecordsMapper.updateById(entity);
        //更新送货记录附件内容
        if (CollectionUtils.isEmpty(dto.getAttachmentList())) {
            return ResultVOUtil.returnFail(ResultEnum.FILE_NOTNULL.getCode(), ResultEnum.FILE_NOTNULL.getMessage());
        }
        for (OrderAttachmentDTO model : dto.getAttachmentList()) {
            if (StringUtils.isEmpty(model.getId())) {
                return ResultVOUtil.returnFail(ResultEnum.ID_NOTNULL.getCode(), ResultEnum.ID_NOTNULL.getMessage());
            }
            OrderAttachment orderAttachment = new OrderAttachment();
            model.setParentId(entity.getId());
            model.setAtttype(2);
            BeanCopyUtil.copyPropertiesIgnoreNull(model, orderAttachment);
            orderAttachmentMapper.updateById(orderAttachment);
        }
        //更新中间表相关字段
        if (dto.getOrderDelivemiddleDTOList().size() > 0) {
            for (OrderDelivemiddleDTO model : dto.getOrderDelivemiddleDTOList()) {
                if (StringUtils.isEmpty(model.getId())) {
                    return ResultVOUtil.returnFail(ResultEnum.ID_NOTNULL.getCode(), ResultEnum.ID_NOTNULL.getMessage());
                }
                OrderDelivemiddle orderDelivemiddle = new OrderDelivemiddle();
                BeanCopyUtil.copyPropertiesIgnoreNull(model, orderDelivemiddle);
                //更新签收对照表
                orderDelivemiddleMapper.updateById(orderDelivemiddle);
                //判断是否切换了资产类型
                OrderDelivemiddle selectResult = orderDelivemiddleMapper.selectById(model);
                if (model.getModuleId() == selectResult.getModuleId()) {
                    if ("2".equals(model.getWmType())) {
                        //物管状态的需要进行附件内容是否变化判断
                        List<Map<String, Object>> result = orderAttachmentMapper.selectListByParentId(model.getId(), 3);
                        if (!CollectionUtils.isEmpty(result)) {
                            //判断明细附件是否进行了改变，如果附件未发生改变，则不需要进行附件的明细记录更新，如果附件发生了改变，则更新送货记录明细附件列表
                            Map<String, Object> attachmentInfo = result.get(0);
                            if (!model.getFileUrl().equals(attachmentInfo.get("fileurl"))) {
                                //明细附件发生了改变，需要重新解析数据表格进行数据的上传
                                //通过明细中间表找到明细表，通过明细表，去到资产实体表中进行之前的数据删除，然后删除明细中间表的数据
                                List<Long> detailAssetIds = orderDelivedetailMapper.getAssetIds(model.getId());
                                Long[] strArray = new Long[detailAssetIds.size()];
                                detailAssetIds.toArray(strArray);
                                //删除资产实体表相关信息
                                assetServiceFeign.deleteEntitys(strArray, TokenUtil.getToken());
                                //删除资产实体属性值表
                                assetServiceFeign.deleteEntityPrpts(strArray, TokenUtil.getToken());
                                //删除明细表的数据
                                orderDelivedetailMapper.deleteDeliveDetails(detailAssetIds);
                                //变更资产实体表，资产实体属性值表，送货明细表的三种资产状态）
                                iOrderDelivemiddleService.updateAssetStatus(model);
                                //更新附件表
                                OrderAttachmentDTO orderAttachment = new OrderAttachmentDTO();
                                orderAttachment.setParentId(model.getId());
                                orderAttachment.setAtttype(3);
                                orderAttachment.setFileurl(model.getFileUrl());
                                orderAttachment.setOrgfilename(model.getFileName());
                                orderAttachmentMapper.updateByParentIdAndType(orderAttachment);
                            }
                        }
                    } else {
                        //量管的资产，更新明细表和资产实体表中的数量
                        List<Long> detailAssetIds = orderDelivedetailMapper.getAssetIds(model.getId());
                        Long[] strArray = new Long[detailAssetIds.size()];
                        detailAssetIds.toArray(strArray);
                        orderDelivedetailMapper.updateDetails(detailAssetIds, model);
                        assetServiceFeign.updateEntityInfo(strArray, model, TokenUtil.getToken());

                    }
                } else {
                    //切换了资产模板，需要清空之前的资产明细数据并重新解析Excel表格进行数据的上传
                    //通过明细中间表找到明细表，通过明细表，去到资产实体表中进行之前的数据删除，然后删除明细中间表的数据
                    List<Long> detailAssetIds = orderDelivedetailMapper.getAssetIds(model.getId());
                    Long[] strArray = new Long[detailAssetIds.size()];
                    detailAssetIds.toArray(strArray);
                    //删除资产实体表相关信息
                    assetServiceFeign.deleteEntitys(strArray, TokenUtil.getToken());
                    //删除资产实体属性值表
                    assetServiceFeign.deleteEntityPrpts(strArray, TokenUtil.getToken());
                    //删除明细表的数据
                    orderDelivedetailMapper.deleteDeliveDetails(detailAssetIds);
                    //判断切换后的模板类型是物管还是量管，物管更新资产实体表，资产实体属性值表，送货明细表的三种资产状态，量管新增一条数据
                    if ("2".equals(model.getWmType())) {
                        //变更资产实体表，资产实体属性值表，送货明细表的三种资产状态）
                        iOrderDelivemiddleService.updateAssetStatus(model);
                    } else {
                        //执行量管的数据记录
                        AssetEntityInfo entityInfo = new AssetEntityInfo();
                        entityInfo.setAmount(model.getAssetNumber());
                        entityInfo.setAssetCatId(model.getAssetCatId());
                        entityInfo.setAssetTemplId(model.getModuleId());
                        entityInfo.setAssetTemplVer(model.getModuleVersion());
                        entityInfo.setItemNo(model.getItemNO());
                        entityInfo.setAssetStatus(0L);
                        //1.入库资产实体表信息
                        entityInfo = assetServiceFeign.addAssetEntityInfo(entityInfo, model.getToken()).getData();
                        //3.这里进行送货记录的详细表中入库
                        OrderDelivedetail orderDelivedetail = new OrderDelivedetail();
                        orderDelivedetail.setAssetId(entityInfo.getId());
                        orderDelivedetail.setAssetNumber(model.getAssetNumber());
                        orderDelivedetail.setAssetCatId(model.getAssetCatId());
                        orderDelivedetail.setMiddleId(entity.getId());
                        orderDelivedetail.setItemNo(model.getItemNO());
                        orderDelivedetailMapper.insert(orderDelivedetail);
                    }
                    //更新附件表
                    OrderAttachmentDTO orderAttachment = new OrderAttachmentDTO();
                    orderAttachment.setParentId(model.getId());
                    orderAttachment.setAtttype(3);
                    orderAttachment.setFileurl(model.getFileUrl());
                    orderAttachment.setOrgfilename(model.getFileName());
                    orderAttachmentMapper.updateByParentIdAndType(orderAttachment);
                }
            }
        }
        return ResultVOUtil.returnSuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEnvelope deleteOrderDeliverecords(Long id) throws Exception {
        orderDeliverecordsMapper.deleteById(id);
        orderAttachmentMapper.deleteAttachmentByParentId(id, 2L);
        iOrderDelivemiddleService.deleteByPassNo(id);
        return ResultVOUtil.returnSuccess();
    }

    public OrderDeliverecords selectOne(Long id) {
        OrderDeliverecords entity = new OrderDeliverecords();
        entity.setId(id);
        QueryWrapper<OrderDeliverecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return orderDeliverecordsMapper.selectOne(queryWrapper);
    }
}
