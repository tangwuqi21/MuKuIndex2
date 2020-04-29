package com.rhdk.purchasingservice.common.utils;

import com.igen.solarman.oauth2.PrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author: YYF
 * @create: 2020-04-29 @Description:
 */
public class TokenUtil {

  // 获得token
  public static String getToken() {
    String token =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .getRequest()
            .getHeader("Authorization");
    return token;
  }

  // 获取User信息  相当于@SolarManPrincipal
  public static PrincipalDetails getUserInfo() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
    return principal;
  }
}
