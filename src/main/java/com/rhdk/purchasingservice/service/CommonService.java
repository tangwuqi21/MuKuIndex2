package com.rhdk.purchasingservice.service;

import com.igen.acc.domain.dto.OrgUserDto;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.query.CustomerQuery;
import com.rhdk.purchasingservice.pojo.query.OrderContractQuery;

public interface CommonService {
    ResponseEnvelope getSupplyList(String companyName);

    ResponseEnvelope getContractInfoList(OrderContractQuery orderContractQuery);

    ResponseEnvelope getAssetInfoList();

    OrgUserDto getOrgUserById(long orgId, Long userId);

    OrgUserDto getUserInfo();

    ResponseEnvelope searchCustomerListPage(CustomerQuery customerQuery);

    ResponseEnvelope getCommonPrpts();

    ResponseEnvelope getModulePrptsById(Long moduleId);
}
