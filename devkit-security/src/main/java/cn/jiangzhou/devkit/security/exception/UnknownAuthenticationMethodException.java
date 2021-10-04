package cn.jiangzhou.devkit.security.exception;

import org.springframework.security.core.AuthenticationException;

public class UnknownAuthenticationMethodException extends AuthenticationException {

    public UnknownAuthenticationMethodException() {
        super("不支持的认证方法");
    }

}
