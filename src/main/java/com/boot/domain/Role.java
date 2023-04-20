package com.boot.domain;

import java.io.Serializable;

//角色类
public class Role implements Serializable {
    private Integer id;
    private String name;
    private String namechn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameZh() {
        return namechn;
    }

    public void setNameZh(String nameZh) {
        this.namechn = nameZh;
    }
}