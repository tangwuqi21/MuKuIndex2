package com.mukutech.seapersonservice.common.enums;

import lombok.Getter;

@Getter
public enum ExceptionEnum {
    UPDATE_FAIL(400, "编辑失败"),
    INSERT_FAIL(400, "公告/事项保存失败"),
    INSER_DATAVISIBILITY_FAIL(400, "设置可见性失败"),
    FALSE_DATATYPE(400, "错误的附件所属对象"),
    FALSE_DELETE_NOTICE(400, "公告删除失败"),
    FALSE_UPDATE_NOTICE(400, "公告更新失败"),
    FALSE_DELETE_DATAVISIBILITY(400, "可见性删除失败"),
    FALSE_GET_ID(400, "所属ID不能为空"),
    FAIL_GET_DATAVISIBILITY(400, "获取用户可见性失败"),

    FALSE_GET_DATAVISIBILITY(400, "id不能为空，获取可见性失败"),
    THE_FILENAME_ERROR(400, "文件名错误"),

    SIGNATURE_DOES_NOT_EXIST(400, "签名无效"),
    ERROR_UPLOADING_ATTACHMENT(400, "上传附件错误"),

    ERROR_ACCOUNT_PASSWORD(500, "账号或者密码错误"),

    ERROR_RPC_SERVICE(500, "远程服务调用失败");

    private int status;
    private String message;

    ExceptionEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
