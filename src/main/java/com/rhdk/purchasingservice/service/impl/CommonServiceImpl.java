package com.rhdk.purchasingservice.service.impl;

import com.igen.acc.domain.dto.OrgUserDto;
import com.igen.acc.rpc.thrift.IUserService;
import com.igen.auth.thrift.IAuthService;
import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
import com.rhdk.purchasingservice.common.utils.TokenUtil;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.mapper.CommonMapper;
import com.rhdk.purchasingservice.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private IUserService iUserService;

    @Override
    public ResponseEnvelope getSupplyList(String companyName) {
        List<HashMap<String,Object>> result=commonMapper.getSupplyList(companyName);
        return ResultVOUtil.returnSuccess(result);
    }

    @Override
    public ResponseEnvelope getContractInfoList(String contractName) {
        List<HashMap<String,Object>> result=commonMapper.getContractInfoList(contractName);
        return ResultVOUtil.returnSuccess(result);
    }

    @Override
    public ResponseEnvelope getAssetInfoList() {
        List<HashMap<String,Object>> result=commonMapper.getAssetInfoList();
        return ResultVOUtil.returnSuccess(result);
    }

    @Override
    public OrgUserDto getOrgUserById(long orgId, Long userId) {
        return iUserService.getOrgUserById(orgId,userId);
    }

    @Override
    public OrgUserDto getUserInfo() {
        return iUserService.getOrgUserById(TokenUtil.getUserInfo().getOrganizationId(),TokenUtil.getUserInfo().getUserId());
    }
}
