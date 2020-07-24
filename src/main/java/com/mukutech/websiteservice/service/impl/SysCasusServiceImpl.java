package com.mukutech.websiteservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mukutech.websiteservice.common.utils.BeanCopyUtil;
import com.mukutech.websiteservice.common.utils.ResultVOUtil;
import com.mukutech.websiteservice.common.utils.response.ResponseEnvelope;

import com.mukutech.websiteservice.mapper.SysCasusMapper;
import com.mukutech.websiteservice.pojo.dto.SysCasusDTO;
import com.mukutech.websiteservice.pojo.entity.SysCasus;
import com.mukutech.websiteservice.pojo.entity.SysCorp;
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
    public ResponseEnvelope searchSysCasusListPage() {
        QueryWrapper<SysCasus> queryWrapper = new QueryWrapper<SysCasus>();
        SysCasus entity = new SysCasus();
        entity.setState(1);
        queryWrapper.setEntity(entity);
        queryWrapper.orderByAsc("order_id");
        return ResultVOUtil.returnSuccess(sysCasusMapper.selectList(queryWrapper));
    }

    @Override
    public ResponseEnvelope searchSysCasusOne(Long id) {

        return ResultVOUtil.returnSuccess(this.selectOne(id));
    }

    @Override
    public ResponseEnvelope addSysCasus(SysCasusDTO dto) {
        SysCasus entity = new SysCasus();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        entity.setState(1);
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

    @Override
    public ResponseEnvelope logicDeleteCasus(Long id) {
        SysCasus sysCasus = new SysCasus();
        sysCasus.setState(0);
        UpdateWrapper<SysCasus> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        sysCasusMapper.update(sysCasus, updateWrapper);
        return ResultVOUtil.returnSuccess();
    }

    public SysCasus selectOne(Long id) {
        SysCasus entity = new SysCasus();
        entity.setId(id);
        entity.setState(1);
        QueryWrapper<SysCasus> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return sysCasusMapper.selectOne(queryWrapper);
    }

}
