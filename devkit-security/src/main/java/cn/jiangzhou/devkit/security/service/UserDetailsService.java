package cn.jiangzhou.devkit.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {

    UserDetails loadUserById(Integer id) throws UsernameNotFoundException;

    UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException;

    UserDetails loadUserByOpenId(String openId) throws UsernameNotFoundException;


}
