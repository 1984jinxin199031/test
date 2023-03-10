package com.boot.security.filter;

import com.boot.security.filter.exception.KaptchaNotMatchException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

//自定义 filter
public class LoginKaptchaFilter extends UsernamePasswordAuthenticationFilter {

    public static final String FORM_KAPTCHA_KEY = "kaptcha";

    private String kaptchaParameter = FORM_KAPTCHA_KEY;

    public String getKaptchaParameter() {
        return kaptchaParameter;
    }

    public void setKaptchaParameter(String kaptchaParameter) {
        this.kaptchaParameter = kaptchaParameter;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //1.判断是否是 post 方式请求
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        try {
            if (request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {//2.判断是否是 json 格式请求类型
                //3.从 json 数据中获取用户输入用户名和密码进行认证 {"uname":"xxx","password":"xxx","remember-me":true}
                //1.获取请求数据
                Map<String, String> userInfo = new ObjectMapper().readValue(request.getInputStream(), Map.class);
                String kaptcha = userInfo.get(getKaptchaParameter());//用来获取数据中验证码
                String username = userInfo.get(getUsernameParameter());//用来接收用户名
                String password = userInfo.get(getPasswordParameter());//用来接收密码
                String rememberValue = userInfo.get(AbstractRememberMeServices.DEFAULT_PARAMETER);//用默认的rememberme表单值
                if (!ObjectUtils.isEmpty(rememberValue)) {
                    request.setAttribute(AbstractRememberMeServices.DEFAULT_PARAMETER, rememberValue);
                }
                System.out.println("用户名：" + username + "，密码：" + password + "，验证码：" + kaptcha + "，是否记住我: " + rememberValue);
                //2.获取 session 中验证码
                String sessionVerifyCode = (String) request.getSession().getAttribute("kaptcha");
                if (!ObjectUtils.isEmpty(kaptcha) && !ObjectUtils.isEmpty(sessionVerifyCode) &&
                        kaptcha.equalsIgnoreCase(sessionVerifyCode)) {
                    //3.获取用户名 和密码认证
                    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
                    setDetails(request, authRequest);
                    return this.getAuthenticationManager().authenticate(authRequest);
                }
            }
        } catch (IOException e) {


            e.printStackTrace();
        }
        throw new KaptchaNotMatchException("验证码不匹配!");
    }
}
