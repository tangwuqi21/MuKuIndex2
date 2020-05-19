package com.rhdk.purchasingservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.igen.acc.domain.dto.OrgUserDto;
import com.igen.acc.rpc.thrift.IUserService;
import com.igen.auth.thrift.IAuthService;
import com.rhdk.purchasingservice.common.utils.BeanCopyUtil;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.TokenUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.mapper.CommonMapper;
import com.rhdk.purchasingservice.mapper.OrderContractMapper;
import com.rhdk.purchasingservice.mapper.PurcasingContractMapper;
import com.rhdk.purchasingservice.pojo.entity.Customer;
import com.rhdk.purchasingservice.pojo.entity.OrderContract;
import com.rhdk.purchasingservice.pojo.entity.OrderDelivemiddle;
import com.rhdk.purchasingservice.pojo.entity.PurcasingContract;
import com.rhdk.purchasingservice.pojo.query.CustomerQuery;
import com.rhdk.purchasingservice.pojo.query.OrderContractQuery;
import com.rhdk.purchasingservice.pojo.vo.OrderContractVO;
import com.rhdk.purchasingservice.pojo.vo.OrderDelivemiddleVO;
import com.rhdk.purchasingservice.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private PurcasingContractMapper purcasingContractMapper;

    @Autowired
    private OrderContractMapper orderContractMapper;

    @Override
    public ResponseEnvelope getSupplyList(String companyName) {
        List<HashMap<String, Object>> result = commonMapper.getSupplyList(companyName);
        return ResultVOUtil.returnSuccess(result);
    }

    @Override
    public ResponseEnvelope getContractInfoList(OrderContractQuery dto) {
        Page<PurcasingContract> page = new Page<PurcasingContract>();
        page.setSize(dto.getPageSize());
        page.setCurrent(dto.getCurrentPage());
        QueryWrapper<PurcasingContract> queryWrapper = new QueryWrapper<PurcasingContract>();
        PurcasingContract entity = new PurcasingContract();
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        queryWrapper.setEntity(entity);
        page = purcasingContractMapper.selectPage(page, queryWrapper);
        List<PurcasingContract> resultList = page.getRecords();
        List<OrderContractVO> orderContractVOList = resultList.stream().map(a -> {
            OrderContract entity2 = new OrderContract();
            entity2.setId(a.getContractId());
            QueryWrapper<OrderContract> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.setEntity(entity2);
            if (!StringUtils.isEmpty(dto.getContractName())) {
                queryWrapper2.like("CONTRACT_NAME", dto.getContractName());
            }
            entity2 = orderContractMapper.selectOne(queryWrapper2);
            OrderContractVO model = OrderContractVO.builder().build();
            if (entity2 != null) {
                BeanCopyUtil.copyPropertiesIgnoreNull(a, model);
                model.setContractName(entity2.getContractName());
                model.setContractDate(entity2.getContractDate());
                model.setContractCode(entity2.getContractCode());
                model.setContractType(entity2.getContractType());
                model.setContractMoney(entity2.getContractMoney());
            }
            return model;
        }).collect(Collectors.toList());
        Page<OrderContractVO> page2 = new Page<OrderContractVO>();
        page2.setRecords(orderContractVOList);
        page2.setSize(page.getSize());
        page2.setCurrent(page.getCurrent());
        page2.setTotal(page.getTotal());
        page2.setOrders(page.getOrders());
        return ResultVOUtil.returnSuccess(page2);
    }

    @Override
    public ResponseEnvelope getAssetInfoList() {
        List<HashMap<String, Object>> result = commonMapper.getAssetInfoList();
        return ResultVOUtil.returnSuccess(result);
    }

    @Override
    public OrgUserDto getOrgUserById(long orgId, Long userId) {
        return iUserService.getOrgUserById(orgId, userId);
    }

    @Override
    public OrgUserDto getUserInfo() {
        return iUserService.getOrgUserById(TokenUtil.getUserInfo().getOrganizationId(), TokenUtil.getUserInfo().getUserId());
    }

    @Override
    public ResponseEnvelope searchCustomerListPage(CustomerQuery dto) {
        Page<Customer> page = new Page<Customer>();
        page.setSize(dto.getPageSize());
        page.setCurrent(dto.getCurrentPage());
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<Customer>();
        Customer entity = new Customer();
        if (!StringUtils.isEmpty(dto.getCusName())) {
            queryWrapper.like("CUS_NAME", dto.getCusName());
            entity.setCusName(null);
        }
        BeanCopyUtil.copyPropertiesIgnoreNull(dto, entity);
        queryWrapper.setEntity(entity);
        return ResultVOUtil.returnSuccess(commonMapper.selectPage(page, queryWrapper));
    }

    @Override
    public ResponseEnvelope getCommonPrpts() {
        return ResultVOUtil.returnSuccess(commonMapper.getCommonPrpts());
    }

    @Override
    public ResponseEnvelope getModulePrptsById(Long moduleId) {
        return ResultVOUtil.returnSuccess(commonMapper.getModulePrptsById(moduleId));
    }
}
