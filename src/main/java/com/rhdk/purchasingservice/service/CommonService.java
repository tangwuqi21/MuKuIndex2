package com.rhdk.purchasingservice.service;

import com.igen.acc.domain.dto.OrgUserDto;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.query.CustomerQuery;
import com.rhdk.purchasingservice.pojo.query.OrderContractQuery;

public interface CommonService {

    ResponseEnvelope getContractInfoList(OrderContractQuery orderContractQuery);

    OrgUserDto getOrgUserById(long orgId, Long userId);

}
