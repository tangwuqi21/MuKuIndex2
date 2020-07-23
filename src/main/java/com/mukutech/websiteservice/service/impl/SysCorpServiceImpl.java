package com.mukutech.websiteservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mukutech.websiteservice.common.utils.BeanCopyUtil;
import com.mukutech.websiteservice.common.utils.ResultVOUtil;
import com.mukutech.websiteservice.common.utils.response.ResponseEnvelope;
import com.mukutech.websiteservice.mapper.SysCorpMapper;
import com.mukutech.websiteservice.pojo.dto.SysCorpDTO;
import com.mukutech.websiteservice.pojo.entity.SysCorp;
import com.mukutech.websiteservice.service.ISysCorpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author TCGUO
 * @since 2020-07-23
 */
@Slf4j
@Transactional
@Service
public class SysCorpServiceImpl extends ServiceImpl<SysCorpMapper, SysCorp> implements ISysCorpService {


    @Autowired
    private SysCorpMapper sysCorpMapper;

    @Override
    public ResponseEnvelope searchSysCorpListPage(SysCorpDTO dto) {
        Page<SysCorp> page = new Page<SysCorp>();
        page.setSize(dto.getPageSize());
        page.setCurrent(dto.getCurrentPage());
        QueryWrapper<SysCorp> queryWrapper = new QueryWrapper<SysCorp>();
        SysCorp entity = new SysCorp();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        queryWrapper.setEntity(entity);
        return ResultVOUtil.returnSuccess(sysCorpMapper.selectPage(page, queryWrapper));
    }

    @Override
    public ResponseEnvelope searchSysCorpOne(Long id) {

        return ResultVOUtil.returnSuccess(this.selectOne(id));
    }

    @Override
    public ResponseEnvelope addSysCorp(SysCorpDTO dto) {
        SysCorp entity = new SysCorp();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        sysCorpMapper.insert(entity);
        return ResultVOUtil.returnSuccess();
    }

    @Override
    public ResponseEnvelope updateSysCorp(SysCorpDTO dto) {
        SysCorp entity = this.selectOne(dto.getId());
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        sysCorpMapper.updateById(entity);
        return ResultVOUtil.returnSuccess();
    }

    @Override
    public ResponseEnvelope deleteSysCorp(Long id) {
        sysCorpMapper.deleteById(id);
        return ResultVOUtil.returnSuccess();
    }


    public SysCorp selectOne(Long id) {
        SysCorp entity = new SysCorp();
        entity.setId(id);
        QueryWrapper<SysCorp> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return sysCorpMapper.selectOne(queryWrapper);
    }

}
