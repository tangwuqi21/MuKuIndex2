package com.rhdk.purchasingservice.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.rhdk.purchasingservice.common.utils.TokenUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

 @Override
 public void insertFill(MetaObject metaObject) {
  this.strictInsertFill(
          metaObject, "createBy", Long.class, TokenUtil.getUserInfo().getUserId());
 }

 @Override
 public void updateFill(MetaObject metaObject) {
  this.strictUpdateFill(metaObject, "updateDate", Date.class, new Date());
  this.strictUpdateFill(metaObject, "updateBy", Long.class, TokenUtil.getUserInfo().getUserId());
 }
}
