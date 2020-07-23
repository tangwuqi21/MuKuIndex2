package com.mukutech.websiteservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mukutech.websiteservice.common.utils.BeanCopyUtil;
import com.mukutech.websiteservice.common.utils.ResultVOUtil;
import com.mukutech.websiteservice.common.utils.response.ResponseEnvelope;
import com.mukutech.websiteservice.mapper.SysJobMapper;
import com.mukutech.websiteservice.pojo.dto.SysJobDTO;
import com.mukutech.websiteservice.pojo.entity.SysJob;
import com.mukutech.websiteservice.service.ISysJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author TCGUO
 * @since 2020-07-23
 */
@Slf4j
@Transactional
@Service
        public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements ISysJobService {


        @Autowired
        private SysJobMapper sysJobMapper;

        @Override
        public ResponseEnvelope searchSysJobListPage(SysJobDTO dto){
        Page<SysJob> page=new Page<SysJob>();
        page.setSize(dto.getPageSize());
        page.setCurrent(dto.getCurrentPage());
        QueryWrapper<SysJob>queryWrapper=new QueryWrapper<SysJob>();
    SysJob entity=new SysJob();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto,entity);
        queryWrapper.setEntity(entity);
        return ResultVOUtil.returnSuccess(sysJobMapper.selectPage(page,queryWrapper));
        }
        @Override
        public ResponseEnvelope searchSysJobOne(Long id){

        return ResultVOUtil.returnSuccess(this.selectOne(id));
        }
        @Override
        public ResponseEnvelope addSysJob(SysJobDTO dto){
    SysJob entity=new SysJob();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto,entity);
    sysJobMapper.insert(entity);
        return ResultVOUtil.returnSuccess();
        }
        @Override
        public ResponseEnvelope updateSysJob(SysJobDTO dto){
    SysJob entity=this.selectOne(dto.getId());
        BeanCopyUtil.copyPropertiesIgnoreNull(dto,entity);
    sysJobMapper.updateById(entity);
        return ResultVOUtil.returnSuccess();
        }
        @Override
        public ResponseEnvelope deleteSysJob(Long id){
    sysJobMapper.deleteById(id);
        return ResultVOUtil.returnSuccess();
        }


        public SysJob selectOne(Long id){
    SysJob entity=new SysJob();
        entity.setId(id);
        QueryWrapper<SysJob>queryWrapper=new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return sysJobMapper.selectOne(queryWrapper);
        }

        }
