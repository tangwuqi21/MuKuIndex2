package com.mukutech.websiteservice.service;

import com.mukutech.websiteservice.common.utils.response.ResponseEnvelope;
import com.mukutech.websiteservice.pojo.entity.SysAdvice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mukutech.websiteservice.pojo.dto.SysAdviceDTO;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author TCGUO
 * @since 2020-07-23
 */
public interface ISysAdviceService extends IService<SysAdvice> {
    public ResponseEnvelope searchSysAdviceListPage(SysAdviceDTO DTO);

    public ResponseEnvelope searchSysAdviceOne(Long id);

    public ResponseEnvelope addSysAdvice(SysAdviceDTO DTO);

    public ResponseEnvelope updateSysAdvice(SysAdviceDTO DTO);

    public ResponseEnvelope deleteSysAdvice(Long id);
}
