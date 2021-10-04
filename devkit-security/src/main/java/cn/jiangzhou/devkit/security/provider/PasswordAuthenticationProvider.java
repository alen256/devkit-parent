package cn.jiangzhou.devkit.security.provider;

import cn.hutool.core.util.StrUtil;
import cn.jiangzhou.devkit.security.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取手机号
        String phone = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        if (StrUtil.isEmpty(phone) || StrUtil.isEmpty(password)) {
            throw new AuthenticationServiceException("缺少手机号或密码参数");
        }
        // 根据手机号从数据库中查询用户信息
        UserDetails user = this.userDetailsService.loadUserByPhone(phone);
        if (user == null) {
            // 未查询到用户信息，抛出异常
            throw new AuthenticationServiceException("手机号未注册");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationServiceException("用户名或密码错误");
        }
        // 查询到了用户信息，则认证通过，构建标记认证成功用户信息类对象 AuthenticationToken
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(user, user.getAuthorities());
        // 需要把认证前 Authentication 对象中的 details 信息加入认证后的 Authentication
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
