package com.rhdk.purchasingservice.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

 @Override
 public void insertFill(MetaObject metaObject) {
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
//    this.strictInsertFill(
//        metaObject, "createby", Long.class, principal.getUserId()); // 起始版本 3.3.0(推荐使用)
 }

 @Override
 public void updateFill(MetaObject metaObject) {
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
   this.strictUpdateFill(metaObject, "updateDate", Date.class, new Date());
//    this.strictUpdateFill(metaObject, "updateby", Date.class, principal.getUserId());
 }
}
