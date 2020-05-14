package com.rhdk.purchasingservice.service;

import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderAttachment;
import com.rhdk.purchasingservice.pojo.query.OrderAttachmentQuery;

import java.util.List;


/**
 * <p>
 * 附件表 服务类
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-08
 */
public interface IOrderAttachmentService extends IService<OrderAttachment> {
     ResponseEnvelope searchOrderAttachmentListPage(OrderAttachmentQuery DTO);

     ResponseEnvelope searchOrderAttachmentOne(Long id);

     ResponseEnvelope addOrderAttachment(OrderAttachmentDTO DTO);

     ResponseEnvelope updateOrderAttachment(OrderAttachmentDTO DTO);

     ResponseEnvelope deleteOrderAttachment(Long id);

     List<Long> insertAttachmentListOfIdList(List<OrderAttachmentDTO> attachmentVoList);

     List<OrderAttachment> selectAttachmentList(Long id);

    Integer deleteAttachmentByParentId(Long id);
}
