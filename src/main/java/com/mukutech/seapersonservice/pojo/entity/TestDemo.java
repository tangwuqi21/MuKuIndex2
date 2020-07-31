package com.mukutech.seapersonservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户基础属性表
 * </p>
 *
 * @author LMYOU
 * @since 2020-07-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TestDemo extends Model<TestDemo> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户OFFSET
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户账号
     */
    private String userCode;
    /**
     * 性别
     */
    private String gender;
    /**
     * 年龄分段
     */
    private String agePeriod;
    /**
     * 生日
     */
    private Date birthday;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 注册地
     */
    private String registerCity;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
