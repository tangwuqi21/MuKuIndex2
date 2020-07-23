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
public class SysJob extends Model<SysJob> {

private static final long serialVersionUID=1L;

                @TableId(value = "id", type = IdType.AUTO)
                private Long id;
    /**
     * 职位编号
     */
        private String code;
    /**
     * 职位名称
     */
        private String name;
    /**
     * 所属部门
     */
        private String department;
    /**
     * 状态：1有效，0删除
     */
        private Integer state;


@Override
protected Serializable pkVal(){
            return this.id;
        }

        }
