package cn.jiangzhou.devkit.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {

    UserDetails loadUserById(Long id) throws UsernameNotFoundException;

    UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException;

}
