package com.boot.controller;

import com.boot.util.QRCodeUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("qr")
public class QrCodeController{
 
    private static final String RootPath="d:\\QRCode";
    private static final String FileFormat=".png";
 
    private static final ThreadLocal<SimpleDateFormat> LOCALDATEFORMAT=ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmss"));

    //生成二维码并将其存放于本地目录
    @PostMapping("v1")
    public void generateV1(String content){
        try {
            final String fileName=LOCALDATEFORMAT.get().format(new Date());
            QRCodeUtil.createCodeToFile(content,new File(RootPath),fileName+FileFormat);
        }catch (Exception e){
        }
    }
 
    //生成二维码并将其返回给前端调用者
    @PostMapping("v2")
    public void generateV2(String content, HttpServletResponse servletResponse){
        try {
            QRCodeUtil.createCodeToOutputStream(content,servletResponse.getOutputStream());
 
        }catch (Exception e){
        }
    }
}