package com.mukutech.websiteservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mukutech.websiteservice.common.utils.BeanCopyUtil;
import com.mukutech.websiteservice.common.utils.ResultVOUtil;
import com.mukutech.websiteservice.common.utils.response.ResponseEnvelope;

import com.mukutech.websiteservice.mapper.SysCasusMapper;
import com.mukutech.websiteservice.pojo.dto.SysCasusDTO;
import com.mukutech.websiteservice.pojo.entity.SysCasus;
import com.mukutech.websiteservice.service.ISysCasusService;
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
public class SysCasusServiceImpl extends ServiceImpl<SysCasusMapper, SysCasus> implements ISysCasusService {


    @Autowired
    private SysCasusMapper sysCasusMapper;

    @Override
    public ResponseEnvelope searchSysCasusListPage(SysCasusDTO dto) {
        Page<SysCasus> page = new Page<SysCasus>();
        page.setSize(dto.getPageSize());
        page.setCurrent(dto.getCurrentPage());
        QueryWrapper<SysCasus> queryWrapper = new QueryWrapper<SysCasus>();
        SysCasus entity = new SysCasus();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        queryWrapper.setEntity(entity);
        return ResultVOUtil.returnSuccess(sysCasusMapper.selectPage(page, queryWrapper));
    }

    @Override
    public ResponseEnvelope searchSysCasusOne(Long id) {

        return ResultVOUtil.returnSuccess(this.selectOne(id));
    }

    @Override
    public ResponseEnvelope addSysCasus(SysCasusDTO dto) {
        SysCasus entity = new SysCasus();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        sysCasusMapper.insert(entity);
        return ResultVOUtil.returnSuccess();
    }

    @Override
    public ResponseEnvelope updateSysCasus(SysCasusDTO dto) {
        SysCasus entity = this.selectOne(dto.getId());
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        sysCasusMapper.updateById(entity);
        return ResultVOUtil.returnSuccess();
    }

    @Override
    public ResponseEnvelope deleteSysCasus(Long id) {
        sysCasusMapper.deleteById(id);
        return ResultVOUtil.returnSuccess();
    }


    public SysCasus selectOne(Long id) {
        SysCasus entity = new SysCasus();
        entity.setId(id);
        QueryWrapper<SysCasus> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return sysCasusMapper.selectOne(queryWrapper);
    }

}
