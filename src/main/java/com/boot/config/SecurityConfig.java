package com.boot.config;

import com.boot.security.filter.LoginKaptchaFilter;
import com.boot.service.MyUserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MyUserDetailService myUserDetailService;

    @Autowired
    public SecurityConfig(MyUserDetailService myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailService);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //自定义 filter 交给工厂管理
    @Bean
    public LoginKaptchaFilter loginFilter() throws Exception {
        LoginKaptchaFilter loginKaptchaFilter = new LoginKaptchaFilter();
        loginKaptchaFilter.setFilterProcessesUrl("/doLogin");//指定认证 url
        loginKaptchaFilter.setUsernameParameter("uname");//指定接收json 用户名 key
        loginKaptchaFilter.setPasswordParameter("passwd");//指定接收 json 密码 key
        loginKaptchaFilter.setKaptchaParameter("kaptcha");//验证码 key
        loginKaptchaFilter.setAuthenticationManager(authenticationManagerBean());
        //认证成功处理
        loginKaptchaFilter.setAuthenticationSuccessHandler((req, resp, authentication) -> {
            Map<String, Object> result = new HashMap<>();
            result.put("msg", "登录成功");
            result.put("用户信息", authentication.getPrincipal());
            resp.setContentType("application/json;charset=UTF-8");
            resp.setStatus(HttpStatus.OK.value());
            String s = new ObjectMapper().writeValueAsString(result);
            resp.getWriter().println(s);
        });
        //认证失败处理
        loginKaptchaFilter.setAuthenticationFailureHandler((req, resp, ex) -> {
            Map<String, Object> result = new HashMap<>();
            result.put("msg", "登录失败: " + ex.getMessage());
            resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            resp.setContentType("application/json;charset=UTF-8");
            String s = new ObjectMapper().writeValueAsString(result);
            resp.getWriter().println(s);
        });
        return loginKaptchaFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/vc.jpg").permitAll()
                .anyRequest().authenticated()//所有请求必须认证
                .and()
                .formLogin()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((req, resp, ex) -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("msg", "请登录!");
                    resp.setContentType("application/json;charset=UTF-8");
                    resp.setStatus(HttpStatus.UNAUTHORIZED.value());
                    String s = new ObjectMapper().writeValueAsString(result);
                    resp.getWriter().println(s);
                })
                .and()
                .logout()
                .logoutRequestMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/logout", HttpMethod.DELETE.name()),
                        new AntPathRequestMatcher("/logout", HttpMethod.GET.name())
                ))
                .logoutSuccessHandler((req, resp, auth) -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("msg", "注销成功");
                    result.put("用户信息", auth.getPrincipal());
                    resp.setContentType("application/json;charset=UTF-8");
                    resp.setStatus(HttpStatus.OK.value());
                    String s = new ObjectMapper().writeValueAsString(result);
                    resp.getWriter().println(s);
                })
                .and()
                .csrf().disable();


        // at: 用来某个 filter 替换过滤器链中哪个 filter
        // before: 放在过滤器链中哪个 filter 之前
        // after: 放在过滤器链中那个 filter 之后
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
