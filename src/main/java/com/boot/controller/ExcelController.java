package com.boot.controller;

import com.boot.util.excel.User;
import com.boot.util.ExcelUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("excel")
public class ExcelController {
    @PostMapping("/import")
    public void importUser(@RequestPart("file")MultipartFile file) throws Exception {
        List<User> users = ExcelUtils.readMultipartFile(file, User.class);
        for (User user : users) {
            System.out.println(user.toString());
        }
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        //数据来源接口 或 数据库
        // 表头数据
        List<Object> head = Arrays.asList("姓名","性别","生日");
        // 用户1数据
        List<Object> user1 = new ArrayList<>();
        user1.add("诸葛亮");
        user1.add("男");
        user1.add("1999.1.4");
        // 用户2数据
        List<Object> user2 = new ArrayList<>();
        user2.add("大乔");
        user2.add("女");
        user2.add("1925.1.4");
        // 将数据汇总
        List<List<Object>> sheetDataList = new ArrayList<>();
        sheetDataList.add(head);
        sheetDataList.add(user1);
        sheetDataList.add(user2);
        // 导出数据
        ExcelUtils.export(response,"用户表", sheetDataList);
    }
}
