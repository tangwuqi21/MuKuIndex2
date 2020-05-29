package com.rhdk.purchasingservice.service;

import com.igen.acc.domain.dto.OrgUserDto;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.query.CommonQuery;

public interface CommonService {

  ResponseEnvelope getContractInfoList(CommonQuery commonQuery);

  OrgUserDto getOrgUserById(long orgId, Long userId);
}
