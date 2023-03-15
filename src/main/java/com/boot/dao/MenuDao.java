package com.boot.dao;

import com.boot.domain.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuDao {
    List<Menu> findByPattern(String url);
    List<Menu> getAllMenu();
}