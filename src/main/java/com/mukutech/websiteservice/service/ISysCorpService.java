package com.mukutech.websiteservice.service;

import com.mukutech.websiteservice.common.utils.response.ResponseEnvelope;
import com.mukutech.websiteservice.pojo.entity.SysCorp;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mukutech.websiteservice.pojo.dto.SysCorpDTO;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author TCGUO
 * @since 2020-07-23
 */
public interface ISysCorpService extends IService<SysCorp> {
public ResponseEnvelope searchSysCorpListPage(SysCorpDTO DTO);
public ResponseEnvelope searchSysCorpOne(Long id);
public ResponseEnvelope addSysCorp(SysCorpDTO DTO);
public ResponseEnvelope updateSysCorp(SysCorpDTO DTO);
public ResponseEnvelope deleteSysCorp(Long id);
        }
