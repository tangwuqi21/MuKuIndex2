package com.rhdk.purchasingservice.service.impl;

import com.rhdk.purchasingservice.common.utils.ResultVOUtil;
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
}
