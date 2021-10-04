package cn.jiangzhou.devkit.bean.base;

import lombok.Getter;

@Getter
public enum ApiCode {

    UN_IMPLEMENT(99999, "接口未完成"),
    PERMISSION_DENIED(10001, "无权限"),
    ALREADY_EXISTS(10002, "已存在"),
    PARAM_NOT_EQUAL(10003, "参数不相同"),
    MODEL_IS_NULL(10004, "模型不存在"),
    DEVICE_NOT_NULL(10005, "设备不存在"),
    AREA_NOT_NULL(10006, "区域不存在"),
    PARAM_INCOMPLETE(10007, "缺少参数"),
    MEMBER_NOT_NULL(10008, "成员不存在"),
    POINT_NOT_NULL(10009, "测点不存在"),
    PARAM_NOT_NULL(10010, "参数不存在"),
    RULE_NOT_NULL(10011, "告警规则不存在"),
    MODEL_NOT_NULL(10012, "告警规则不存在"),

    USER_NOT_VERIFICATION(20001, "用户验证不通过"),
    PASSWORD_IS_ERROR(20002, "密码不正确"),
    PHONE_NOT_NULL(20003, "手机号为空"),
    PHONE_ALREADY_EXIST(20004, "手机号已被绑定"),
    USER_NOT_NULL(20005, "用户不存在"),
    OPENID_IS_BINDING(20006, "第三方账号已经被绑定"),
    OPENID_IS_NULL(20007, "第三方账号不存在"),




    ;
    private final Integer code;

    private final String msg;

    ApiCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
