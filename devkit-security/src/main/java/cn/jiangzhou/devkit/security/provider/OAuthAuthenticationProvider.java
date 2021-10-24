package cn.jiangzhou.devkit.security.provider;

import cn.hutool.core.util.StrUtil;
import cn.jiangzhou.devkit.security.bean.OAuthAuthenticationToken;
import cn.jiangzhou.devkit.security.service.OAuthService;
import cn.jiangzhou.devkit.security.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class OAuthAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private OAuthService oAuthService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String type = (String) authentication.getPrincipal();
        String code = (String) authentication.getCredentials();
        if (StrUtil.isEmpty(type) || StrUtil.isEmpty(code)) {
            throw new AuthenticationServiceException("缺少OAuthType或OAuthCode");
        }
        Long id = oAuthService.auth(type, code);
        if (id == null) {
            throw new AuthenticationServiceException("登陆失败");
        }
        UserDetails user = this.userDetailsService.loadUserById(id);
        if (user == null) {
            // 未查询到用户信息，脏数据，抛出异常。关联关系异常，需要手工检查。
            throw new AuthenticationServiceException("用户数据异常");
        }

        // 查询到了用户信息，则认证通过，构建标记认证成功用户信息类对象 AuthenticationToken
        OAuthAuthenticationToken result = new OAuthAuthenticationToken(user, user.getAuthorities());
        // 需要把认证前 Authentication 对象中的 details 信息加入认证后的 Authentication
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuthAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
