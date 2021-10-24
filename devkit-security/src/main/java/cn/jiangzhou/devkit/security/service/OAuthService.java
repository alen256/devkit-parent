package cn.jiangzhou.devkit.security.service;

public interface OAuthService {

    Long auth(String type, String code);
    
}
