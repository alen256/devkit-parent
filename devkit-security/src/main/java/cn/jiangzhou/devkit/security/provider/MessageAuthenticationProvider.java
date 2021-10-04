package cn.jiangzhou.devkit.security.provider;

import cn.hutool.core.util.StrUtil;
import cn.jiangzhou.devkit.bean.AppConst;
import cn.jiangzhou.devkit.security.bean.MessageAuthenticationToken;
import cn.jiangzhou.devkit.security.service.CacheService;
import cn.jiangzhou.devkit.security.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class MessageAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private CacheService cacheService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取手机号
        String phone = (String) authentication.getPrincipal();
        String code = (String) authentication.getCredentials();
        if (StrUtil.isEmpty(phone) || StrUtil.isEmpty(code)) {
            throw new AuthenticationServiceException("缺少手机号或验证码参数");
        }
        String rightCode = cacheService.get(AppConst.CACHE_NS_MESSAGE_CODE, phone);
        if (!code.equals(rightCode)) {
            throw new AuthenticationServiceException("验证码错误");
        }
        // 根据手机号从数据库中查询用户信息
        UserDetails user = this.userDetailsService.loadUserByPhone(phone);
        if (user == null) {
            // 未查询到用户信息，抛出异常
            // TODO 改为自动注册
            throw new AuthenticationServiceException("手机号未注册");
        }

        // 查询到了用户信息，则认证通过，构建标记认证成功用户信息类对象 AuthenticationToken
        MessageAuthenticationToken result = new MessageAuthenticationToken(user, user.getAuthorities());
        // 需要把认证前 Authentication 对象中的 details 信息加入认证后的 Authentication
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MessageAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
