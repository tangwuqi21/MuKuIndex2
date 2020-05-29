package com.rhdk.purchasingservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rhdk.purchasingservice.pojo.dto.OrderAttachmentDTO;
import com.rhdk.purchasingservice.pojo.entity.OrderAttachment;
import org.apache.ibatis.annotations.Param;

/**
 * 附件表 Mapper 接口
 *
 * @author LMYOU
 * @since 2020-05-08
 */
public interface OrderAttachmentMapper extends BaseMapper<OrderAttachment> {

  Integer updateByParentIdAndType(@Param("dto") OrderAttachmentDTO dto);
}
