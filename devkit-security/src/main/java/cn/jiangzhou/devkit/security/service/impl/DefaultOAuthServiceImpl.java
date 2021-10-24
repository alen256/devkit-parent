package cn.jiangzhou.devkit.security.service.impl;

import cn.jiangzhou.devkit.security.service.OAuthService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@ConditionalOnBean(OAuthService.class)
@Service
public class DefaultOAuthServiceImpl implements OAuthService {
    @Override
    public Long auth(String type, String code) {
        return null;
    }
}
