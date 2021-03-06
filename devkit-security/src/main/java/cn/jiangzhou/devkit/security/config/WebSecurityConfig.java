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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(DevkitSecurityProperty.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private DevkitSecurityProperty devkitSecurityProperty;

    @Resource
    private MessageAuthenticationProvider messageAuthenticationProvider;

    @Resource
    private OAuthAuthenticationProvider oAuthAuthenticationProvider;

    @Resource
    private PasswordAuthenticationProvider passwordAuthenticationProvider;

    @Resource
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Resource
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Resource
    private AuthenticationFailedEntryPoint authenticationFailedEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        if (devkitSecurityProperty.getEnableMethods().getPassword()) {
            auth.authenticationProvider(passwordAuthenticationProvider);
        }
        if (devkitSecurityProperty.getEnableMethods().getMessage()) {
            auth.authenticationProvider(messageAuthenticationProvider);
        }
        if (devkitSecurityProperty.getEnableMethods().getOauth()) {
            auth.authenticationProvider(oAuthAuthenticationProvider);
        }
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
                // ???????????????????????????????????? 401 ??????
                .authenticationEntryPoint(authenticationFailedEntryPoint)
                // ????????????????????????????????????????????? 403 ??????
                .accessDeniedHandler(customAccessDeniedHandler)
                .and()
                .headers().frameOptions().disable();
        for (DevkitSecurityProperty.Authorize authorize : devkitSecurityProperty.getAuthorizes()) {
            if (authorize.getPermitAll()) {
                http.authorizeRequests()
                        .antMatchers(authorize.getPatterns()).permitAll();
            } else {
                http.authorizeRequests()
                        .antMatchers(authorize.getPatterns()).hasAnyRole(authorize.getRoles());
            }
        }
        http.authorizeRequests().anyRequest().authenticated();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**");
        if (devkitSecurityProperty.getSwagger()) {

            web.ignoring().antMatchers("/v2/api-docs")
                    .antMatchers("/v3/api-docs")
                    .antMatchers("/webjars/springfox-swagger-ui/**")
                    .antMatchers("/swagger-ui/")
                    .antMatchers("/swagger-ui/**")
                    .antMatchers("/swagger-resources/**");
        }
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
