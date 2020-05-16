package com.rhdk.purchasingservice.service;

import com.igen.acc.domain.dto.OrgUserDto;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;

public interface CommonService {
    ResponseEnvelope getSupplyList(String companyName);

    ResponseEnvelope getContractInfoList(String contractName);

    ResponseEnvelope getAssetInfoList();

    OrgUserDto getOrgUserById(long orgId, Long userId);

    OrgUserDto getUserInfo();
}
