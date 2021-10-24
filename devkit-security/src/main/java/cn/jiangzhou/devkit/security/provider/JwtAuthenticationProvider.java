package cn.jiangzhou.devkit.security.provider;

import cn.hutool.core.util.StrUtil;
import cn.jiangzhou.devkit.security.bean.JwtAuthenticationToken;
import cn.jiangzhou.devkit.security.service.TokenService;
import cn.jiangzhou.devkit.security.service.UserDetailsService;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            String jwt = (String) authentication.getPrincipal();
            if (StrUtil.isEmpty(jwt)) {
                throw new AuthenticationServiceException("缺少Token");
            }
            if (tokenService.isTokenExpired(jwt)) {
                throw new AuthenticationServiceException("Token已过期");
            }
            Long id = tokenService.getUserIdFromToken(jwt);
            UserDetails user = this.userDetailsService.loadUserById(id);
            if (user == null) {
                throw new AuthenticationServiceException("用户不存在");
            }

            // 查询到了用户信息，则认证通过，构建标记认证成功用户信息类对象 AuthenticationToken
            JwtAuthenticationToken result = new JwtAuthenticationToken(user, user.getAuthorities());
            // 需要把认证前 Authentication 对象中的 details 信息加入认证后的 Authentication
            result.setDetails(authentication.getDetails());
            return result;
        }
        catch (JwtException e) {
            throw new AuthenticationServiceException("Token无效");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
