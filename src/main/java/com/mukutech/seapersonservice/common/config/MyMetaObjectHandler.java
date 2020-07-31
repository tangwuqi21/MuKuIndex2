package com.mukutech.seapersonservice.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createBy", Long.class, 10L);
        this.strictInsertFill(metaObject, "createDate", Date.class, new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateDate", null);
        metaObject.setValue("updateBy", null);
        this.strictUpdateFill(metaObject, "updateDate", Date.class, new Date());
        this.strictUpdateFill(metaObject, "updateBy", Long.class, 10L);
    }
}
