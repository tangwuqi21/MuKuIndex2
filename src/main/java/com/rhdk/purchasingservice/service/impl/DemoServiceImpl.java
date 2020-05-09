package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rhdk.purchasingservice.common.utils.BeanCopyUtil;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.mapper.DemoMapper;
import com.rhdk.purchasingservice.pojo.entity.Demo;
import com.rhdk.purchasingservice.pojo.vo.DemoVo;
import com.rhdk.purchasingservice.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 巡查主题 服务实现类
 *
 * @author LMYOU
 * @since 2020-04-20
 */
@Transactional
@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo> implements DemoService {

  @Autowired
  private DemoMapper demoMapper;


  @Override
  public ResponseEnvelope searchTPatrolThemeListPage(DemoVo dto) {
    QueryWrapper<Demo> queryWrapper = new QueryWrapper<Demo>();
    Demo entity = new Demo();
    BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
    queryWrapper.setEntity(entity);
    return ResultVOUtil.returnSuccess(demoMapper.selectList(queryWrapper));
  }
}
