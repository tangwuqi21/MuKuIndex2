package com.mukutech.websiteservice.pojo.entity;

import lombok.Data;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
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
public class SysAdvice extends Model<SysAdvice> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String title;
    private String msg;
    private String email;
    private LocalDateTime createTime;
    private Integer state;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
