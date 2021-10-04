package cn.jiangzhou.devkit.security.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface TokenService {

    String generateToken(UserDetails userDetails);

    Boolean validateToken(String token, UserDetails userDetails);

    Boolean isTokenExpired(String token);

    Integer getUserIdFromToken(String token);

    Date getExpirationDateFromToken(String token);

}