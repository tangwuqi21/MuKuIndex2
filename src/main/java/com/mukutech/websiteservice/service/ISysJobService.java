package com.mukutech.websiteservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.mukutech.websiteservice.common.utils.response.ResponseEnvelope;
import com.mukutech.websiteservice.pojo.dto.SysJobDTO;
import com.mukutech.websiteservice.pojo.entity.SysJob;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author SnowLee
 * @since 2020-07-27
 */
public interface ISysJobService extends IService<SysJob> {
public ResponseEnvelope searchSysJobListPage(SysJobDTO DTO);
public ResponseEnvelope searchSysJobOne(Long id);
public ResponseEnvelope addSysJob(SysJobDTO DTO);
public ResponseEnvelope updateSysJob(SysJobDTO DTO);
public ResponseEnvelope deleteSysJob(Long id);
        }
