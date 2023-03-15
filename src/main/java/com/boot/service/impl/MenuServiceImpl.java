package com.boot.service.impl;

import com.boot.dao.MenuDao;
import com.boot.domain.Menu;
import com.boot.domain.Role;
import com.boot.service.MenuService;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class MenuServiceImpl implements MenuService {

    private final MenuDao menuDao;
    private final RedisTemplate redisTemplate;
    public MenuServiceImpl(MenuDao menuDao, RedisTemplate redisTemplate) {
        this.menuDao = menuDao;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Menu> findByUrl(String url) {
        return menuDao.findByPattern(url);
    }

    @Override
    public List<String> getAllMenu() {
        boolean menusRedisFlag = redisTemplate.hasKey("menus");
        if (!menusRedisFlag) {
            List<Menu> menusDao = menuDao.getAllMenu();
            BoundListOperations menusBoundListOperations = redisTemplate.boundListOps("menus");
            for (Menu menu: menusDao) {
                menusBoundListOperations.leftPush(menu.getPattern());
                BoundListOperations boundListOperations = redisTemplate.boundListOps(menu.getPattern());
                List<Role> roles = menu.getRoles();
                for (Role role: roles) {
                   boundListOperations.leftPush(role.getName());
                }
            }

        }
        return redisTemplate.boundListOps("menus").range(0, -1);
    }

    @Override
    public String[] getRoleByPattern(String pattern) {
        List<String> list = (List<String>) redisTemplate.boundListOps(pattern).range(0, -1);
        return list.toArray(new String[0]);
    }
}
