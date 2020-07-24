package com.mukutech.websiteservice.service;

import com.mukutech.websiteservice.common.utils.response.ResponseEnvelope;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mukutech.websiteservice.pojo.dto.SysJobDTO;
import com.mukutech.websiteservice.pojo.entity.SysJob;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author TCGUO
 * @since 2020-07-23
 */
public interface ISysJobService extends IService<SysJob> {
    public ResponseEnvelope searchSysJobListPage();

    public ResponseEnvelope searchSysJobOne(Long id);

    public ResponseEnvelope addSysJob(SysJobDTO DTO);

    public ResponseEnvelope updateSysJob(SysJobDTO DTO);

    public ResponseEnvelope deleteSysJob(Long id);

    public ResponseEnvelope logicDeleteJob(Long id);
}
