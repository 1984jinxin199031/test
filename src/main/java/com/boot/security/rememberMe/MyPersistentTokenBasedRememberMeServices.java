package com.boot.security.rememberMe;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.servlet.http.HttpServletRequest;


/**
 * 自定义记住我 services 实现类
 */
public class MyPersistentTokenBasedRememberMeServices extends PersistentTokenBasedRememberMeServices {


    public MyPersistentTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
    }


    /**
     * 自定义前后端分离获取 remember-me 方式
     *
     * @param request
     * @param parameter
     * @return
     */
    @Override
    protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
        Object paramValue = request.getAttribute(parameter);
        if (paramValue != null) {
            String value = paramValue.toString();
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on")
                    || value.equalsIgnoreCase("yes") || value.equals("1")) {
                return true;
            }
        }
        return false;
    }
}
