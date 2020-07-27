package com.mukutech.websiteservice.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author SnowLee
 * @since 2020-07-27
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
     * 发布日期
     */
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
        private Date publishDate;
    /**
     * 状态：1有效，0删除
     */
        private Integer state;


    @Override
    protected Serializable pkVal(){
            return this.id;
        }

}
