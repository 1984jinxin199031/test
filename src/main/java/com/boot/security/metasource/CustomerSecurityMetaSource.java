package com.boot.security.metasource;

import com.boot.domain.Menu;
import com.boot.domain.Role;
import com.boot.service.MenuService;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

@Component
public class CustomerSecurityMetaSource implements FilterInvocationSecurityMetadataSource {

    private final MenuService menuService;


    AntPathMatcher antPathMatcher = new AntPathMatcher();

    public CustomerSecurityMetaSource(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * 自定义动态资源权限元数据信息
     * @param object the object being secured
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        //当前请求对象
        String requestURI = ((FilterInvocation) object).getRequest().getRequestURI();

        //2.查询所有菜单
        List<String> allMenu = menuService.getAllMenu();
        for (String menu : allMenu) {
            if (antPathMatcher.match(menu, requestURI)) {
                String[] roles = menuService.getRoleByPattern(menu);
                return SecurityConfig.createList(roles);
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
