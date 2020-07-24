package com.mukutech.websiteservice.service;

import com.mukutech.websiteservice.common.utils.response.ResponseEnvelope;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mukutech.websiteservice.pojo.dto.SysCasusDTO;
import com.mukutech.websiteservice.pojo.entity.SysCasus;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author TCGUO
 * @since 2020-07-23
 */
public interface ISysCasusService extends IService<SysCasus> {
    public ResponseEnvelope searchSysCasusListPage(SysCasusDTO DTO);

    public ResponseEnvelope searchSysCasusOne(Long id);

    public ResponseEnvelope addSysCasus(SysCasusDTO DTO);

    public ResponseEnvelope updateSysCasus(SysCasusDTO DTO);

    public ResponseEnvelope deleteSysCasus(Long id);

    public ResponseEnvelope logicDeleteCasus(Long id);
}
