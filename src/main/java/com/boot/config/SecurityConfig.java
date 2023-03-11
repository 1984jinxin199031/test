package com.boot.config;

import com.boot.security.filter.LoginKaptchaFilter;
import com.boot.security.rememberMe.MyPersistentTokenBasedRememberMeServices;
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
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //记住我令牌保存的数据源
    @Autowired
    private DataSource dataSource;
    //session和redis连通
//    @Autowired
//    private FindByIndexNameSessionRepository findByIndexNameSessionRepository;

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
        loginKaptchaFilter.setRememberMeServices(rememberMeServices()); //设置认证成功时使用自定义rememberMeService
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
                //.mvcMatchers("/index").rememberMe()  //指定资源记住我
                .anyRequest().authenticated()//所有请求必须认证
                .and()
                .formLogin()
                .and()
                .rememberMe() //开启记住我功能  cookie 进行实现  1.认证成功保存记住我 cookie 到客户端   2.只有 cookie 写入客户端成功才能实现自动登录功能
//                .tokenRepository(persistentTokenRepository())//持久化令牌，下面rememberMeServices已经设置完持久化cookie令牌了
                .rememberMeServices(rememberMeServices())  //设置自动登录使用哪个 rememberMeServices
                //.rememberMeParameter("remember-me") 用来接收请求中哪个参数作为开启记住我的参数
                //.alwaysRemember(true) //总是记住我
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((req, resp, ex) -> {//未登录(认证)时响应的json
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
                .logoutSuccessHandler((req, resp, auth) -> {//注销成功响应的json
                    Map<String, Object> result = new HashMap<>();
                    result.put("msg", "注销成功");
//                    result.put("用户信息", auth.getPrincipal());
                    resp.setContentType("application/json;charset=UTF-8");
                    resp.setStatus(HttpStatus.OK.value());
                    String s = new ObjectMapper().writeValueAsString(result);
                    resp.getWriter().println(s);
                })
                .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())//将令牌保存到 cookie 中 允许 cookie 前端获取
                .and()
                .sessionManagement()//开启登录会话管理
                .maximumSessions(1)//允许登录会话最大并发只能一个客户端;
                .expiredSessionStrategy(event -> { //登录会话失效显示的json
                    HttpServletResponse response = event.getResponse();
                    Map<String, Object> result = new HashMap<>();
                    result.put("status", 500);
                    result.put("msg", "当前会话已经失效,请重新登录!");
                    String s = new ObjectMapper().writeValueAsString(result);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().println(s);
                    response.flushBuffer();
                })
//                .sessionRegistry(sessionRegistry()) //将 session 交给谁管理(redis) 有BUG
                .maxSessionsPreventsLogin(true);//一旦登录禁止再次登录，除非用户注销


        // at: 用来某个 filter 替换过滤器链中哪个 filter
        // before: 放在过滤器链中哪个 filter 之前
        // after: 放在过滤器链中那个 filter 之后
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * 指定数据库持久化
     * @return
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        jdbcTokenRepository.setCreateTableOnStartup(false);//启动创建表结构，第一次用时设置未true
        return jdbcTokenRepository;
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        return new MyPersistentTokenBasedRememberMeServices(UUID.randomUUID().toString(), userDetailsService(), persistentTokenRepository());
    }

    //创建 session 同步到 redis 中方案
//    @Bean
//    public SpringSessionBackedSessionRegistry sessionRegistry() {
//        return new SpringSessionBackedSessionRegistry(findByIndexNameSessionRepository);
//    }
}
