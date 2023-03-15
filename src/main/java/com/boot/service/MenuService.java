package com.boot.service;

import com.boot.domain.Menu;

import java.util.List;

public interface MenuService {

    public List<Menu> findByUrl(String url);

    public List<String> getAllMenu();

    public String[] getRoleByPattern(String pattern);
}
