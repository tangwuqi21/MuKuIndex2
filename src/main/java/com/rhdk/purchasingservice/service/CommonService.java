package com.rhdk.purchasingservice.service;

import com.igen.acc.domain.dto.OrgUserDto;
import com.igen.acc.domain.dto.UserInfoDto;
import com.rhdk.purchasingservice.common.utils.response.ResponseEnvelope;
import com.rhdk.purchasingservice.pojo.query.OrderContractQuery;

public interface CommonService {

  ResponseEnvelope getContractInfoList(OrderContractQuery dto, Long orgId);

  OrgUserDto getOrgUserById(long orgId, Long userId);

  ResponseEnvelope insertRedisData();

  public UserInfoDto getUserInfo(Long userId);
}
