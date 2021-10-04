package cn.jiangzhou.devkit.security.config;

import cn.jiangzhou.devkit.security.filter.CustomAuthenticationFilter;
import cn.jiangzhou.devkit.security.filter.JwtTokenFilter;
import cn.jiangzhou.devkit.security.handler.AuthenticationFailedEntryPoint;
import cn.jiangzhou.devkit.security.handler.CustomAccessDeniedHandler;
import cn.jiangzhou.devkit.security.provider.JwtAuthenticationProvider;
import cn.jiangzhou.devkit.security.provider.MessageAuthenticationProvider;
import cn.jiangzhou.devkit.security.provider.OAuthAuthenticationProvider;
import cn.jiangzhou.devkit.security.provider.PasswordAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MessageAuthenticationProvider messageAuthenticationProvider;

    @Autowired
    private OAuthAuthenticationProvider oAuthAuthenticationProvider;

    @Autowired
    private PasswordAuthenticationProvider passwordAuthenticationProvider;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private AuthenticationFailedEntryPoint authenticationFailedEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(messageAuthenticationProvider);
        auth.authenticationProvider(oAuthAuthenticationProvider);
        auth.authenticationProvider(passwordAuthenticationProvider);
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter() throws Exception {
        return new JwtTokenFilter(authenticationManagerBean());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                // 当用户无权访问资源时发送 401 响应
                .authenticationEntryPoint(authenticationFailedEntryPoint)
                // 当用户访问资源因权限不足时发送 403 响应
                .accessDeniedHandler(customAccessDeniedHandler)
                .and()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                //允许认证相关接口
                .antMatchers("/code/**").permitAll()
//                .antMatchers("/projects**").hasAnyRole("USER")
                .anyRequest().authenticated()
        ;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/v2/api-docs")
                .antMatchers("/v3/api-docs")
                .antMatchers("/webjars/springfox-swagger-ui/**")
                .antMatchers("/swagger-ui/")
                .antMatchers("/swagger-ui/**")
                .antMatchers("/swagger-resources/**");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
