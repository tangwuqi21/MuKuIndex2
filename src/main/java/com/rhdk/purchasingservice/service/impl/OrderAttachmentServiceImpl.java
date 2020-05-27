package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rhdk.purchasingservice.common.utils.BeanCopyUtil;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.mapper.OrderAttachmentMapper;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderAttachment;
import com.rhdk.purchasingservice.pojo.query.OrderAttachmentQuery;
import com.rhdk.purchasingservice.service.IOrderAttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 附件表 服务实现类
 *
 * @author LMYOU
 * @since 2020-05-08
 */
@Slf4j
@Transactional
@Service
public class OrderAttachmentServiceImpl extends ServiceImpl<OrderAttachmentMapper, OrderAttachment>
    implements IOrderAttachmentService {

  @Autowired private OrderAttachmentMapper orderAttachmentMapper;

  @Override
  public ResponseEnvelope searchOrderAttachmentListPage(OrderAttachmentQuery dto) {
    Page<OrderAttachment> page = new Page<OrderAttachment>();
    page.setSize(dto.getPageSize());
    page.setCurrent(dto.getCurrentPage());
    QueryWrapper<OrderAttachment> queryWrapper = new QueryWrapper<OrderAttachment>();
    OrderAttachment entity = new OrderAttachment();
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    queryWrapper.setEntity(entity);
    return ResultVOUtil.returnSuccess(orderAttachmentMapper.selectPage(page, queryWrapper));
  }

  @Override
  public ResponseEnvelope searchOrderAttachmentOne(Long id) {

    return ResultVOUtil.returnSuccess(this.selectOne(id));
  }

  @Override
  public ResponseEnvelope addOrderAttachment(OrderAttachmentDTO dto) {
    OrderAttachment entity = new OrderAttachment();
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    orderAttachmentMapper.insert(entity);
    return ResultVOUtil.returnSuccess();
  }

  @Override
  public ResponseEnvelope updateOrderAttachment(OrderAttachmentDTO dto) {
    OrderAttachment entity = this.selectOne(dto.getId());
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    orderAttachmentMapper.updateById(entity);
    return ResultVOUtil.returnSuccess();
  }

  @Override
  public ResponseEnvelope deleteOrderAttachment(Long id) {
    orderAttachmentMapper.deleteById(id);
    return ResultVOUtil.returnSuccess();
  }

  public OrderAttachment selectOne(Long id) {
    OrderAttachment entity = new OrderAttachment();
    entity.setId(id);
    QueryWrapper<OrderAttachment> queryWrapper = new QueryWrapper<>();
    queryWrapper.setEntity(entity);
    return orderAttachmentMapper.selectOne(queryWrapper);
  }

  /** 批量添加附件信息 */
  @Override
  public List<Long> insertAttachmentListOfIdList(List<OrderAttachmentDTO> attachmentVoList) {

    if (CollectionUtils.isEmpty(attachmentVoList)) {
      return new ArrayList<>();
    }
    return attachmentVoList.stream()
        .map(
            a -> {
              OrderAttachment at =
                  OrderAttachment.builder()
                      .orgfilename(a.getOrgfilename())
                      .fileurl(a.getFileurl())
                      .atttype(a.getAtttype())
                      .parentId(a.getParentId())
                      .discription(a.getDiscription())
                      .build();
              orderAttachmentMapper.insert(at);
              return at.getId();
            })
        .collect(Collectors.toList());
  }

  @Override
  public List<OrderAttachment> selectAttachmentList(Long id) {
    OrderAttachment entity = new OrderAttachment();
    entity.setParentId(id);
    QueryWrapper<OrderAttachment> queryWrapper = new QueryWrapper<>();
    queryWrapper.setEntity(entity);
    queryWrapper.orderByDesc("CREATE_DATE");
    return orderAttachmentMapper.selectList(queryWrapper).stream()
        .map(
            a -> {
              OrderAttachment at = new OrderAttachment();
              BeanCopyUtil.copyPropertiesIgnoreNull(a, at);
              return at;
            })
        .collect(Collectors.toList());
  }

  @Override
  public Integer deleteAttachmentByParentId(Long id, Long attType) {
    return orderAttachmentMapper.deleteAttachmentByParentId(id, attType);
  }

  @Override
  public ResponseEnvelope selectListByParentId(Long id, Integer attType) {
    orderAttachmentMapper.selectListByParentId(id, attType);
    return ResultVOUtil.returnSuccess();
  }
}
