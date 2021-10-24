package cn.jiangzhou.devkit.security.filter;

import cn.jiangzhou.devkit.bean.base.BaseResult;
import cn.jiangzhou.devkit.security.bean.MessageAuthenticationToken;
import cn.jiangzhou.devkit.security.bean.OAuthAuthenticationToken;
import cn.jiangzhou.devkit.security.exception.UnknownAuthenticationMethodException;
import cn.jiangzhou.devkit.security.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String TYPE_KEY = "type";
    public static final String PHONE_KEY = "phone";
    public static final String PASSWORD_KEY = "password";
    public static final String MESSAGE_CODE_KEY = "messageCode";
    public static final String OAUTH_TYPE_KEY = "oauthType";
    public static final String OAUTH_CODE_KEY = "oauthCode";

    private final ObjectMapper mapper = new ObjectMapper();

    public CustomAuthenticationFilter() {
        super(new AntPathRequestMatcher("/api/token", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        Map json = mapper.readValue(request.getInputStream(), Map.class);
        String type = getParam(json, TYPE_KEY);

        AbstractAuthenticationToken authentication;
        switch (type) {
            case "password":
                authentication = buildPasswordToken(json);
                break;
            case "message":
                authentication = buildMessageToken(json);
                break;
            case "oauth":
                authentication = buildOAuthToken(json);
                break;
            default:
                throw new UnknownAuthenticationMethodException();
        }

        setDetails(request, authentication);

        return this.getAuthenticationManager().authenticate(authentication);
    }

    private AbstractAuthenticationToken buildPasswordToken(Map json) {
        return new UsernamePasswordAuthenticationToken(getParam(json, PHONE_KEY), getParam(json, PASSWORD_KEY));
    }

    private AbstractAuthenticationToken buildMessageToken(Map json) {
        return new MessageAuthenticationToken(getParam(json, PHONE_KEY), getParam(json, MESSAGE_CODE_KEY));
    }

    private AbstractAuthenticationToken buildOAuthToken(Map json) {
        return new OAuthAuthenticationToken(getParam(json, OAUTH_TYPE_KEY), getParam(json, OAUTH_CODE_KEY));
    }

    private void setDetails(HttpServletRequest request, AbstractAuthenticationToken token) {
        token.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    private String getParam(Map json, String key) {
        String param = (String) json.get(key);
        if (param == null) {
            param = "";
        }
        return param.trim();
    }

    @Autowired
    private TokenService tokenService;

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("用户认证成功:" + authResult.getName());
        handleResponse(response, 200, "操作成功", tokenService.generateToken((UserDetails) authResult.getPrincipal()));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("用户认证失败");
        handleResponse(response, 10000, failed.getLocalizedMessage(), null);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private void handleResponse(HttpServletResponse response, int code, String msg, Object data) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(BaseResult.wrap(code, msg, data)));
    }

}
