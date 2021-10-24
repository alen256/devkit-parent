package cn.jiangzhou.devkit.security.service.impl;

import cn.jiangzhou.devkit.security.service.UserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@ConditionalOnBean(UserDetailsService.class)
@Service
public class DefaultUserDetailsServiceImpl implements UserDetailsService {

    private User getUser() {
        return new User("0", "$2a$10$7xkMHAzbOmgGY.irt8yas.YVf7B.ggTqku54IlqYsJonHbi9YKFpG",
                Collections.singletonList(new SimpleGrantedAuthority("USER")));
    }

    @Override
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        return getUser();
    }

    @Override
    public UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException {
        return getUser();
    }
}
