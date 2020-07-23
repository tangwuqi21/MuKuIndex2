package com.mukutech.websiteservice.pojo.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author TCGUO
 * @since 2020-07-23
 */
@Getter
@Setter
public class SysAdviceDTO extends BaseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private String title;

    private String msg;

    private String email;

    private LocalDateTime createTime;

    private Integer state;

    }
