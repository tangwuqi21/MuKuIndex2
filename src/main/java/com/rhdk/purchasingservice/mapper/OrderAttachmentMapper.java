package com.rhdk.purchasingservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rhdk.purchasingservice.pojo.entity.OrderAttachment;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 * 附件表 Mapper 接口
 * </p>
 *
 * @author LMYOU
 * @since 2020-05-08
 */
public interface OrderAttachmentMapper extends BaseMapper<OrderAttachment> {

    Integer deleteAttachmentByParentId(@Param("id") Long id);
}
