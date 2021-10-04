package cn.jiangzhou.devkit.security.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.core.authority.AuthorityUtils;
import cn.jiangzhou.devkit.security.service.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p> JwtTokenTemplate </p>
 * <p> Description : JwtTokenTemplate </p>
 * <p> Author : qianmoQ </p>
 * <p> Version : 1.0 </p>
 * <p> Create Time : 2019-11-26 20:49 </p>
 * <p> Author Email: <a href="mailTo:shichengoooo@163.com">qianmoQ</a> </p>
 */
@ConditionalOnBean(TokenService.class)
@Component
public class JwtTokenServiceImpl implements TokenService, Serializable {

    private static final String CLAIM_KEY_USER_ID = "sub";

    private static final String CLAIM_KEY_ROLE = "role";

    private static final long EXPIRATION_TIME = 432000000;

    private static final String SECRET = "secret";

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put(CLAIM_KEY_USER_ID, userDetails.getUsername());
        claims.put(CLAIM_KEY_ROLE, AuthorityUtils.authorityListToSet(userDetails.getAuthorities()));
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(Instant.now().toEpochMilli() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        Integer userId = getUserIdFromToken(token);
        return (userDetails.getUsername().equals(String.valueOf(userId)) && !isTokenExpired(token));
    }

    public Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Integer getUserIdFromToken(String token) {
        return Integer.parseInt(getClaimsFromToken(token).getSubject());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

}