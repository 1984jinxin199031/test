package com.boot.util.excel;

import com.boot.util.ExcelImport;

public class User {

    @ExcelImport("姓名")
    private String name;
    @ExcelImport("性别")
    private String sex;
    @ExcelImport("生日")
    private String birthday;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
