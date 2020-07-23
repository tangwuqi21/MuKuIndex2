package com.mukutech.websiteservice.pojo.entity;
import lombok.Data;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author TCGUO
 * @since 2020-07-23
 */
@Data
    @EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysCasus extends Model<SysCasus> {

private static final long serialVersionUID=1L;

                @TableId(value = "id", type = IdType.AUTO)
                private Long id;
    /**
     * 案例编号
     */
        private String code;
    /**
     * 案例名称
     */
        private String name;
    /**
     * 备注/具体项目描述
     */
        private String node;
    /**
     * 展示图片的地址
     */
        private String picture;
    /**
     * 用于自定义排序
     */
        private Integer orderId;
    /**
     * 状态：1有效，0删除
     */
        private Integer state;


@Override
protected Serializable pkVal(){
            return this.id;
        }

        }
