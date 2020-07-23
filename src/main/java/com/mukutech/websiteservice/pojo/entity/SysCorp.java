package com.mukutech.websiteservice.pojo.entity;
import lombok.Data;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class SysCorp extends Model<SysCorp> {

private static final long serialVersionUID=1L;

                @TableId(value = "id", type = IdType.AUTO)
                private Long id;
    /**
     * 公司名称
     */
        private String name;
    /**
     * 公司名称缩写
     */
        private String miniName;
    /**
     * 地址
     */
        private String address;
    /**
     * 电话
     */
        private String phone;
    /**
     * 邮箱
     */
        private String email;
    /**
     * 邮政编码
     */
        private String zipCode;
    /**
     * 版权所有
     */
        private String copyright;
    /**
     * ICP备案号
     */
    @TableField("ICP_code")
        private String icpCode;
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
