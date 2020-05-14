package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rhdk.purchasingservice.common.enums.ResultEnum;
import com.rhdk.purchasingservice.common.utils.BeanCopyUtil;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.mapper.OrderAttachmentMapper;
import com.rhdk.purchasingservice.mapper.OrderContractMapper;
import com.rhdk.purchasingservice.mapper.OrderDelivemiddleMapper;
import com.rhdk.purchasingservice.mapper.OrderDeliverecordsMapper;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivedetailDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderDelivemiddleDTO;
import com.rhdk.purchasingservice.pojo.dto.OrderDeliverecordsDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderContract;
import com.rhdk.purchasingservice.pojo.entity.OrderDeliverecords;
import com.rhdk.purchasingservice.pojo.query.OrderDelivemiddleQuery;
import com.rhdk.purchasingservice.pojo.query.OrderDeliverecordsQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDelivemiddleVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDeliverecordsVO;
import com.rhdk.purchasingservice.service.IOrderAttachmentService;
import com.rhdk.purchasingservice.service.IOrderDelivedetailService;
import com.rhdk.purchasingservice.service.IOrderDelivemiddleService;
import com.rhdk.purchasingservice.service.IOrderDeliverecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

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
    private OrderContractMapper orderContractMapper;

    @Autowired
    private OrderDelivemiddleMapper orderDelivemiddleMapper;

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
        //获取源单信息，获取附件列表信息
        List<OrderDeliverecords> resultList = page.getRecords();
        List<OrderDeliverecordsVO> orderDeliverecordsVOList = resultList.stream().map(a -> {
            OrderContractVO orderContract = orderContractMapper.selectContractById(a.getOrderId());
            Map<String, String> userInfo = orderContractMapper.getUserNameByID(a.getCreateBy());
            OrderDeliverecordsVO orderDeliverecordsVO = OrderDeliverecordsVO.builder()
                    .contractCode(orderContract.getContractCode())
                    .contractName(orderContract.getContractName())
                    .contractType(orderContract.getContractType())
                    .supplierId(a.getSupplierId()).id(a.getId())
                    .deliveryCode(a.getDeliveryCode()).deliveryDate(a.getDeliveryDate())
                    .deliveryName(a.getDeliveryName()).signAddress(a.getSignAddress())
                    .remark(a.getRemark()).createDate(a.getCreateDate())
                    .attachmentList(orderAttachmentMapper.selectListByParentId(a.getId(), 2))
                    .createName(userInfo.get("userName")).deptName(userInfo.get("deptName"))
                    .build();
            return orderDeliverecordsVO;
        }).collect(Collectors.toList());
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
        OrderContractVO orderContract = orderContractMapper.selectContractById(entity.getOrderId());
        Map<String, String> userInfo = orderContractMapper.getUserNameByID(entity.getCreateBy());
        BeanCopyUtil.copyPropertiesIgnoreNull(entity, orderDeliverecordsVO);
        orderDeliverecordsVO.setContractCode(orderContract.getContractCode());
        orderDeliverecordsVO.setContractName(orderContract.getContractName());
        orderDeliverecordsVO.setContractType(orderContract.getContractType());
        orderDeliverecordsVO.setCreateName(userInfo.get("userName"));
        orderDeliverecordsVO.setDeptName(userInfo.get("deptName"));
        orderDeliverecordsVO.setAttachmentList(orderAttachmentMapper.selectListByParentId(entity.getId(), 2));
        //添加送货记录明细信息
        OrderDelivemiddleQuery orderDelivemiddleQuery = new OrderDelivemiddleQuery();
        orderDelivemiddleQuery.setDeliveryId(entity.getId());
        ResponseEnvelope result = iOrderDelivemiddleService.searchOrderDelivemiddleListPage(orderDelivemiddleQuery);
        Page<OrderDelivemiddleVO> page = (Page<OrderDelivemiddleVO>) result.getData();
        orderDeliverecordsVO.setDelivemiddleVOList(page.getRecords());
        return ResultVOUtil.returnSuccess(orderDeliverecordsVO);
    }

    @Override
    @Transactional
    public ResponseEnvelope addOrderDeliverecords(OrderDeliverecordsDTO dto) {
        //保存送货记录基本信息
        OrderDeliverecords entity = new OrderDeliverecords();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        orderDeliverecordsMapper.insert(entity);
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
            for (OrderDelivemiddleDTO delivemiddleDTO : dto.getOrderDelivemiddleDTOList()) {
                delivemiddleDTO.setDeliveryId(entity.getId());
                ResponseEnvelope result = iOrderDelivemiddleService.addOrderDelivemiddle(delivemiddleDTO);
                if (result.getCode() != 0) {
                    break;
                }
            }
        } else {
            return ResultVOUtil.returnFail(ResultEnum.DETAIL_NOTNULL.getCode(), "保存附件发生异常");
        }
        return ResultVOUtil.returnSuccess();
    }

    @Override
    public ResponseEnvelope updateOrderDeliverecords(OrderDeliverecordsDTO dto) {
        OrderDeliverecords entity = this.selectOne(dto.getId());
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        orderDeliverecordsMapper.updateById(entity);
        return ResultVOUtil.returnSuccess();
    }

    @Override
    public ResponseEnvelope deleteOrderDeliverecords(Long id) {
        orderDeliverecordsMapper.deleteById(id);
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
