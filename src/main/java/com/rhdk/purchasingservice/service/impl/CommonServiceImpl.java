package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.igen.acc.domain.dto.OrgUserDto;
import com.igen.acc.rpc.thrift.IUserService;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.mapper.OrderContractMapper;
import com.rhdk.purchasingservice.mapper.PurcasingContractMapper;
import com.rhdk.purchasingservice.pojo.entity.PurcasingContract;
import com.rhdk.purchasingservice.pojo.query.OrderContractQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;
import com.rhdk.purchasingservice.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired private IUserService iUserService;

  @Autowired private PurcasingContractMapper purcasingContractMapper;

  @Autowired private OrderContractMapper orderContractMapper;

  @Override
  public ResponseEnvelope getContractInfoList(OrderContractQuery dto, Long orgId) {
    Page page = new Page();
    page.setSize(dto.getPageSize());
    page.setCurrent(dto.getCurrentPage());
    IPage<OrderContractVO> recordsList = orderContractMapper.selectContractList(page, dto, orgId);
    List<OrderContractVO> orderContractVOList = recordsList.getRecords();
    orderContractVOList
        .parallelStream()
        .forEach(
            a -> {
              PurcasingContract entity = new PurcasingContract();
              entity.setContractId(a.getId());
              QueryWrapper<PurcasingContract> queryWrapper = new QueryWrapper<>();
              queryWrapper.setEntity(entity);
              entity = purcasingContractMapper.selectOne(queryWrapper);
              if (entity != null) {
                a.setContractCompany(entity.getContractCompany());
              }
              a.setId(entity.getId());
            });
    recordsList.setRecords(orderContractVOList);
    return ResultVOUtil.returnSuccess(recordsList);
  }

  @Override
  public OrgUserDto getOrgUserById(long orgId, Long userId) {
    return iUserService.getOrgUserById(orgId, userId);
  }
}
