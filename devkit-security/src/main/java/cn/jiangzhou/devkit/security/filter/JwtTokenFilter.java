package cn.jiangzhou.devkit.security.filter;

import cn.jiangzhou.devkit.bean.base.BaseResult;
import cn.jiangzhou.devkit.security.bean.JwtAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {

    public static final String HEADER_KEY = "Authorization";

    public static final String TOKEN_PREFIX = "Bearer ";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AuthenticationManager authenticationManager;

    public JwtTokenFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 判断是否有 token，并且进行认证
        String header = request.getHeader(HEADER_KEY);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        String jwt = header.split(" ")[1];
        JwtAuthenticationToken authRequest = new JwtAuthenticationToken(jwt);
        try {
            Authentication authResult = this.authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authResult);
            chain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(BaseResult.wrap(HttpStatus.UNAUTHORIZED.value(), e.getLocalizedMessage())));
        }
    }
}