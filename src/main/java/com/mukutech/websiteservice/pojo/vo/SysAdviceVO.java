package com.mukutech.websiteservice.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author TCGUO
 * @since 2020-07-23
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysAdviceVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private String title;

    private String msg;

    private String email;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    private Integer state;

    }