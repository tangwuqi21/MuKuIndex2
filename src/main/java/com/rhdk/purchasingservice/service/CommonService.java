package com.rhdk.purchasingservice.service;

import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;

public interface CommonService {
    ResponseEnvelope getSupplyList(String companyName);

    ResponseEnvelope getContractInfoList(String contractName);

    ResponseEnvelope getAssetInfoList();
}
